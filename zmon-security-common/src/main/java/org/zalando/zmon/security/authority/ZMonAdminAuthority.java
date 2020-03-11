package org.zalando.zmon.security.authority;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.zalando.zmon.domain.AlertCommentImport;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.DashboardImport;

import com.google.common.collect.ImmutableSet;

public class ZMonAdminAuthority extends AbstractZMonAuthority {

    private static final long serialVersionUID = 1L;

    public static final GrantedAuthorityFactory FACTORY = new GrantedAuthorityFactory() {
        @Override
        public GrantedAuthority create(final String username, final Set<String> projects) {
            return new ZMonAdminAuthority(username, ImmutableSet.copyOf(projects));
        }
    };

    public ZMonAdminAuthority(final String username, final ImmutableSet<String> teams) {
        super(username, teams);
    }

    @Override
    public boolean hasTrialRunPermission() {
        return true;
    }

    @Override
    public boolean hasAddCommentPermission() {
        return true;
    }

    @Override
    public boolean hasDeleteCommentPermission(final AlertCommentImport comment) {
        return true;
    }

    @Override
    public boolean hasScheduleDowntimePermission() {
        return true;
    }

    @Override
    public boolean hasDeleteDowntimePermission() {
        return true;
    }

    @Override
    public boolean hasAddDashboardPermission() {
        return true;
    }

    @Override
    public boolean hasEditDashboardPermission(final DashboardImport dashboard) {
        return true;
    }

    @Override
    public boolean hasDashboardEditModePermission(final DashboardImport dashboard) {
        return true;
    }

    @Override
    public boolean hasAddAlertDefinitionPermission() {
        return true;
    }

    @Override
    public boolean hasAddAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        return true;
    }

    @Override
    public boolean hasEditAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        return true;
    }

    @Override
    public boolean hasUpdateAlertDefinitionPermission(final AlertDefinition currentAlertDefinition,
            final AlertDefinition newAlertDefinitionDefinition) {
        return true;
    }

    @Override
    public boolean hasDeleteAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        return true;
    }

    @Override
    public boolean hasDeleteUnusedCheckDefinitionPermission(final CheckDefinition checkDefinition) {
        return true;
    }

    @Override
    public boolean hasInstantaneousAlertEvaluationPermission() {
        return true;
    }

    @Override
    protected ZMonRole getZMonRole() {
        return ZMonRole.ADMIN;
    }
}
