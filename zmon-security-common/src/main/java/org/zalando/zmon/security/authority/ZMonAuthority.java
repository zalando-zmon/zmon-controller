package org.zalando.zmon.security.authority;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.zalando.zmon.domain.AlertCommentImport;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.DashboardImport;

public interface ZMonAuthority extends GrantedAuthority {

    Set<String> getTeams();

    boolean hasTrialRunPermission();

    boolean hasAddCommentPermission();

    boolean hasDeleteCommentPermission(AlertCommentImport comment);

    boolean hasScheduleDowntimePermission();

    boolean hasDeleteDowntimePermission();

    boolean hasAddDashboardPermission();

    boolean hasEditDashboardPermission(DashboardImport dashboard);

    boolean hasDashboardEditModePermission(DashboardImport dashboard);

    boolean hasAddAlertDefinitionPermission();

    boolean hasAddAlertDefinitionPermission(AlertDefinition alertDefinition);

    boolean hasEditAlertDefinitionPermission(AlertDefinition alertDefinition);

    boolean hasUpdateAlertDefinitionPermission(AlertDefinition currentAlertDefinition,
            AlertDefinition newAlertDefinitionDefinition);

    boolean hasDeleteAlertDefinitionPermission(AlertDefinition alertDefinition);

    boolean hasDeleteUnusedCheckDefinitionPermission(CheckDefinition checkDefinition);

    boolean hasInstantaneousAlertEvaluationPermission();

}
