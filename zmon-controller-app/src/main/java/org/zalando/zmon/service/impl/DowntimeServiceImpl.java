package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.api.DowntimeGroup;
import org.zalando.zmon.config.SchedulerProperties;
import org.zalando.zmon.config.annotation.RedisWrite;
import org.zalando.zmon.domain.DefinitionStatus;
import org.zalando.zmon.domain.DowntimeDetails;
import org.zalando.zmon.domain.DowntimeEntities;
import org.zalando.zmon.domain.DowntimeRequest;
import org.zalando.zmon.exception.SerializationException;
import org.zalando.zmon.persistence.AlertDefinitionSProcService;
import org.zalando.zmon.redis.RedisPattern;
import org.zalando.zmon.redis.ResponseHolder;
import org.zalando.zmon.service.DowntimeService;
import org.zalando.zmon.service.impl.downtimes.DowntimeAPIRequest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DowntimeServiceImpl implements DowntimeService {

    private static final Logger LOG = LoggerFactory.getLogger(DowntimeServiceImpl.class);

    private final JedisPool redisPool;
    private final JedisPool writeRedisPool;
    private final AlertDefinitionSProcService alertDefinitionSProc;
    private final NoOpEventLog eventLog;
    private final ObjectMapper mapper;

    @Autowired
    private SchedulerProperties schedulerProperties;

    private static final String SCHEDULER_DOWNTIMES_PATH = "/api/v1/downtimes";
    private static final String SCHEDULER_DOWNTIME_GROUPS_PATH = "/api/v1/downtime-groups";

    @Autowired
    public DowntimeServiceImpl(final JedisPool redisPool, @RedisWrite JedisPool writeRedisPool,
                               final ObjectMapper mapper,
                               final AlertDefinitionSProcService alertDefinitionSProc, NoOpEventLog eventLog) {
        this.redisPool = Preconditions.checkNotNull(redisPool, "redisPool");
        this.writeRedisPool = Preconditions.checkNotNull(writeRedisPool, "writeRedisPool");
        this.mapper = Preconditions.checkNotNull(mapper, "mapper");
        this.alertDefinitionSProc = Preconditions.checkNotNull(alertDefinitionSProc, "alertDefinitionSProc");
        this.eventLog = eventLog;
    }

    @Override
    public DowntimeGroup scheduleDowntimeGroup(final DowntimeGroup group) {
        // well, not performant, but at least we don't need to touch python

        // load all active alert ids
        final Set<Integer> ids = group.getAlertDefinitions() == null
                ? ImmutableSet.copyOf(alertDefinitionSProc.getAlertIdsByStatus(DefinitionStatus.ACTIVE))
                : group.getAlertDefinitions();

        final Map<Integer, Response<Set<String>>> results = resolveEntities(ids);

        // filter entities
        final Set<Integer> alertDefinitionsInDowntime = new HashSet<>(results.size());
        final Set<String> entitiesInDowntime = new HashSet<>();
        final List<DowntimeEntities> requests = new LinkedList<>();
        for (final Map.Entry<Integer, Response<Set<String>>> entry : results.entrySet()) {
            final Set<String> entities = Sets.intersection(group.getEntities(), entry.getValue().get());
            if (!entities.isEmpty()) {
                alertDefinitionsInDowntime.add(entry.getKey());
                entitiesInDowntime.addAll(entities);

                final DowntimeEntities downtimeEntities = new DowntimeEntities();
                downtimeEntities.setAlertDefinitionId(entry.getKey());
                downtimeEntities.setEntityIds(entities);
                requests.add(downtimeEntities);
            }
        }

        final String groupId = UUID.randomUUID().toString();
        doScheduleDowntime(group, groupId, requests);

        final DowntimeGroup response = new DowntimeGroup();
        response.setId(groupId);
        response.setStartTime(group.getStartTime());
        response.setEndTime(group.getEndTime());
        response.setComment(group.getComment());
        response.setCreatedBy(group.getCreatedBy());
        response.setAlertDefinitions(alertDefinitionsInDowntime);
        response.setEntities(entitiesInDowntime);

        return response;
    }

    private Map<Integer, Response<Set<String>>> resolveEntities(final Collection<Integer> ids) {
        final Map<Integer, Response<Set<String>>> results = Maps.newHashMapWithExpectedSize(ids.size());
        try (Jedis jedis = writeRedisPool.getResource()) {
            final Pipeline pipeline = jedis.pipelined();
            for (final Integer id : ids) {
                results.put(id, pipeline.hkeys(RedisPattern.alertFilterEntities(id)));
            }

            pipeline.sync();
        }

        return results;
    }

    private void doScheduleDowntime(final DowntimeGroup group, final String groupId,
                                    final List<DowntimeEntities> requests) {
        if (!requests.isEmpty()) {
            final DowntimeRequest request = new DowntimeRequest();
            request.setDowntimeEntities(requests);
            request.setEndTime(group.getEndTime());
            request.setComment(group.getComment());
            request.setCreatedBy(group.getCreatedBy());
            request.setStartTime(group.getStartTime());

            scheduleDowntime(request, groupId);
        }
    }

    @Override
    public List<String> scheduleDowntime(final DowntimeRequest request) {
        return scheduleDowntime(request, UUID.randomUUID().toString());
    }

    private List<String> scheduleDowntime(final DowntimeRequest request, final String groupId) {
        Preconditions.checkNotNull(request, "request");
        Preconditions.checkNotNull(groupId, "groupId");

        final Executor executor = Executor.newInstance(schedulerProperties.getHttpClient());
        final String url = schedulerProperties.getUrl().toString() + SCHEDULER_DOWNTIMES_PATH;

        final DowntimeAPIRequest apiRequest = DowntimeAPIRequest.convert(groupId, request);
        try {
            final Request httpRequest = Request.Post(url).bodyString(mapper.writeValueAsString(apiRequest),
                    ContentType.APPLICATION_JSON);
            executor.execute(httpRequest).returnContent().asString();
        } catch (Throwable t) {
            LOG.error("Creating downtime failed", t.getMessage());
            throw new RuntimeException(t);
        }

        return apiRequest.getDowntimeIds();
    }

    @Override
    public List<DowntimeDetails> getDowntimes(final Set<Integer> alertDefinitionIds) {

        final List<Response<Map<String, String>>> asyncDowntimeResults = new LinkedList<>();

        // only process results after returning the connection to the pool
        // we should hold the connection as less time as possible since we have a limited number of connections
        try (Jedis jedis = redisPool.getResource()) {
            final Set<Integer> alertIdsWithDowntime = Sets.intersection(alertDefinitionIds, alertsInDowntime(jedis));
            if (!alertIdsWithDowntime.isEmpty()) {
                final List<ResponseHolder<Integer, Set<String>>> asyncAlertResults = fetchEntities(jedis,
                        alertIdsWithDowntime);

                // execute async call to get all downtime data for each entity
                final Pipeline p = jedis.pipelined();
                for (final ResponseHolder<Integer, Set<String>> response : asyncAlertResults) {
                    for (final String entity : response.getResponse().get()) {
                        asyncDowntimeResults.add(p.hgetAll(RedisPattern.downtimeDetails(response.getKey(), entity)));
                    }
                }

                p.sync();
            }
        }

        // process results
        return processDowntimeResponses(asyncDowntimeResults);
    }

    private List<DowntimeDetails> processDowntimeResponses(final List<Response<Map<String, String>>> downtimeResults) {

        final List<DowntimeDetails> results = new LinkedList<>();
        for (final Response<Map<String, String>> response : downtimeResults) {
            for (final String entityResults : response.get().values()) {
                try {
                    results.add(mapper.readValue(entityResults, DowntimeDetails.class));
                } catch (final IOException e) {
                    throw new SerializationException("Could not read JSON: " + entityResults, e);
                }
            }
        }

        return results;
    }

    @Override
    public DowntimeGroup deleteDowntimeGroup(final String groupId) {
        Preconditions.checkNotNull(groupId, "groupId");

        final Executor executor = Executor.newInstance(schedulerProperties.getHttpClient());
        final String url = schedulerProperties.getUrl().toString() + SCHEDULER_DOWNTIME_GROUPS_PATH + "/" + groupId;

        try {
            executor.execute(Request.Delete(url)).returnContent().asString();
        } catch (Throwable t) {
            LOG.error("Deleting downtime group failed: group={}", groupId, t.getMessage());
            throw new RuntimeException(t);
        }

        return new DowntimeGroup();
    }

    @Override
    public void deleteDowntimes(final Set<String> downtimeIds) {
        Preconditions.checkNotNull(downtimeIds);

        final Executor executor = Executor.newInstance(schedulerProperties.getHttpClient());
        List<String> errorIds = new ArrayList<>();

        for (String downtimeId : downtimeIds) {
            String url = schedulerProperties.getUrl().toString() + SCHEDULER_DOWNTIMES_PATH + "/" + downtimeId;

            try {
                executor.execute(Request.Delete(url)).returnContent().asString();
            } catch (Throwable t) {
                LOG.error("Deleting downtime failed: id={}", downtimeId, t.getMessage());
                errorIds.add(downtimeId);
            }
        }

        if (!errorIds.isEmpty()) {
            throw new RuntimeException("Delete failed for " + errorIds.size() + " downtimes");
        }
    }

    private List<ResponseHolder<Integer, Set<String>>> fetchEntities(final Jedis jedis,
                                                                     final Iterable<Integer> alertIdsWithDowntime) {
        final List<ResponseHolder<Integer, Set<String>>> asyncAlertEntities = new LinkedList<>();

        final Pipeline p = jedis.pipelined();
        for (final Integer alertDefinitionId : alertIdsWithDowntime) {
            asyncAlertEntities.add(ResponseHolder.create(alertDefinitionId,
                    p.smembers(RedisPattern.downtimeEntities(alertDefinitionId))));
        }

        p.sync();

        return asyncAlertEntities;
    }

    private Set<Integer> alertsInDowntime(final Jedis jedis) {
        return jedis.smembers(RedisPattern.downtimeAlertIds()).stream().map(Integer::parseInt).collect(Collectors.toSet());
    }
}
