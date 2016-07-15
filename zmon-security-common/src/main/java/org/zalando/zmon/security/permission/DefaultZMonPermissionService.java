package org.zalando.zmon.security.permission;

import static org.zalando.zmon.security.permission.AuthorityFunctions.TRIAL_RUN_PERMISSION_FUNCTION;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zalando.zmon.domain.AlertComment;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.Dashboard;
import org.zalando.zmon.domain.DefinitionStatus;
import org.zalando.zmon.exception.ZMonAuthorizationException;
import org.zalando.zmon.persistence.AlertDefinitionSProcService;
import org.zalando.zmon.persistence.DashboardSProcService;
import org.zalando.zmon.security.authority.ZMonAdminAuthority;
import org.zalando.zmon.security.authority.ZMonAuthority;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

@Service("defaultZMONPermissionService")
public class DefaultZMonPermissionService {

    private static final String ANONYMOUS_USER = "anonymousUser";

    public String getUserName() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? ANONYMOUS_USER : authentication.getName();
    }

    public Set<String> getTeams() {
        final ImmutableSet.Builder<String> teams = ImmutableSet.builder();
        for (final GrantedAuthority authority : getUserAuthorities()) {
            if (authority instanceof ZMonAuthority) {
                teams.addAll(((ZMonAuthority) authority).getTeams());
            }
        }

        return teams.build();
    }

    private boolean hasAnyAuthority(final Function<ZMonAuthority, Boolean> function) {
        for (final GrantedAuthority authority : getUserAuthorities()) {
            if (authority instanceof ZMonAuthority && function.apply((ZMonAuthority) authority)) {
                return true;
            }
        }

        return false;
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? Collections.<GrantedAuthority>emptyList() : authentication.getAuthorities();
    }

    public boolean hasTrialRunPermission() {
        return hasAnyAuthority(TRIAL_RUN_PERMISSION_FUNCTION);
    }

    public void verifyTrialRunPermission() {
        if (!hasTrialRunPermission()) {
            throw new ZMonAuthorizationException(getUserName(), getUserAuthorities(),
                "You are not allowed to use 'trial run' functionality");
        }
    }

    public boolean hasAddCommentPermission() {
        return hasAnyAuthority(AuthorityFunctions.ADD_COMMENT_PERMISSION_FUNCTION);
    }

    public void verifyAddCommentPermission() {
        if (!hasAddCommentPermission()) {
            throw new ZMonAuthorizationException(getUserName(), getUserAuthorities(),
                "You are not allowed to add comments");
        }
    }

    public boolean hasDeleteCommentPermission(final AlertComment comment) {
        Preconditions.checkNotNull(comment, "comment");

        return hasAnyAuthority(new HasDeleteCommentPermission(comment));
    }

    public void verifyDeleteCommentPermission(final int commentId) {
        final AlertComment comment = alertDefinitionSProc.getAlertCommentById(commentId);

        if (comment == null || !hasDeleteCommentPermission(comment)) {
            throw new ZMonAuthorizationException(getUserName(), getUserAuthorities(),
                "You are not allowed to delete this comment", commentId);
        }
    }

    public boolean hasAddAlertDefinitionPermission() {
        return hasAnyAuthority(AuthorityFunctions.ADD_ALERT_DEFINITION_PERMISSION_FUNCTION);
    }

    public boolean hasEditAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        Preconditions.checkNotNull(alertDefinition, "alertDefinition");

        return hasAnyAuthority(new HasEditAlertDefinitionPermission(alertDefinition)) && alertDefinition.getStatus() != DefinitionStatus.DELETED;
    }

    public boolean hasDeleteAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        Preconditions.checkNotNull(alertDefinition, "alertDefinition");

        return hasAnyAuthority(new HasDeleteAlertDefinitionPermission(alertDefinition));
    }

    public void verifyDeleteAlertDefinitionPermission(final int alertDefinitionId) {
        final List<AlertDefinition> definitions = alertDefinitionSProc.getAlertDefinitions(null,
                Collections.singletonList(alertDefinitionId));

        if (definitions == null || definitions.size() != 1 || !hasDeleteAlertDefinitionPermission(definitions.get(0))) {
            throw new ZMonAuthorizationException(getUserName(), getUserAuthorities(),
                "You are not allowed to delete this alert definition", alertDefinitionId);
        }
    }

    public void verifyEditAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        Preconditions.checkNotNull(alertDefinition, "alertDefinition");

        boolean isAllowed = false;
        if (alertDefinition.getId() == null) {
            isAllowed = hasAnyAuthority(new HasAddAlertDefinitionPermission(alertDefinition));
        } else {

            // that's an update... load current alert definition
            final List<AlertDefinition> definitions = alertDefinitionSProc.getAlertDefinitions(null,
                    Collections.singletonList(alertDefinition.getId()));

            isAllowed = definitions.size() == 1 && hasAnyAuthority(new HasUpdateAlertDefinitionPermission(alertDefinition));
        }

        if (!isAllowed) {
            throw new ZMonAuthorizationException(getUserName(), getUserAuthorities(),
                "Edit denied. Please check documentation for more details: /docs/permissions.html", alertDefinition);
        }
    }

    public boolean hasScheduleDowntimePermission() {
        return hasAnyAuthority(AuthorityFunctions.SCHEDULE_DOWNTIME_PERMISSION_FUNCTION);
    }

    public void verifyScheduleDowntimePermission() {
        if (!hasScheduleDowntimePermission()) {
            throw new ZMonAuthorizationException(getUserName(), getUserAuthorities(),
                "You are not allowed to schedule downtimes");
        }
    }

    public boolean hasDeleteDowntimePermission() {
        return hasAnyAuthority(AuthorityFunctions.DELETE_DOWNTIME_PERMISSION_FUNCTION);
    }

    public void verifyDeleteDowntimePermission() {
        if (!hasDeleteDowntimePermission()) {
            throw new ZMonAuthorizationException(getUserName(), getUserAuthorities(),
                "You are not allowed to delete downtimes");
        }
    }

    public boolean hasAddDashboardPermission() {
        return hasAnyAuthority(AuthorityFunctions.ADD_DASHBOARD_PERMISSION_FUNCTION);
    }

    public boolean hasEditDashboardPermission(final Dashboard dashboard) {
        Preconditions.checkNotNull(dashboard, "dashboard");

        return hasAnyAuthority(new HasEditDashboardPermission(dashboard));
    }

    public boolean hasDashboardEditModePermission(final Dashboard dashboard) {
        Preconditions.checkNotNull(dashboard, "dashboard");

        return hasAnyAuthority(new HasDashboardEditModePermission(dashboard));
    }

    public void verifyEditDashboardPermission(final Dashboard dashboard) {
        Preconditions.checkNotNull(dashboard, "dashboard");

        boolean isAllowed = hasAddDashboardPermission();
        if (isAllowed && dashboard.getId() != null) {
            final List<Dashboard> dashboards = dashboardSProc.getDashboards(Collections.singletonList(
                        dashboard.getId()));
            isAllowed = dashboards.size() == 1 && hasEditDashboardPermission(dashboards.get(0))
                    && (hasDashboardEditModePermission(dashboards.get(0))
                        || dashboards.get(0).getEditOption() == dashboard.getEditOption());
        }

        if (!isAllowed) {
            throw new ZMonAuthorizationException(getUserName(), getUserAuthorities(),
                "Your are not allowed to create/edit this dashboard", dashboard);
        }
    }

    public boolean hasInstantaneousAlertEvaluationPermission() {
        return hasAnyAuthority(AuthorityFunctions.INSTANTANEOUS_ALERT_EVALUATION_FUNCTION);
    }

    public void verifyInstantaneousAlertEvaluationPermission() {
        if (!hasInstantaneousAlertEvaluationPermission()) {
            throw new ZMonAuthorizationException(getUserName(), getUserAuthorities(),
                "Your are not allowed to force alert evaluation");
        }
    }

    // bad name but every admin is also user for now
    public boolean hasUserAuthority() {
        return getUserAuthorities().stream().anyMatch(x -> x instanceof ZMonUserAuthority || x instanceof ZMonAdminAuthority);
    }
}
