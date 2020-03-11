package org.zalando.zmon.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.domain.AlertCommentImport;
import org.zalando.zmon.domain.AlertCommentIsEqual;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.AlertDefinitionIsEqual;
import org.zalando.zmon.domain.AlertDefinitionsDiff;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.DefinitionStatus;
import org.zalando.zmon.domain.Parameter;
import org.zalando.zmon.exception.AlertDefinitionNotFoundException;
import org.zalando.zmon.generator.AlertCommentGenerator;
import org.zalando.zmon.generator.AlertDefinitionGenerator;
import org.zalando.zmon.generator.CheckDefinitionImportGenerator;
import org.zalando.zmon.generator.DataGenerator;
import org.zalando.zmon.service.AlertService;
import org.zalando.zmon.service.ZMonService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

// TODO remove duplicate code in diff tests
// TODO test dst

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@DirtiesContext
public class AlertServiceImplIT {

    @Autowired
    private ZMonService service;

    @Autowired
    private AlertService alertService;

    private DataGenerator<CheckDefinitionImport> checkImportGenerator;
    private DataGenerator<AlertDefinition> alertGenerator;
    private DataGenerator<AlertCommentImport> commentGenerator;

    private static final String USER_NAME ="default_user";
    private static final List<String> USER_TEAMS = Arrays.asList("Platform/Software");

    @Before
    public void setup() {
        checkImportGenerator = new CheckDefinitionImportGenerator();
        alertGenerator = new AlertDefinitionGenerator();
        commentGenerator = new AlertCommentGenerator();
    }

    @Test
    public void testEmptyAlertDefinitions() {
        final List<AlertDefinition> alertDefinitions = alertService.getAllAlertDefinitions();

        MatcherAssert.assertThat(alertDefinitions, Matchers.hasSize(0));
    }

    private CheckDefinition createNewCheckDefinition() {
        return service.createOrUpdateCheckDefinition(checkImportGenerator.generate(), USER_NAME, USER_TEAMS).getEntity();
    }

    @Ignore
    @Test
    public void testCreateAlertDefinition() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        // create a new alert
        AlertDefinition newAlertDefinition = alertGenerator.generate();
        newAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        newAlertDefinition = alertService.createOrUpdateAlertDefinition(newAlertDefinition);

        final List<AlertDefinition> alertDefinitions = alertService.getAlertDefinitions(newAlertDefinition.getStatus(), Lists.newArrayList(newAlertDefinition.getId()));

        MatcherAssert.assertThat(alertDefinitions,
            Matchers.contains(AlertDefinitionIsEqual.equalTo(newAlertDefinition)));
    }

    @Test
    public void testCreateTemplate() throws Exception {
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        // create a template with minimum fields.
        AlertDefinition template = new AlertDefinition();
        template.setTemplate(true);
        template.setLastModifiedBy("pribeiro");
        template.setCheckDefinitionId(newCheckDefinition.getId());
        template.setStatus(DefinitionStatus.ACTIVE);
        template.setTeam("Platform/System");
        template.setResponsibleTeam("Platform/Database");

        template = alertService.createOrUpdateAlertDefinition(template);

        // workaround: SP is returning empty list instead of null in entities filter/notifications
        template.setEntities(null);
        template.setEntitiesExclude(null);
        template.setNotifications(null);
        template.setTags(null);

        final List<AlertDefinition> alertDefinitions = alertService.getAllAlertDefinitions();
        MatcherAssert.assertThat(alertDefinitions, Matchers.contains(AlertDefinitionIsEqual.equalTo(template)));
    }

    @Test
    public void testUpdateTemplate() throws Exception {
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        // create a template with minimum fields.
        AlertDefinition template = new AlertDefinition();
        template.setTemplate(true);
        template.setLastModifiedBy("pribeiro");
        template.setCheckDefinitionId(newCheckDefinition.getId());
        template.setStatus(DefinitionStatus.ACTIVE);
        template.setTeam("Platform/System");
        template.setResponsibleTeam("Platform/Database");

        template.setId(alertService.createOrUpdateAlertDefinition(template).getId());

        // update name
        template.setName(template.getName() + " UPDATE");
        template = alertService.createOrUpdateAlertDefinition(template);

        // workaround: SP is returning empty list instead of null in entities filter/notifications
        template.setEntities(null);
        template.setEntitiesExclude(null);
        template.setNotifications(null);
        template.setTags(null);

        MatcherAssert.assertThat(alertService.getAllAlertDefinitions(),
            Matchers.contains(AlertDefinitionIsEqual.equalTo(template)));
    }

    @Ignore
    @Test
    public void testExtendAlertDefinition() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        final Parameter param0 = new Parameter(0, "desc 0", "int");
        final Parameter param1 = new Parameter(1, "desc 1", "float");
        final Parameter param2 = new Parameter("2", "desc 2", "double");
        final Parameter param3 = new Parameter(3, "desc 3", "short");

        // create a new alert
        AlertDefinition rootAlertDefinition = alertGenerator.generate();
        rootAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        rootAlertDefinition.setParameters(ImmutableMap.of("param0", param0, "param1", param1));
        rootAlertDefinition = alertService.createOrUpdateAlertDefinition(rootAlertDefinition);

        final AlertDefinition extendedAlertDefinition = new AlertDefinition();
        extendedAlertDefinition.setParentId(rootAlertDefinition.getId());
        extendedAlertDefinition.setTemplate(false);
        extendedAlertDefinition.setLastModifiedBy("pribeiro");
        extendedAlertDefinition.setStatus(DefinitionStatus.ACTIVE);
        extendedAlertDefinition.setTeam("Platform/System");
        extendedAlertDefinition.setResponsibleTeam("Platform/Database");
        extendedAlertDefinition.setParameters(ImmutableMap.of("param1", param3, "param2", param2));
        extendedAlertDefinition.setTags(ImmutableList.of("PM", "QA"));

        final AlertDefinition persistedAlertDefinition = alertService.createOrUpdateAlertDefinition(
                extendedAlertDefinition);

        // set inherited fields for comparison
        extendedAlertDefinition.setParameters(ImmutableMap.of("param0", param0, "param1", param3, "param2", param2));

        extendedAlertDefinition.setId(persistedAlertDefinition.getId());
        extendedAlertDefinition.setLastModified(persistedAlertDefinition.getLastModified());

        extendedAlertDefinition.setName(rootAlertDefinition.getName());
        extendedAlertDefinition.setDescription(rootAlertDefinition.getDescription());
        extendedAlertDefinition.setEntities(rootAlertDefinition.getEntities());
        extendedAlertDefinition.setEntitiesExclude(rootAlertDefinition.getEntitiesExclude());
        extendedAlertDefinition.setCondition(rootAlertDefinition.getCondition());
        extendedAlertDefinition.setNotifications(rootAlertDefinition.getNotifications());
        extendedAlertDefinition.setPriority(rootAlertDefinition.getPriority());
        extendedAlertDefinition.setPeriod(rootAlertDefinition.getPeriod());
        extendedAlertDefinition.setCheckDefinitionId(rootAlertDefinition.getCheckDefinitionId());

        MatcherAssert.assertThat(alertService.getAllAlertDefinitions(),
            Matchers.containsInAnyOrder(
                AlertDefinitionIsEqual.equalTo(ImmutableList.of(rootAlertDefinition, extendedAlertDefinition))));
    }

    @Ignore
    @Test
    public void testUpdateAlertDefinition() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        // create alert number 0
        final AlertDefinition genAlertDefinition = alertGenerator.generate();
        genAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());

        final AlertDefinition newAlertDefinition0 = alertService.createOrUpdateAlertDefinition(genAlertDefinition);

        // create alert number 1
        genAlertDefinition.setName(genAlertDefinition.getName() + " UPDATE");

        AlertDefinition newAlertDefinition1 = alertService.createOrUpdateAlertDefinition(genAlertDefinition);

        // update previous alert
        newAlertDefinition1.setName(genAlertDefinition.getName() + " NEW");
        newAlertDefinition1 = alertService.createOrUpdateAlertDefinition(newAlertDefinition1);

        MatcherAssert.assertThat(alertService.getAllAlertDefinitions(),
            Matchers.containsInAnyOrder(
                AlertDefinitionIsEqual.equalTo(ImmutableList.of(newAlertDefinition0, newAlertDefinition1))));
    }

    @Ignore
    @Test
    public void testCreateDuplicateAlertDefinition() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        final AlertDefinition genAlertDefinition = alertGenerator.generate();
        genAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());

        final AlertDefinition newAlertDefinition0 = alertService.createOrUpdateAlertDefinition(genAlertDefinition);
        final AlertDefinition newAlertDefinition1 = alertService.createOrUpdateAlertDefinition(genAlertDefinition);

        MatcherAssert.assertThat(alertService.getAllAlertDefinitions(),
            Matchers.containsInAnyOrder(
                AlertDefinitionIsEqual.equalTo(ImmutableList.of(newAlertDefinition0, newAlertDefinition1))));
    }

    @Test(expected = AlertDefinitionNotFoundException.class)
    public void testUpdateNonExistingAlertDefinition() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        final AlertDefinition genAlertDefinition = alertGenerator.generate();
        genAlertDefinition.setId(-1);
        genAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());

        alertService.createOrUpdateAlertDefinition(genAlertDefinition);
    }

    @Test
    public void testGetAlertDefinitionChildren() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        // create a new alert
        AlertDefinition rootAlertDefinition = alertGenerator.generate();
        rootAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        rootAlertDefinition = alertService.createOrUpdateAlertDefinition(rootAlertDefinition);

        AlertDefinition extendedAlertDefinition0 = new AlertDefinition();
        extendedAlertDefinition0.setParentId(rootAlertDefinition.getId());
        extendedAlertDefinition0.setTemplate(false);
        extendedAlertDefinition0.setLastModifiedBy("pribeiro");
        extendedAlertDefinition0.setStatus(DefinitionStatus.ACTIVE);
        extendedAlertDefinition0.setTeam("Platform/System");
        extendedAlertDefinition0.setResponsibleTeam("Platform/Database");
        extendedAlertDefinition0 = alertService.createOrUpdateAlertDefinition(extendedAlertDefinition0);

        AlertDefinition extendedAlertDefinition1 = new AlertDefinition();
        extendedAlertDefinition1.setParentId(rootAlertDefinition.getId());
        extendedAlertDefinition1.setTemplate(false);
        extendedAlertDefinition1.setLastModifiedBy("pribeiro");
        extendedAlertDefinition1.setStatus(DefinitionStatus.ACTIVE);
        extendedAlertDefinition1.setTeam("Platform/Security");
        extendedAlertDefinition1.setResponsibleTeam("Platform/Software");
        extendedAlertDefinition1 = alertService.createOrUpdateAlertDefinition(extendedAlertDefinition1);

        MatcherAssert.assertThat(alertService.getAlertDefinitionChildren(rootAlertDefinition.getId()),
            Matchers.containsInAnyOrder(
                AlertDefinitionIsEqual.equalTo(
                    alertService.getAlertDefinitions(null,
                        ImmutableList.of(extendedAlertDefinition0.getId(), extendedAlertDefinition1.getId())))));
    }

    @Ignore
    @Test
    public void testGetAlertDefinitionTemplateChildren() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        // create a template with minimum fields.
        AlertDefinition template = new AlertDefinition();
        template.setTemplate(true);
        template.setLastModifiedBy("pribeiro");
        template.setCheckDefinitionId(newCheckDefinition.getId());
        template.setStatus(DefinitionStatus.ACTIVE);
        template.setTeam("Platform/System");
        template.setResponsibleTeam("Platform/Database");

        template = alertService.createOrUpdateAlertDefinition(template);

        AlertDefinition extendedAlertDefinition = alertGenerator.generate();
        extendedAlertDefinition.setParentId(template.getId());
        extendedAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        extendedAlertDefinition = alertService.createOrUpdateAlertDefinition(extendedAlertDefinition);

        final List<AlertDefinition> children = alertService.getAlertDefinitionChildren(template.getId());

        MatcherAssert.assertThat(children, Matchers.contains(AlertDefinitionIsEqual.equalTo(extendedAlertDefinition)));
    }

    @Test
    public void testGetAlertDefinitionLeafChildren() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        // create a new alert
        AlertDefinition newAlertDefinition = alertGenerator.generate();
        newAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        newAlertDefinition = alertService.createOrUpdateAlertDefinition(newAlertDefinition);

        final List<AlertDefinition> children = alertService.getAlertDefinitionChildren(newAlertDefinition.getId());
        MatcherAssert.assertThat(children, Matchers.hasSize(0));

    }

    @Test
    public void testDeleteExistingAlertDefinition() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition alertDefinition = alertGenerator.generate();
        alertDefinition.setCheckDefinitionId(newCheckDefinition.getId());

        alertDefinition = alertService.createOrUpdateAlertDefinition(alertDefinition);

        List<AlertDefinition> alertDefinitions = alertService.getAlertDefinitions(alertDefinition.getStatus(), Lists.newArrayList(alertDefinition.getId()));

        MatcherAssert.assertThat(alertDefinitions, Matchers.hasSize(1));

        // delete the alert definition
        alertService.deleteAlertDefinition(alertDefinition.getId());

        alertDefinitions = alertService.getAlertDefinitions(alertDefinition.getStatus(), Lists.newArrayList(alertDefinition.getId()));

        MatcherAssert.assertThat(alertDefinitions, Matchers.hasSize(0));
    }

    @Test
    public void testDeleteNonExistingAlertDefinition() throws Exception {

        // delete non existing alert definition
        alertService.deleteAlertDefinition(Integer.MIN_VALUE);
    }

    @Test
    public void testDeleteAlertDefinitionsDiff() throws Exception {

        // create a new check
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition alertDefinition = alertGenerator.generate();
        alertDefinition.setCheckDefinitionId(newCheckDefinition.getId());

        alertDefinition = alertService.createOrUpdateAlertDefinition(alertDefinition);

        final AlertDefinitionsDiff alertDefinitions = alertService.getAlertDefinitionsDiff(null);

        // delete the alert definition
        alertService.deleteAlertDefinition(alertDefinition.getId());

        final AlertDefinitionsDiff diff = alertService.getAlertDefinitionsDiff(alertDefinitions.getSnapshotId());

        MatcherAssert.assertThat(diff, IsNull.notNullValue());
        MatcherAssert.assertThat(diff.getSnapshotId(), Matchers.greaterThan(alertDefinitions.getSnapshotId()));
        MatcherAssert.assertThat(diff.getDisabledDefinitions(), Matchers.contains(alertDefinition.getId()));
        MatcherAssert.assertThat(diff.getChangedDefinitions(), IsNull.nullValue());
    }

    @Test
    public void testAllAlertDefinitionsDiff() throws Exception {
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        final AlertDefinition genAlertDefinition = alertGenerator.generate();
        genAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());

        final AlertDefinition newAlertDefinition0 = alertService.createOrUpdateAlertDefinition(genAlertDefinition);
        final AlertDefinition newAlertDefinition1 = alertService.createOrUpdateAlertDefinition(genAlertDefinition);

        alertService.deleteAlertDefinition(newAlertDefinition1.getId());

        final AlertDefinitionsDiff diff = alertService.getAlertDefinitionsDiff(null);

        MatcherAssert.assertThat(diff, IsNull.notNullValue());
        MatcherAssert.assertThat(diff.getSnapshotId(), Matchers.greaterThan(0L));
        MatcherAssert.assertThat(diff.getDisabledDefinitions(), Matchers.hasSize(0));
        MatcherAssert.assertThat(diff.getChangedDefinitions(),
            Matchers.contains(AlertDefinitionIsEqual.equalTo(newAlertDefinition0)));
    }

    @Test
    public void testLastAlertDefinitionsDiff() throws Exception {
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        final AlertDefinition genAlertDefinition = alertGenerator.generate();
        genAlertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        alertService.createOrUpdateAlertDefinition(genAlertDefinition);

        final Long snapshotId = alertService.getAlertDefinitionsDiff(null).getSnapshotId();

        final AlertDefinitionsDiff diff = alertService.getAlertDefinitionsDiff(snapshotId);

        MatcherAssert.assertThat(diff, IsNull.notNullValue());
        MatcherAssert.assertThat(diff.getSnapshotId(), Matchers.equalTo(snapshotId));
        MatcherAssert.assertThat(diff.getDisabledDefinitions(), IsNull.nullValue());
        MatcherAssert.assertThat(diff.getChangedDefinitions(), IsNull.nullValue());
    }

    @Test
    public void testGetAlertDefinitionsByTeam() throws Exception {

        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition genAlertDefinition0 = alertGenerator.generate();
        genAlertDefinition0.setCheckDefinitionId(newCheckDefinition.getId());
        genAlertDefinition0.setTeam("Platform/Software");
        genAlertDefinition0.setResponsibleTeam("Backend/Payment");
        genAlertDefinition0 = alertService.createOrUpdateAlertDefinition(genAlertDefinition0);

        final AlertDefinition genAlertDefinition1 = alertGenerator.generate();
        genAlertDefinition1.setCheckDefinitionId(newCheckDefinition.getId());
        genAlertDefinition1.setTeam("Backend/Article");
        genAlertDefinition1.setResponsibleTeam("Backend/Order");
        alertService.createOrUpdateAlertDefinition(genAlertDefinition1);

        final List<AlertDefinition> alertDefinitions = alertService.getAlertDefinitions(DefinitionStatus.ACTIVE,
                ImmutableSet.of("Platform"));

        MatcherAssert.assertThat(alertDefinitions,
            Matchers.containsInAnyOrder(
                AlertDefinitionIsEqual.equalTo(
                    alertService.getAlertDefinitions(null, ImmutableList.of(genAlertDefinition0.getId())))));
    }

    @Test
    public void testGetAlertDefinitionsByResponsibleTeam() throws Exception {

        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition genAlertDefinition0 = alertGenerator.generate();
        genAlertDefinition0.setCheckDefinitionId(newCheckDefinition.getId());
        genAlertDefinition0.setTeam("Backend/Payment");
        genAlertDefinition0.setResponsibleTeam("Platform/Software");
        genAlertDefinition0 = alertService.createOrUpdateAlertDefinition(genAlertDefinition0);

        final AlertDefinition genAlertDefinition1 = alertGenerator.generate();
        genAlertDefinition1.setCheckDefinitionId(newCheckDefinition.getId());
        genAlertDefinition1.setTeam("Backend/Article");
        genAlertDefinition1.setResponsibleTeam("Backend/Order");
        alertService.createOrUpdateAlertDefinition(genAlertDefinition1);

        final List<AlertDefinition> alertDefinitions = alertService.getAlertDefinitions(DefinitionStatus.ACTIVE,
                ImmutableSet.of("Platform"));

        MatcherAssert.assertThat(alertDefinitions,
            Matchers.containsInAnyOrder(
                AlertDefinitionIsEqual.equalTo(
                    alertService.getAlertDefinitions(null, ImmutableList.of(genAlertDefinition0.getId())))));
    }

    @Test
    public void testGetAlertDefinitionsByNonExistingTeam() throws Exception {

        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        final AlertDefinition genAlertDefinition0 = alertGenerator.generate();
        genAlertDefinition0.setCheckDefinitionId(newCheckDefinition.getId());
        genAlertDefinition0.setTeam("Backend/Payment");
        genAlertDefinition0.setResponsibleTeam("Platform/Software");
        alertService.createOrUpdateAlertDefinition(genAlertDefinition0);

        final List<AlertDefinition> alertDefinitions = alertService.getAlertDefinitions(DefinitionStatus.ACTIVE,
                ImmutableSet.of("Backend/\\%"));

        MatcherAssert.assertThat(alertDefinitions, Matchers.hasSize(0));
    }

    @Test
    public void testAddComment() throws Exception {
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition alertDefinition = alertGenerator.generate();
        alertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        alertDefinition = alertService.createOrUpdateAlertDefinition(alertDefinition);

        final AlertCommentImport comment = commentGenerator.generate();
        comment.setAlertDefinitionId(alertDefinition.getId());

        final AlertCommentImport result = alertService.addComment(comment);
        final List<AlertCommentImport> comments = alertService.getComments(alertDefinition.getId(), Integer.MAX_VALUE, 0);

        MatcherAssert.assertThat(comments, Matchers.contains(AlertCommentIsEqual.equalTo(result)));
    }

    @Test(expected = AlertDefinitionNotFoundException.class)
    public void testAddCommentWithNonExistingAlertDefinitionId() throws Exception {
        final AlertCommentImport comment = commentGenerator.generate();
        comment.setAlertDefinitionId(-1);

        alertService.addComment(comment);
    }

    @Test
    public void testEmptyComments() throws Exception {
        final List<AlertCommentImport> comments = alertService.getComments(1, 1, 0);

        MatcherAssert.assertThat(comments, Matchers.hasSize(0));
    }

    @Test
    public void testGetComment() throws Exception {
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition alertDefinition = alertGenerator.generate();
        alertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        alertDefinition = alertService.createOrUpdateAlertDefinition(alertDefinition);

        final AlertCommentImport comment = commentGenerator.generate();
        comment.setAlertDefinitionId(alertDefinition.getId());

        final AlertCommentImport result = alertService.addComment(comment);

        // get comment
        final List<AlertCommentImport> comments = alertService.getComments(alertDefinition.getId(), 1, 0);

        MatcherAssert.assertThat(comments, Matchers.contains(AlertCommentIsEqual.equalTo(result)));
    }

    @Test
    public void testCommentPagination() throws Exception {
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition alertDefinition = alertGenerator.generate();
        alertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        alertDefinition = alertService.createOrUpdateAlertDefinition(alertDefinition);

        final AlertCommentImport comment = commentGenerator.generate();
        comment.setAlertDefinitionId(alertDefinition.getId());

        final AlertCommentImport comment0 = alertService.addComment(comment);

        comment.setComment(comment.getComment() + " NEW");

        final AlertCommentImport comment1 = alertService.addComment(comment);

        // get comment number 1
        List<AlertCommentImport> comments = alertService.getComments(alertDefinition.getId(), 1, 0);
        MatcherAssert.assertThat(comments, Matchers.contains(AlertCommentIsEqual.equalTo(comment1)));

        // get comment number 0
        comments = alertService.getComments(alertDefinition.getId(), 1, 1);
        MatcherAssert.assertThat(comments, Matchers.contains(AlertCommentIsEqual.equalTo(comment0)));
    }

    @Test
    public void testDeleteExistingComment() throws Exception {

        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition alertDefinition = alertGenerator.generate();
        alertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        alertDefinition = alertService.createOrUpdateAlertDefinition(alertDefinition);

        AlertCommentImport comment = commentGenerator.generate();
        comment.setAlertDefinitionId(alertDefinition.getId());

        comment = alertService.addComment(comment);

        alertService.deleteAlertComment(comment.getId());

        final List<AlertCommentImport> comments = alertService.getComments(alertDefinition.getId(), 1, 0);
        MatcherAssert.assertThat(comments, Matchers.hasSize(0));
    }

    @Test
    public void testDeleteNonExistingComment() throws Exception {
        alertService.deleteAlertComment(Integer.MIN_VALUE);
    }

    @Test
    public void testGetMultipleTags() throws Exception {
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        AlertDefinition alertDefinition = alertGenerator.generate();
        alertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        alertDefinition = alertService.createOrUpdateAlertDefinition(alertDefinition);

        MatcherAssert.assertThat(alertService.getAllTags(), Matchers.is(alertDefinition.getTags()));
    }

    @Test
    public void testGetEmptyTags() throws Exception {
        final CheckDefinition newCheckDefinition = createNewCheckDefinition();

        final AlertDefinition alertDefinition = alertGenerator.generate();
        alertDefinition.setTags(Collections.<String>emptyList());
        alertDefinition.setCheckDefinitionId(newCheckDefinition.getId());
        alertService.createOrUpdateAlertDefinition(alertDefinition);

        MatcherAssert.assertThat(alertService.getAllTags(), Matchers.hasSize(0));
    }
}
