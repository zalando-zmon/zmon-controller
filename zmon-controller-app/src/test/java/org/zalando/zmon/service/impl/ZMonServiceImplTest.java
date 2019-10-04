package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.util.Lists;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.zmon.api.domain.AlertResult;
import org.zalando.zmon.config.CheckRuntimeConfig;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.domain.Alert;
import org.zalando.zmon.domain.AlertDefinitionAuth;
import org.zalando.zmon.domain.LastCheckResult;
import org.zalando.zmon.persistence.AlertDefinitionSProcService;
import org.zalando.zmon.persistence.CheckDefinitionSProcService;
import org.zalando.zmon.persistence.EntitySProcService;
import org.zalando.zmon.persistence.ZMonSProcService;
import org.zalando.zmon.service.AlertService;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class ZMonServiceImplTest {

    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @Mock
    private AlertService alertService;

    @Autowired
    protected CheckDefinitionSProcService checkDefinitionSProc;
    @Autowired
    protected AlertDefinitionSProcService alertDefinitionSProc;
    @Autowired
    protected ZMonSProcService zmonSProc;
    @Autowired
    protected EntitySProcService entitySProc;
    @Autowired
    protected JedisPool redisPool;
    @Autowired
    private NoOpEventLog eventLog;
    @Autowired
    private CheckRuntimeConfig checkRuntimeConfig;
    @Autowired
    private ControllerProperties config;

    private ZMonServiceImpl service;

    @Before
    public void setUp() {
        service = new ZMonServiceImpl(checkDefinitionSProc, alertDefinitionSProc, zmonSProc, entitySProc, redisPool, new ObjectMapper(), eventLog, checkRuntimeConfig, config, alertService);
    }

    @Test
    public void parseAlertCoverageShouldReturnEmptyListIfJsonNodeIsNullTest() {
        List<ZMonServiceImpl.EntityGroup> alertCoverage = service.parseAlertCoverage(null);

        MatcherAssert.assertThat(alertCoverage, IsNull.notNullValue());
        MatcherAssert.assertThat(alertCoverage.size(), Matchers.is(0));
    }

    @Test
    public void parseAlertCoverageShouldReturnEmptyListIfCannotParse() {
        ObjectNode node = factory.objectNode();
        node.put("unknown-field", "value");

        List<ZMonServiceImpl.EntityGroup> alertCoverage = service.parseAlertCoverage(node);

        MatcherAssert.assertThat(alertCoverage, IsNull.notNullValue());
        MatcherAssert.assertThat(alertCoverage.size(), Matchers.is(0));
    }

    @Test
    public void parseAlertCoverageShouldReturnParsedAlertCoverage() {
        ArrayNode root = factory.arrayNode();
        ObjectNode entityGroup1 = factory.objectNode();

        ArrayNode entities1 = factory.arrayNode();
        ObjectNode entity = factory.objectNode();
        entity.put("id", "pod_123");
        entity.put("type", "pod");
        entities1.add(entity);

        ArrayNode alerts1 = factory.arrayNode();
        ObjectNode alert = factory.objectNode();
        alert.put("name", "test-alert");
        alert.put("id", 1);
        alerts1.add(alert);

        entityGroup1.set("entities", entities1);
        entityGroup1.set("alerts", alerts1);

        root.add(entityGroup1);

        List<ZMonServiceImpl.EntityGroup> alertCoverage = service.parseAlertCoverage(root);

        MatcherAssert.assertThat(alertCoverage, IsNull.notNullValue());
        MatcherAssert.assertThat(alertCoverage.size(), Matchers.is(1));

        MatcherAssert.assertThat(alertCoverage.get(0).alerts.size(), Matchers.is(1));
        MatcherAssert.assertThat(alertCoverage.get(0).alerts.get(0).name, Matchers.is("test-alert"));
        MatcherAssert.assertThat(alertCoverage.get(0).alerts.get(0).id, Matchers.is(1));

        MatcherAssert.assertThat(alertCoverage.get(0).entities.size(), Matchers.is(1));
        MatcherAssert.assertThat(alertCoverage.get(0).entities.get(0).id, Matchers.is("pod_123"));
        MatcherAssert.assertThat(alertCoverage.get(0).entities.get(0).type, Matchers.is("pod"));
    }

    @Test
    public void createAlertResultsShouldReturnEmptyListIfAlertCoverageIsNotAvailable() {
        List<AlertResult> alertResults = service.createAlertResults(Lists.emptyList(), null);

        MatcherAssert.assertThat(alertResults, IsNull.notNullValue());
        MatcherAssert.assertThat(alertResults.size(), Matchers.is(0));
    }

    @Test
    public void createAlertResults() {
        Map<Integer, Alert> alerts = new HashMap<>();
        alerts.put(1, alert(1, "ZMON is great", 1, "pod_1"));
        alerts.put(2, alert(2, "ZMON is hot", 2, "pod_b"));
        alerts.put(3, alert(3, "ZMON is hot", 3));

        List<ZMonServiceImpl.EntityGroup> alertCoverage = new LinkedList<>();

        ZMonServiceImpl.EntityGroup entityGroup1 = new ZMonServiceImpl.EntityGroup();
        entityGroup1.entities = Arrays.asList(entityInfo("pod_1"), entityInfo("pod_2"));
        entityGroup1.alerts = Collections.singletonList(alertInfo(1));

        ZMonServiceImpl.EntityGroup entityGroup2 = new ZMonServiceImpl.EntityGroup();
        entityGroup2.entities = Collections.singletonList(entityInfo("pod_a"));
        entityGroup2.alerts = Collections.singletonList(alertInfo(2));

        ZMonServiceImpl.EntityGroup entityGroup3 = new ZMonServiceImpl.EntityGroup();
        entityGroup3.entities = Collections.singletonList(entityInfo("pod_I"));
        entityGroup3.alerts = Collections.singletonList(alertInfo(3));

        alertCoverage.add(entityGroup1);
        alertCoverage.add(entityGroup2);
        alertCoverage.add(entityGroup3);

        List<AlertResult> alertResults = service.createAlertResults(alertCoverage, alerts);

        MatcherAssert.assertThat(alertResults, IsNull.notNullValue());
        MatcherAssert.assertThat(alertResults.size(), Matchers.is(4));

        MatcherAssert.assertThat(alertResults.get(0).getAlertDefinitionId(), Matchers.is("1"));
        MatcherAssert.assertThat(alertResults.get(0).getCheckDefinitionId(), Matchers.is("1"));
        MatcherAssert.assertThat(alertResults.get(0).getEntityId(), Matchers.is("pod_1"));
        MatcherAssert.assertThat(alertResults.get(0).getEntityType(), Matchers.is("pod"));
        MatcherAssert.assertThat(alertResults.get(0).getPriority(), Matchers.is("1"));
        MatcherAssert.assertThat(alertResults.get(0).getTitle(), Matchers.is("ZMON is great"));
        MatcherAssert.assertThat(alertResults.get(0).isTriggered(), Matchers.is(true));

        MatcherAssert.assertThat(alertResults.get(1).getAlertDefinitionId(), Matchers.is("1"));
        MatcherAssert.assertThat(alertResults.get(1).getCheckDefinitionId(), Matchers.is("1"));
        MatcherAssert.assertThat(alertResults.get(1).getEntityId(), Matchers.is("pod_2"));
        MatcherAssert.assertThat(alertResults.get(1).getEntityType(), Matchers.is("pod"));
        MatcherAssert.assertThat(alertResults.get(1).getPriority(), Matchers.is("1"));
        MatcherAssert.assertThat(alertResults.get(1).getTitle(), Matchers.is("ZMON is great"));
        MatcherAssert.assertThat(alertResults.get(1).isTriggered(), Matchers.is(false));

        MatcherAssert.assertThat(alertResults.get(2).getAlertDefinitionId(), Matchers.is("2"));
        MatcherAssert.assertThat(alertResults.get(2).getCheckDefinitionId(), Matchers.is("2"));
        MatcherAssert.assertThat(alertResults.get(2).getEntityId(), Matchers.is("pod_a"));
        MatcherAssert.assertThat(alertResults.get(2).getEntityType(), Matchers.is("pod"));
        MatcherAssert.assertThat(alertResults.get(2).getPriority(), Matchers.is("1"));
        MatcherAssert.assertThat(alertResults.get(2).getTitle(), Matchers.is("ZMON is hot"));
        MatcherAssert.assertThat(alertResults.get(2).isTriggered(), Matchers.is(false));

        MatcherAssert.assertThat(alertResults.get(3).getAlertDefinitionId(), Matchers.is("3"));
        MatcherAssert.assertThat(alertResults.get(3).getCheckDefinitionId(), Matchers.is("3"));
        MatcherAssert.assertThat(alertResults.get(3).getEntityId(), Matchers.is("pod_I"));
        MatcherAssert.assertThat(alertResults.get(3).getEntityType(), Matchers.is("pod"));
        MatcherAssert.assertThat(alertResults.get(3).getPriority(), Matchers.is("1"));
        MatcherAssert.assertThat(alertResults.get(3).getTitle(), Matchers.is("ZMON is hot"));
        MatcherAssert.assertThat(alertResults.get(3).isTriggered(), Matchers.is(false));
    }

    private ZMonServiceImpl.AlertInfo alertInfo(int id) {
        ZMonServiceImpl.AlertInfo alertInfo = new ZMonServiceImpl.AlertInfo();
        alertInfo.id = id;
        alertInfo.name = "some alert";
        return alertInfo;
    }

    private ZMonServiceImpl.EntityInfo entityInfo(String id) {
        ZMonServiceImpl.EntityInfo entityInfo = new ZMonServiceImpl.EntityInfo();
        entityInfo.id = id;
        entityInfo.type = "pod";
        return entityInfo;
    }

    private Alert alert(int id, String name, int definitionId, String... entities) {
        Alert alert = new Alert();
        AlertDefinitionAuth alertDef = new AlertDefinitionAuth();
        alertDef.setId(id);
        alertDef.setCheckDefinitionId(definitionId);
        alertDef.setName(name);
        alertDef.setPriority(1);
        alert.setAlertDefinition(alertDef);
        alert.setEntities(Arrays.stream(entities).map(e -> new LastCheckResult(e, null)).collect(Collectors.toList()));

        return alert;
    }
}
