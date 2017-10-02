package org.zalando.zmon.security.authority;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.springframework.security.core.GrantedAuthority;
import org.zalando.zmon.domain.AlertDefinition;

import java.util.Set;

public class ZMonLeadAuthority extends ZMonUserAuthority {

    private static final long serialVersionUID = 1L;

    public static final GrantedAuthorityFactory FACTORY = new GrantedAuthorityFactory() {
        @Override
        public GrantedAuthority create(final String username, final Set<String> projects) {
            return new ZMonLeadAuthority(username, ImmutableSet.copyOf(projects));
        }
    };

    public ZMonLeadAuthority(final String userName, final ImmutableSet<String> teams) {
        super(userName, teams);
    }

    @Override
    public boolean hasEditAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        Preconditions.checkNotNull(alertDefinition, "alertDefinition");

        return isMemberOfTeam(alertDefinition.getTeam()) || isMemberOfTeam(alertDefinition.getResponsibleTeam());
    }

    @Override
    public boolean hasDeleteAlertDefinitionPermission(final AlertDefinition alertDefinition) {
        return hasEditAlertDefinitionPermission(alertDefinition);
    }

    @Override
    protected ZMonRole getZMonRole() {
        return ZMonRole.LEAD;
    }

}
