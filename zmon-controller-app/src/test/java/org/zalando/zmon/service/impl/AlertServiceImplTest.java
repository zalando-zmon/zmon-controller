package org.zalando.zmon.service.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zalando.zmon.domain.Alert;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.persistence.AlertDefinitionSProcService;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.util.NamedMessageFormatter;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.zalando.zmon.domain.DefinitionStatus.ACTIVE;
import static org.zalando.zmon.event.ZMonEventType.ALERT_ACKNOWLEDGED;
import static org.zalando.zmon.redis.RedisPattern.*;

@RunWith(MockitoJUnitRunner.class)
public class AlertServiceImplTest {
    @Mock private JedisPool writeRedisPool;
    @Mock private JedisPool redisPool;
    @Mock private Jedis jedis;
    @Mock private NoOpEventLog eventLog;
    @Mock private DefaultZMonPermissionService authorityService;
    @Mock private AlertDefinitionSProcService alertDefinitionSProc;
    @Mock private ObjectMapper mapper;
    @Mock private NamedMessageFormatter messageFormatter;
    @Mock private MetricRegistry metricRegistry;
    @Mock private Timer timer;

    @InjectMocks
    private AlertServiceImpl alertService;

    @Before
    public void setUp() throws Exception {
        when(writeRedisPool.getResource()).thenReturn(jedis);
        when(redisPool.getResource()).thenReturn(jedis);
        when(messageFormatter.format(anyString(), any())).thenReturn("test-msg");
        when(metricRegistry.timer(anyString())).thenReturn(timer);
        when(timer.time()).thenReturn(mock(Timer.Context.class));
    }

    @Test
    public void testGettingAckedAlerts() throws Exception {
        final List<AlertDefinition> testAlertDefs = ImmutableList.of(mockAlertDefinition(42), mockAlertDefinition(43));
        when(jedis.smembers(REDIS_ALERT_ACKS_PREFIX)).thenReturn(ImmutableSet.of("42", "08", "15"));
        when(alertDefinitionSProc.getAlertDefinitions(eq(ACTIVE), anyListOf(Integer.class))).thenReturn(testAlertDefs);

        final Pipeline pipeline = mock(Pipeline.class);
        when(jedis.pipelined()).thenReturn(pipeline);
        final Response<Set<String>> smembersResp = new Response<>(BuilderFactory.STRING_SET);
        smembersResp.set(ImmutableList.of("test-entity".getBytes()));
        when(pipeline.smembers(alertEntities(anyInt()))).thenReturn(smembersResp);

        final Response<String> getResp = new Response<>(BuilderFactory.STRING);
        getResp.set("true".getBytes());
        when(pipeline.get(alertResult(anyInt(), "test-entity"))).thenReturn(getResp);
        when(mapper.readTree(anyString())).thenReturn(new TextNode("test"));
        List<Alert> alerts = alertService.fetchAlertsById(ImmutableSet.of(42, 43));
        assertNotNull(alerts);
        assertThat(alerts, hasSize(2));
        assertThat(alerts.get(0).isNotificationsAck(), is(true));
        assertThat(alerts.get(1).isNotificationsAck(), is(false));

        when(alertDefinitionSProc.getAlertDefinitionsByTeamAndTag(eq(ACTIVE), anyListOf(String.class),
                anyListOf(String.class))).thenReturn(testAlertDefs);
        alerts = alertService.getAllAlertsByTeamAndTag(ImmutableSet.of("test-team"), ImmutableSet.of());
        assertThat(alerts, hasSize(2));
        assertThat(alerts.get(0).isNotificationsAck(), is(true));
        assertThat(alerts.get(1).isNotificationsAck(), is(false));
    }

    private AlertDefinition mockAlertDefinition(final int id) {
        final AlertDefinition alertDefinition = new AlertDefinition();
        alertDefinition.setId(id);
        alertDefinition.setName(String.format("alert #%d", id));
        return alertDefinition;
    }

    @Test
    public void acknowledgeAlert() throws Exception {
        when(authorityService.getUserName()).thenReturn("johndoe");
        alertService.acknowledgeAlert(42);
        verify(jedis).sadd(eq(REDIS_ALERT_ACKS_PREFIX), eq("42"));
        verify(eventLog).log(eq(ALERT_ACKNOWLEDGED), eq(42), eq("johndoe"));
    }

    @Test
    public void testGettingAckedAlertIds() throws Exception {
        when(jedis.smembers(REDIS_ALERT_ACKS_PREFIX)).thenReturn(ImmutableSet.of("42", "08", "15"));
        final Set<Integer> acknowledgedAlerts = alertService.getAcknowledgedAlerts();
        assertThat(acknowledgedAlerts, instanceOf(HashSet.class));
        assertThat(acknowledgedAlerts, hasItems(42, 8, 15));
    }

    @Test
    public void testFailingToGetAckedAlertIds() throws Exception {
        when(jedis.smembers(REDIS_ALERT_ACKS_PREFIX)).thenThrow(new RuntimeException("uh...oh"));
        final Set<Integer> acknowledgedAlerts = alertService.getAcknowledgedAlerts();
        assertThat(acknowledgedAlerts, empty());
    }

    @Test
    public void testCleanup() throws Exception {
        alertService.cleanAlertState(42);
        verify(jedis).del(alertEntities(42));
        verify(jedis).del(alertFilterEntities(42));
        verify(jedis).srem(REDIS_ALERT_ACKS_PREFIX, "42");
    }
}