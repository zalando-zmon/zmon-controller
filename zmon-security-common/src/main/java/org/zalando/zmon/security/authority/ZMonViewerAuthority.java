package org.zalando.zmon.security.authority;

import com.google.common.collect.ImmutableSet;
import org.springframework.security.core.GrantedAuthority;
import org.zalando.zmon.domain.*;

import java.util.Set;

public class ZMonViewerAuthority extends AbstractZMonAuthority {

    private static final long serialVersionUID = 1L;

    public static final GrantedAuthorityFactory FACTORY = new GrantedAuthorityFactory() {
        @Override
        public GrantedAuthority create(final String username, final Set<String> projects) {
            return new ZMonViewerAuthority(username, ImmutableSet.copyOf(projects));
        }
    };

    public ZMonViewerAuthority(final String userName, final ImmutableSet<String> teams) {
        super(userName, teams);
    }

    @Override
    public boolean hasTrialRunPermission() {
        return false;
    }

    @Override
    public boolean hasAddCommentPermission() {
        return false;
    }

    @Override
    public boolean hasDeleteCommentPermission(final AlertComment comment) {
        return false;
    }

    @Override
    public boolean hasScheduleDowntimePermission() {
        return false;
    }

    @Override
    public boolean hasDeleteDowntimePermission() {
        return false;
    }

    @Override
    public boolean hasAddDashboardPermission() {
        return false;
    }

    @Override
    public boolean hasEditDashboardPermission(final Dashboard dashboard) {
        return false;
    }

    @Override
    public boolean hasDashboardEditModePermission(final Dashboard dashboard) {
        return false;
    }

    @Override
    public boolean hasAddAlertDefinitionPermission() {
        return false;
    }

    @Override
    public boolean hasAddAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        return false;
    }

    @Override
    public boolean hasEditAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        return false;
    }

    @Override
    public boolean hasUpdateAlertDefinitionPermission(final AlertDefinition currentAlertDefinition,
            final AlertDefinition newAlertDefinitionDefinition) {
        return false;
    }

    @Override
    public boolean hasDeleteAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        return false;
    }

    @Override
    public boolean hasDeleteUnusedCheckDefinitionPermission(final CheckDefinition checkDefinition) {
        return false;
    }

    @Override
    public boolean hasInstantaneousAlertEvaluationPermission() {
        return false;
    }

    @Override
    protected ZMonRole getZMonRole() {
        return ZMonRole.VIEWER;
    }

}
