package org.zalando.zmon.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.zalando.zmon.domain.Alert;
import org.zalando.zmon.domain.AlertComment;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.AlertDefinitions;
import org.zalando.zmon.domain.AlertDefinitionsDiff;
import org.zalando.zmon.domain.DefinitionStatus;
import org.zalando.zmon.exception.ZMonException;

public interface AlertService {

    List<AlertDefinition> getAllAlertDefinitions();

    List<AlertDefinition> getAlertDefinitions(@Nullable DefinitionStatus status, List<Integer> alertDefinitionIds);

    List<AlertDefinition> getAlertDefinitions(@Nullable DefinitionStatus status, Set<String> teams);

    AlertDefinitions getActiveAlertDefinitionsDiff();

    AlertDefinitionsDiff getAlertDefinitionsDiff(@Nullable Long snapshotId);

    List<Alert> getAllAlerts();

    List<Alert> getAllAlertsById(Set<Integer> alertIdfilter);

    List<Alert> getAllAlertsByTeamAndTag(Set<String> teams, Set<String> tags);

    Alert getAlert(int alertId);

    AlertDefinition createOrUpdateAlertDefinition(AlertDefinition alertDefinition) throws ZMonException;

    AlertDefinition deleteAlertDefinition(int id) throws ZMonException;

    AlertComment addComment(AlertComment comment) throws ZMonException;

    List<AlertComment> getComments(int alertDefinitionId, int limit, int offset);

    void deleteAlertComment(int id);

    AlertDefinition getAlertDefinitionNode(int alertDefinitionId);

    List<AlertDefinition> getAlertDefinitionChildren(int alertDefinitionId);

    void forceAlertEvaluation(int alertDefinitionId) throws IOException;

    void cleanAlertState(int alertDefinitionId);

    void acknowledgeAlert(int alertId);

    List<String> getAllTags();

    Date getMaxLastModified();
}
