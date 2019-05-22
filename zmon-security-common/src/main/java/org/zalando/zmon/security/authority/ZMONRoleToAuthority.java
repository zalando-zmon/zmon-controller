package org.zalando.zmon.security.authority;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.ImmutableSet;

public final class ZMONRoleToAuthority {
    private static final Logger log = LoggerFactory.getLogger(ZMONRoleToAuthority.class);
    private static final Map<String, GrantedAuthorityFactory> factories = new HashMap<>();

    static {
        factories.put(ZMonRole.ADMIN.getRoleName(), ZMonAdminAuthority.FACTORY);
        factories.put(ZMonRole.API_READER.getRoleName(), ZMonApiReaderAuthority.FACTORY);
        factories.put(ZMonRole.API_WRITER.getRoleName(), ZMonApiWriterAuthority.FACTORY);
        factories.put(ZMonRole.LEAD.getRoleName(), ZMonLeadAuthority.FACTORY);
        factories.put(ZMonRole.USER.getRoleName(), ZMonUserAuthority.FACTORY);
        factories.put(ZMonRole.VIEWER.getRoleName(), ZMonViewerAuthority.FACTORY);
        factories.put(ZMonRole.KAIROS_READER.getRoleName(), ZMonKairosReaderAuthority.FACTORY);
    }

    public static GrantedAuthority createAutority(String rolename, String username, Set<String> teams) {
        log.debug("create authority for username: {}, with role {} and teams: {}", username, rolename, teams.toArray(new String[0]).toString());
        return factories.get(rolename).create(username, ImmutableSet.copyOf(teams));
    }

}
