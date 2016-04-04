package org.zalando.zmon.service.impl;

import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.util.Assert;
import org.zalando.zmon.event.ZMonEventType;
import org.zalando.zmon.redis.RedisPattern;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author jbellmann
 *
 */
public class ForceAlertEvaluation {

    private static final int INSTANTANEOUS_ALERT_EVALUATION_TIME = 300;

    private final RedisOperations<String, String> redisOperations;

    private final DefaultZMonPermissionService authorityService;

    private final NoOpEventLog eventLog;

    private final ObjectMapper objectMapper;

    public ForceAlertEvaluation(RedisOperations<String, String> redisOperations,
            DefaultZMonPermissionService authorityService) {

        Assert.notNull(redisOperations, "'redisOperations' should not be null");
        Assert.notNull(authorityService, "'authorityService' should not be null");

        this.redisOperations = redisOperations;
        this.authorityService = authorityService;

        //
        this.eventLog = new NoOpEventLog();
        this.objectMapper = new ObjectMapper();
    }

    public void forceAlertEvaluation(final int alertDefinitionId) {

        // generate id
        final String id = UUID.randomUUID().toString();

        // build the json
        final ObjectNode node = objectMapper.createObjectNode();
        node.put("alert_definition_id", alertDefinitionId);

        final String json = node.toString();

        redisOperations.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringConnection = (StringRedisConnection) connection;
                stringConnection.hSet(RedisPattern.alertEvaluationQueue(), id, json);
                stringConnection.expire(RedisPattern.alertEvaluationQueue(), INSTANTANEOUS_ALERT_EVALUATION_TIME);
                stringConnection.publish(RedisPattern.alertEvaluationQueue(), id);
                return null;
            }
        });

        eventLog.log(ZMonEventType.INSTANTANEOUS_ALERT_EVALUATION_SCHEDULED, alertDefinitionId,
                authorityService.getUserName());

        //
        // final Jedis jedis = redisWritePool.getResource();
        // try {
        // final Pipeline p = jedis.pipelined();
        // p.hset(RedisPattern.alertEvaluationQueue(), id, json);
        //
        // // expire queue. The scheduler might be down
        // p.expire(RedisPattern.alertEvaluationQueue(),
        // INSTANTANEOUS_ALERT_EVALUATION_TIME);
        // p.publish(RedisPattern.alertEvaluationChannel(), id);
        //
        //// NewRelicRedis.syncPipeline(p);
        // } finally {
        // redisWritePool.returnResource(jedis);
        // }

        // eventLog.log(ZMonEventType.INSTANTANEOUS_ALERT_EVALUATION_SCHEDULED,
        // alertDefinitionId,
        // authorityService.getUserName());
    }

}
