package org.zalando.zmon.service.impl;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.domain.ActivityDiff;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.generator.AlertDefinitionGenerator;
import org.zalando.zmon.generator.CheckDefinitionImportGenerator;
import org.zalando.zmon.generator.DataGenerator;
import org.zalando.zmon.service.AlertService;
import org.zalando.zmon.service.HistoryService;
import org.zalando.zmon.service.ZMonService;

@ContextConfiguration(classes = ServiceTestConfiguration.class)
@Transactional
@DirtiesContext
public class HistoryServiceImplIT extends AbstractServiceIntegrationTest {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private ZMonService service;

    private DataGenerator<CheckDefinitionImport> checkImportGenerator;
    private DataGenerator<AlertDefinition> alertGenerator;

    @Before
    public void setup() {
        checkImportGenerator = new CheckDefinitionImportGenerator();
        alertGenerator = new AlertDefinitionGenerator();
    }

    @Test
    public void testGetCheckDefinitionHistory() throws Exception {

        final CheckDefinition newCheckDefinition = service.createOrUpdateCheckDefinition(
                checkImportGenerator.generate());

        // TODO test history pagination
        // TODO improve this test: check each field
        final List<ActivityDiff> history = historyService.getCheckDefinitionHistory(newCheckDefinition.getId(), 10,
                null, null);

        MatcherAssert.assertThat(history, Matchers.hasSize(1));
    }

    @Test
    public void testGetAlertDefinitionHistory() throws Exception {

        final CheckDefinition newCheckDefinition = service.createOrUpdateCheckDefinition(
                checkImportGenerator.generate());

        AlertDefinition newAlertDefinition = alertGenerator.generate();
        newAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        newAlertDefinition = alertService.createOrUpdateAlertDefinition(newAlertDefinition);

        // TODO test history pagination
        // TODO improve this test: check each field
        final List<ActivityDiff> history = historyService.getAlertDefinitionHistory(newAlertDefinition.getId(), 10,
                null, null);

        MatcherAssert.assertThat(history, Matchers.hasSize(1));
    }
}
