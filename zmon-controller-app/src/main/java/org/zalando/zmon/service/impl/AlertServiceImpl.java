package org.zalando.zmon.service.impl;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.config.SchedulerProperties;
import org.zalando.zmon.config.annotation.RedisWrite;
import org.zalando.zmon.diff.AlertDefinitionsDiffFactory;
import org.zalando.zmon.domain.*;
import org.zalando.zmon.event.ZMonEventType;
import org.zalando.zmon.exception.SerializationException;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.persistence.AlertDefinitionOperationResult;
import org.zalando.zmon.persistence.AlertDefinitionSProcService;
import org.zalando.zmon.redis.RedisPattern;
import org.zalando.zmon.redis.ResponseHolder;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.AlertService;
import org.zalando.zmon.util.DBUtil;
import org.zalando.zmon.util.NamedMessageFormatter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
import static org.zalando.zmon.event.ZMonEventType.ALERT_ACKNOWLEDGED;
import static org.zalando.zmon.redis.RedisPattern.REDIS_ALERT_ACKS_PREFIX;

@Service
@Transactional
public class AlertServiceImpl implements AlertService {

    private final Logger log = LoggerFactory.getLogger(AlertServiceImpl.class);

    private static final String ENTITIES_PLACEHOLDER = "entities";
    private static final String CAPTURES_KEY = "captures";
    private static final String DOWNTIMES_KEY = "downtimes";

    private final NamedMessageFormatter messageFormatter = new NamedMessageFormatter();
    private final long redisSMembersCount = parseRedisSmembersCount();

    @Autowired
    private NoOpEventLog eventLog;

    @Autowired
    protected AlertDefinitionSProcService alertDefinitionSProc;

    @Autowired
    protected DefaultZMonPermissionService authorityService;

    @Autowired
    protected MetricRegistry metricRegistry;

    @Autowired
    private JedisPool redisPool;

    @Autowired
    @RedisWrite
    private JedisPool writeRedisPool;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Override
    public AlertDefinition createOrUpdateAlertDefinition(final AlertDefinition alertDefinition) throws ZMonException {
        Preconditions.checkNotNull(alertDefinition);

        final AlertDefinitionOperationResult operationResult = alertDefinitionSProc.createOrUpdateAlertDefinitionTree(
                alertDefinition).throwExceptionOnFailure();
        final AlertDefinition result = operationResult.getEntity();

        // TODO save inherited data to eventlog, or the diff?
        eventLog.log(alertDefinition.getId() == null ? ZMonEventType.ALERT_DEFINITION_CREATED
                        : ZMonEventType.ALERT_DEFINITION_UPDATED, result.getId(),
                result.getEntities(), result.getCondition(), result.getLastModifiedBy());

        return result;
    }

    @Override
    public AlertDefinition deleteAlertDefinition(final int id) throws ZMonException {
        log.info("Deleting alert definition with id '{}'", id);

        final AlertDefinitionOperationResult operationResult = alertDefinitionSProc.deleteAlertDefinition(id)
                .throwExceptionOnFailure();
        final AlertDefinition alertDefinition = operationResult.getEntity();

        if (alertDefinition != null) {
            eventLog.log(ZMonEventType.ALERT_DEFINITION_DELETED, alertDefinition.getId(),
                    alertDefinition.getEntities(), alertDefinition.getCondition(), authorityService.getUserName());
        }

        return alertDefinition;
    }

    @Override
    public List<AlertDefinition> getAllAlertDefinitions() {
        return alertDefinitionSProc.getAllAlertDefinitions();
    }

    @Override
    public AlertDefinitions getActiveAlertDefinitionsDiff() {
        return alertDefinitionSProc.getActiveAlertDefinitionsDiff();
    }

    @Override
    public AlertDefinitionsDiff getAlertDefinitionsDiff(final Long snapshotId) {
        return AlertDefinitionsDiffFactory.create(alertDefinitionSProc.getAlertDefinitionsDiff(snapshotId));
    }

    @Override
    public List<AlertDefinition> getAlertDefinitions(final DefinitionStatus status,
                                                     final List<Integer> alertDefinitionIds) {
        Preconditions.checkNotNull(alertDefinitionIds, "alertDefinitionIds");

        return alertDefinitionSProc.getAlertDefinitions(status, alertDefinitionIds);
    }

    @Override
    public List<AlertDefinition> getAlertDefinitions(@Nullable final DefinitionStatus status, final Set<String> teams) {
        Preconditions.checkNotNull(teams, "teams");

        // SP doesn't support sets
        final List<String> teamList = teams.stream().map(DBUtil::prefix).collect(Collectors.toList());

        return alertDefinitionSProc.getAlertDefinitionsByTeam(status, teamList);
    }

    @Override
    public List<Alert> getAllAlerts() {
        return fetchAlertsById(getActiveAlertIds().stream().map(Integer::parseInt).collect(Collectors.toSet()));
    }

    @Override
    public List<Alert> getAllAlertsById(final Set<Integer> alertIdfilter) {
        Preconditions.checkNotNull(alertIdfilter, "alertIdfilter");

        List<Alert> alerts = Collections.emptyList();

        if (!alertIdfilter.isEmpty()) {

            // first extract the id of all active alerts
            final Set<String> redisAlertIds = getActiveAlertIds();

            // apply the filter
            final Set<Integer> alertIds = Sets.newHashSetWithExpectedSize(redisAlertIds.size());
            for (final String alertId : redisAlertIds) {
                final Integer alertIdValue = Integer.valueOf(alertId);
                if (alertIdfilter.contains(alertIdValue)) {
                    alertIds.add(alertIdValue);
                }
            }

            alerts = fetchAlertsById(alertIds);
        }

        return alerts;
    }

    protected Set<String> getActiveAlertIds() {
        try (Jedis jedis = redisPool.getResource()) {
            return jedis.smembers(RedisPattern.alertIds());
        }
    }

    protected void getAlertDataFromStorage(Set<Integer> ids, List<Integer> alertIds, List<ResponseHolder<Integer, Set<String>>> results) {
        // first extract everything and return the connection to the connection pool
        try (Jedis jedis = redisPool.getResource()) {

            // execute async call
            final Pipeline p = jedis.pipelined();
            for (final Integer alertId : ids) {
                alertIds.add(alertId);
                results.add(ResponseHolder.create(alertId, p.smembers(RedisPattern.alertEntities(alertId))));
            }

            p.sync();
        }
    }

    @VisibleForTesting
    List<Alert> fetchAlertsById(final Set<Integer> ids) {
        Preconditions.checkNotNull(ids, "ids");

        final List<Alert> alerts = new LinkedList<>();

        if (!ids.isEmpty()) {
            final List<Integer> alertIds = new LinkedList<>();
            final List<ResponseHolder<Integer, Set<String>>> results = new LinkedList<>();

            getAlertDataFromStorage(ids, alertIds, results);

            // process responses
            if (!alertIds.isEmpty()) {

                // get alert definitions from database
                final List<AlertDefinition> definitions = alertDefinitionSProc.getAlertDefinitions(
                        DefinitionStatus.ACTIVE, alertIds);
                if (!definitions.isEmpty()) {

                    // index all definitions
                    final Map<Integer, AlertDefinition> mappedAlerts = Maps.uniqueIndex(definitions,
                            AlertDefinition::getId);
                    final Set<Integer> ackedAlertIds = getAcknowledgedAlerts();
                    // process alerts
                    for (final ResponseHolder<Integer, Set<String>> entry : results) {
                        final AlertDefinition def = mappedAlerts.get(entry.getKey());
                        if (def != null) {
                            final Alert alert = buildAlert(entry.getKey(), entry.getResponse().get(), def);
                            alert.setNotificationsAck(ackedAlertIds.contains(alert.getAlertDefinition().getId()));
                            alerts.add(alert);
                        }
                    }
                }
            }
        }

        return alerts;
    }

    protected void getActiveAlertsForDefinitions(List<AlertDefinition> definitions, List<ResponseHolder<Integer, Set<String>>> results) {
        try (Jedis jedis = redisPool.getResource()) {

            // execute async call
            final Pipeline p = jedis.pipelined();
            for (final AlertDefinition definition : definitions) {
                results.add(ResponseHolder.create(definition.getId(),
                        p.smembers(RedisPattern.alertEntities(definition.getId()))));
            }

            p.sync();
        }
    }

    //Added to replace smembers call with scard to avoid pressure on redis during Black Friday period.
    protected void getActiveAlertsForDefinitionsWithoutEntities(List<AlertDefinition> definitions, List<ResponseHolder<Integer, Long>> results, List<ResponseHolder<Integer, Set<String>>> results1) {
        try (Jedis jedis = redisPool.getResource()) {

            // execute async call
            final Pipeline p = jedis.pipelined();
            for (final AlertDefinition definition : definitions) {

                Response<Long> response = p.scard(RedisPattern.alertEntities(definition.getId()));
                p.sync();
                Long count = response.get();

                if (count > redisSMembersCount)
                results.add(ResponseHolder.create(definition.getId(),
                        p.scard(RedisPattern.alertEntities(definition.getId()))));
                else
                    results1.add(ResponseHolder.create(definition.getId(),
                            p.smembers(RedisPattern.alertEntities(definition.getId()))));
            }

            p.sync();
        }
    }

    @Override
    public List<Alert> getAllAlertsByTeamAndTag(final Set<String> teams, final Set<String> tags) {

        final List<Alert> alerts = new LinkedList<>();

        if (teams != null && !teams.isEmpty() || tags != null && !tags.isEmpty()) {
            final List<AlertDefinition> definitions = getActiveAlertDefinitionByTeamAndTag(teams, tags);
            final List<ResponseHolder<Integer, Set<String>>> results = new LinkedList<>();

            if (!definitions.isEmpty()) {

                getActiveAlertsForDefinitions(definitions, results);

                // TODO remove duplicate code
                // TODO move redis calls to a different service
                // TODO abstract connection management with command pattern/template
                // TODO remove ResponseHolder and use a Map (alerts should be ordered on the webpage)
                //
                // index all definitions
                final Map<Integer, AlertDefinition> mappedAlerts = Maps.uniqueIndex(definitions,
                        AlertDefinition::getId);
                final Set<Integer> ackedAlertIds = getAcknowledgedAlerts();
                // process alerts
                for (final ResponseHolder<Integer, Set<String>> entry : results) {
                    final AlertDefinition def = mappedAlerts.get(entry.getKey());

                    final Set<String> entities = entry.getResponse().get();
                    if (!entities.isEmpty()) {
                        final Alert alert = buildAlert(entry.getKey(), entities, def);
                        alert.setNotificationsAck(ackedAlertIds.contains(alert.getAlertDefinition().getId()));
                        alerts.add(alert);
                    }
                }
            }
        }

        return alerts;
    }

    @Override
    public List<Alert> getAllAlertsByTeamAndTagWithoutEntities(final Set<String> teams, final Set<String> tags) {

        final List<Alert> alerts = new LinkedList<>();

        if (teams != null && !teams.isEmpty() || tags != null && !tags.isEmpty()) {
            final List<AlertDefinition> definitions = getActiveAlertDefinitionByTeamAndTag(teams, tags);
            final List<ResponseHolder<Integer, Long>> resultsEntitiesCount = new LinkedList<>();
            final List<ResponseHolder<Integer, Set<String>>> resultsEntities = new LinkedList<>();

            if (!definitions.isEmpty()) {

                getActiveAlertsForDefinitionsWithoutEntities(definitions, resultsEntitiesCount, resultsEntities);

                // TODO remove duplicate code
                // TODO move redis calls to a different service
                // TODO abstract connection management with command pattern/template
                // TODO remove ResponseHolder and use a Map (alerts should be ordered on the webpage)
                //
                // index all definitions
                final Map<Integer, AlertDefinition> mappedAlerts = Maps.uniqueIndex(definitions,
                        AlertDefinition::getId);
                final Set<Integer> ackedAlertIds = getAcknowledgedAlerts();

                if(resultsEntitiesCount.size() > 0) {
                    // process alerts
                    for (final ResponseHolder<Integer, Long> entry : resultsEntitiesCount) {
                        final AlertDefinition def = mappedAlerts.get(entry.getKey());

                        final Long entities = entry.getResponse().get();

                        if (entities > 0) {
                            final Alert alert = buildAlertWithoutEntities(entry.getKey(), entities, def);
                            alert.setNotificationsAck(ackedAlertIds.contains(alert.getAlertDefinition().getId()));
                            alerts.add(alert);
                        }
                    }
                }
                if(resultsEntities.size() > 0) {
                    for (final ResponseHolder<Integer, Set<String>> entry : resultsEntities) {
                        final AlertDefinition def = mappedAlerts.get(entry.getKey());

                        final Set<String> entities = entry.getResponse().get();
                        if (!entities.isEmpty()) {
                            final Alert alert = buildAlert(entry.getKey(), entities, def);
                            alert.setNotificationsAck(ackedAlertIds.contains(alert.getAlertDefinition().getId()));
                            alerts.add(alert);
                        }
                    }
                }

            }
        }

        return alerts;
    }

    @VisibleForTesting
    Set<Integer> getAcknowledgedAlerts() {
        try (Jedis jedis = redisPool.getResource()) {
            final Set<String> ackedAlertIds = jedis.smembers(REDIS_ALERT_ACKS_PREFIX);
            return ackedAlertIds.stream().map(Integer::parseInt).collect(toCollection(HashSet::new));
        } catch (Exception ex) {
            log.error("Failed to load acknowledged alert ids", ex);
        }
        return ImmutableSet.of();
    }

    @Override
    public Alert getAlert(final int alertId) {

        // get alert definitions from database
        final List<AlertDefinition> definitions = alertDefinitionSProc.getAlertDefinitions(DefinitionStatus.ACTIVE,
                Collections.singletonList(alertId));

        Alert alertResult = null;
        if (!definitions.isEmpty()) {

            Set<String> alertEntities;
            try (Jedis jedis = redisPool.getResource()) {
                alertEntities = jedis.smembers(RedisPattern.alertEntities(alertId));
            }

            alertResult = buildAlert(alertId, alertEntities, definitions.get(0));

        }

        return alertResult;
    }

    @Override
    public AlertComment addComment(final AlertComment comment) throws ZMonException {
        Preconditions.checkNotNull(comment, "comment");
        log.info("Adding new comment to alert definition '{}'", comment.getAlertDefinitionId());

        final AlertComment result = this.alertDefinitionSProc.addAlertComment(comment).getEntity();

        eventLog.log(ZMonEventType.ALERT_COMMENT_CREATED, result.getId(), result.getComment(),
                result.getAlertDefinitionId(), result.getEntityId(), result.getCreatedBy());

        return result;
    }

    @Override
    public List<AlertComment> getComments(final int alertDefinitionId, final int limit, final int offset) {
        return alertDefinitionSProc.getAlertComments(alertDefinitionId, limit, offset);
    }

    @Override
    public void deleteAlertComment(final int id) {
        log.info("Deleting comment with id '{}'", id);

        final AlertComment comment = alertDefinitionSProc.deleteAlertComment(id);

        if (comment != null) {
            eventLog.log(ZMonEventType.ALERT_COMMENT_REMOVED, comment.getId(), comment.getComment(),
                    comment.getAlertDefinitionId(), comment.getEntityId(), comment.getCreatedBy());
        }
    }

    @Override
    public AlertDefinition getAlertDefinitionNode(final int alertDefinitionId) {
        return alertDefinitionSProc.getAlertDefinitionNode(alertDefinitionId);
    }

    @Override
    public List<AlertDefinition> getAlertDefinitionChildren(final int alertDefinitionId) {
        return alertDefinitionSProc.getAlertDefinitionChildren(alertDefinitionId);
    }

    @Override
    public void forceAlertEvaluation(final int alertDefinitionId) throws IOException {

        // TODO, use ForceAlertEvaluation

        final Executor executor = Executor.newInstance(schedulerProperties.getHttpClient());

        final String url = schedulerProperties.getUrl().toString() + "/api/v1/alerts/" + alertDefinitionId + "/instant-eval";

        executor.execute(Request.Post(url)).returnContent().asString();

        eventLog.log(ZMonEventType.INSTANTANEOUS_ALERT_EVALUATION_SCHEDULED, alertDefinitionId,
                authorityService.getUserName());
    }

    @Override
    public void cleanAlertState(int alertDefinitionId) {
        try (Jedis jedis = writeRedisPool.getResource()) {
            jedis.del(RedisPattern.alertEntities(alertDefinitionId));
            jedis.del(RedisPattern.alertFilterEntities(alertDefinitionId));
            jedis.srem(REDIS_ALERT_ACKS_PREFIX, Integer.toString(alertDefinitionId));
        }
    }

    @Override
    public void acknowledgeAlert(int alertId) {
        try (Jedis jedis = writeRedisPool.getResource()) {
            jedis.sadd(REDIS_ALERT_ACKS_PREFIX, Integer.toString(alertId));
            eventLog.log(ALERT_ACKNOWLEDGED, alertId, authorityService.getUserName());
        }
    }

    @Override
    public List<String> getAllTags() {
        return alertDefinitionSProc.getAllTags();
    }

    private List<AlertDefinition> getActiveAlertDefinitionByTeamAndTag(final Set<String> teams,
                                                                       final Set<String> tags) {

        List<String> teamList = null;
        if (teams != null) {

            // SP doesn't support sets
            teamList = new ArrayList<>(teams.size());
            for (final String team : teams) {
                teamList.add(DBUtil.expandExpression(team));
            }
        }

        List<String> tagList = null;
        if (tags != null) {

            // SP doesn't support sets
            tagList = new ArrayList<>(tags);
        }

        final Timer t = metricRegistry.timer("alert-service.get-alerts-by-team-and-tag");
        final Timer.Context c = t.time();

        try {
            // get alert definitions filtered by team from the database
            return alertDefinitionSProc.getAlertDefinitionsByTeamAndTag(DefinitionStatus.ACTIVE, teamList, tagList);
        }
        finally {
            c.stop();
        }
    }

    protected void getAlertEntityData(Integer alertId, Set<String> entities, List<ResponseHolder<String, String>> results) {
        try (Jedis jedis = redisPool.getResource()) {

            // execute async call
            final Pipeline p = jedis.pipelined();
            for (final String entity : entities) {
                results.add(ResponseHolder.create(entity, p.get(RedisPattern.alertResult(alertId, entity))));
            }

            p.sync();
        }
    }

    protected Alert buildAlert(final Integer alertId, final Set<String> entities, final AlertDefinition definition) {
        final List<LastCheckResult> checkResults = new LinkedList<>();

        if (!entities.isEmpty()) {
            final List<ResponseHolder<String, String>> results = new LinkedList<>();

            getAlertEntityData(alertId, entities, results);

            // process results
            for (final ResponseHolder<String, String> entry : results) {
                final String result = entry.getResponse().get();

                // alert result might not be there (worker updated might be in progress )
                if (result != null) {
                    try {
                        checkResults.add(new LastCheckResult(entry.getKey(), mapper.readTree(result)));
                    } catch (final IOException e) {
                        throw new SerializationException("Could not read JSON: " + result, e);
                    }
                }
            }
        }

        final Alert alert = new Alert();
        final AlertDefinitionAuth definitionAuth = AlertDefinitionAuth.from(definition,
                authorityService.hasEditAlertDefinitionPermission(definition),
                authorityService.hasAddAlertDefinitionPermission(),
                authorityService.hasDeleteAlertDefinitionPermission(definition));

        alert.setAlertDefinition(definitionAuth);
        alert.setEntities(checkResults);
        alert.setMessage(buildMessage(definition.getName(), checkResults));

        return alert;
    }

    protected Alert buildAlertWithoutEntities(final Integer alertId, final long entities, final AlertDefinition definition) {
        final List<LastCheckResult> checkResults = new LinkedList<>();

        if (entities > 0) {
            final List<ResponseHolder<String, String>> results = new LinkedList<>();
        }

        final Alert alert = new Alert();
        final AlertDefinitionAuth definitionAuth = AlertDefinitionAuth.from(definition,
                authorityService.hasEditAlertDefinitionPermission(definition),
                authorityService.hasAddAlertDefinitionPermission(),
                authorityService.hasDeleteAlertDefinitionPermission(definition));

        alert.setAlertDefinition(definitionAuth);
        alert.setEntitiesCount(entities);
        alert.setMessage(buildMessage(definition.getName(), checkResults));

        return alert;
    }


    private String buildMessage(final String template, final List<LastCheckResult> checkResults) {
        String result = template;

        if (!checkResults.isEmpty()) {
            final Set<String> entities = Sets.newHashSetWithExpectedSize(checkResults.size());
            final Map<String, Collection<String>> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (final LastCheckResult checkResult : checkResults) {
                final JsonNode node = checkResult.getResult().get(DOWNTIMES_KEY);

                // get captures from an entity not in downtime
                if (node == null || node.size() == 0) {
                    entities.add(checkResult.getEntity());

                    final JsonNode jsonNode = checkResult.getResult().get(CAPTURES_KEY);
                    if (jsonNode != null && jsonNode.isObject()) {
                        final Iterator<Map.Entry<String, JsonNode>> fieldIterator = jsonNode.fields();
                        while (fieldIterator.hasNext()) {
                            final Map.Entry<String, JsonNode> entry = fieldIterator.next();
                            Collection<String> values = parameters.get(entry.getKey());
                            if (values == null) {
                                values = new TreeSet<>();
                                parameters.put(entry.getKey(), values);
                            }

                            values.add(entry.getValue().toString());
                        }
                    }
                }
            }

            parameters.put(ENTITIES_PLACEHOLDER, entities);
            result = messageFormatter.format(result, parameters);
        }

        return result;
    }

    @Override
    public Date getMaxLastModified() {
        return alertDefinitionSProc.getAlertLastModifiedMax();
    }

    private static long parseRedisSmembersCount() {
        final String smembersCount = System.getenv("REDIS_SMEMBERS_COUNT");
        return smembersCount == null ? 1000 : Long.parseLong(smembersCount);
    }
}
