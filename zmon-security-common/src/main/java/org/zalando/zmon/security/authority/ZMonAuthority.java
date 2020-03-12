package org.zalando.zmon.security.authority;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.zalando.zmon.domain.AlertCommentRecord;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.DashboardRecord;

public interface ZMonAuthority extends GrantedAuthority {

    Set<String> getTeams();

    boolean hasTrialRunPermission();

    boolean hasAddCommentPermission();

    boolean hasDeleteCommentPermission(AlertCommentRecord comment);

    boolean hasScheduleDowntimePermission();

    boolean hasDeleteDowntimePermission();

    boolean hasAddDashboardPermission();

    boolean hasEditDashboardPermission(DashboardRecord dashboard);

    boolean hasDashboardEditModePermission(DashboardRecord dashboard);

    boolean hasAddAlertDefinitionPermission();

    boolean hasAddAlertDefinitionPermission(AlertDefinition alertDefinition);

    boolean hasEditAlertDefinitionPermission(AlertDefinition alertDefinition);

    boolean hasUpdateAlertDefinitionPermission(AlertDefinition currentAlertDefinition,
            AlertDefinition newAlertDefinitionDefinition);

    boolean hasDeleteAlertDefinitionPermission(AlertDefinition alertDefinition);

    boolean hasDeleteUnusedCheckDefinitionPermission(CheckDefinition checkDefinition);

    boolean hasInstantaneousAlertEvaluationPermission();

}
