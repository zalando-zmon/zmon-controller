package org.zalando.zmon.service.impl;

import java.util.*;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.domain.*;
import org.zalando.zmon.generator.AlertDefinitionGenerator;
import org.zalando.zmon.generator.CheckDefinitionImportGenerator;
import org.zalando.zmon.generator.DataGenerator;
import org.zalando.zmon.generator.RandomDataGenerator;
import org.zalando.zmon.persistence.CheckDefinitionImportResult;
import org.zalando.zmon.persistence.CheckDefinitionSProcService;
import org.zalando.zmon.service.AlertService;
import org.zalando.zmon.service.HistoryService;
import org.zalando.zmon.service.ZMonService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {"zmon.checkruntime.enabled=true"})
@Transactional
@DirtiesContext
public class HistoryServiceImplIT {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private ZMonService service;

    @Autowired
    private CheckDefinitionSProcService sProcService;

    private RandomDataGenerator<CheckDefinitionImport> checkImportGenerator;
    private DataGenerator<AlertDefinition> alertGenerator;

    private static final String USER_NAME ="default_user";
    private static final List<String> USER_TEAMS = Arrays.asList("Platform/Software");

    private CheckDefinition createNewCheckDefinition() {
        return service.createOrUpdateCheckDefinition(checkImportGenerator.generate(), USER_NAME, USER_TEAMS).getEntity();
    }

    @Before
    public void setup() {
        checkImportGenerator = new CheckDefinitionImportGenerator();
        alertGenerator = new AlertDefinitionGenerator();
    }

    @Test
    public void testGetCheckDefinitionHistory() throws Exception {

        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        // TODO test history pagination
        // TODO improve this test: check each field
        final List<ActivityDiff> history = historyService.getCheckDefinitionHistory(newCheckDefinition.getId(), 10,
                null, null);

        MatcherAssert.assertThat(history, Matchers.hasSize(1));
    }

    @Test
    public void testGetAlertDefinitionHistory() throws Exception {

        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition newAlertDefinition = alertGenerator.generate();
        newAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        newAlertDefinition = alertService.createOrUpdateAlertDefinition(newAlertDefinition);

        // TODO test history pagination
        // TODO improve this test: check each field
        final List<ActivityDiff> history = historyService.getAlertDefinitionHistory(newAlertDefinition.getId(), 10,
                null, null);

        MatcherAssert.assertThat(history, Matchers.hasSize(1));
    }

    @Test
    public void testCheckDefinitionIsRestored() {
        CheckDefinitionImport previousCheckDefinitionImport = new CheckDefinitionImport();
        previousCheckDefinitionImport.setName("Previous name");
        previousCheckDefinitionImport.setDescription("Previous description");
        previousCheckDefinitionImport.setOwningTeam("Previous team");
        previousCheckDefinitionImport.setEntities(Collections.singletonList(Collections.singletonMap("type", "previous_type")));
        previousCheckDefinitionImport.setInterval(42L);
        previousCheckDefinitionImport.setCommand("Previous command");
        previousCheckDefinitionImport.setSourceUrl("https://previous.url.com");
        previousCheckDefinitionImport.setStatus(DefinitionStatus.ACTIVE);
        previousCheckDefinitionImport.setRuntime(DefinitionRuntime.PYTHON_2);
        previousCheckDefinitionImport.setTechnicalDetails("Previous technical details");
        previousCheckDefinitionImport.setPotentialAnalysis("Previous potential analysis");
        previousCheckDefinitionImport.setPotentialImpact("Previous potential impact");
        previousCheckDefinitionImport.setPotentialSolution("Previous potential solution");
        previousCheckDefinitionImport.setLastModifiedBy(USER_NAME);
        CheckDefinition previousCheckDefinition = sProcService.createOrUpdateCheckDefinition(previousCheckDefinitionImport, USER_NAME, USER_TEAMS, true, false, DefinitionRuntime.PYTHON_2).getEntity();

        CheckDefinitionImport newCheckDefinitionImport = new CheckDefinitionImport();
        newCheckDefinitionImport.setId(previousCheckDefinition.getId());
        newCheckDefinitionImport.setName("New name");
        newCheckDefinitionImport.setDescription("New description");
        newCheckDefinitionImport.setOwningTeam("New team");
        newCheckDefinitionImport.setEntities(Collections.singletonList(Collections.singletonMap("type", "new_type")));
        newCheckDefinitionImport.setInterval(66L);
        newCheckDefinitionImport.setCommand("New command");
        newCheckDefinitionImport.setSourceUrl("https://new.url.com");
        newCheckDefinitionImport.setStatus(DefinitionStatus.DELETED);
        newCheckDefinitionImport.setRuntime(DefinitionRuntime.PYTHON_3);
        newCheckDefinitionImport.setTechnicalDetails("New technical details");
        newCheckDefinitionImport.setPotentialAnalysis("New potential analysis");
        newCheckDefinitionImport.setPotentialImpact("New potential impact");
        newCheckDefinitionImport.setPotentialSolution("New potential solution");
        CheckDefinitionImportResult result = service.createOrUpdateCheckDefinition(newCheckDefinitionImport, USER_NAME, USER_TEAMS, true);
        int insertHistoryId = (int) historyService.getCheckDefinitionHistory(previousCheckDefinition.getId(), null, null,
                null, HistoryAction.INSERT).get(0).getHistoryId();
        boolean isRestored = historyService.restoreCheckDefinition(insertHistoryId, USER_NAME, USER_TEAMS, true);

        MatcherAssert.assertThat(result.isPermissionDenied(), Matchers.is(false));
        MatcherAssert.assertThat(isRestored, Matchers.is(true));
        MatcherAssert.assertThat(
                service.getCheckDefinitionById(previousCheckDefinition.getId()).orElse(null),
                CheckDefinitionIsEqual.equalTo(previousCheckDefinition)
        );
    }
}
