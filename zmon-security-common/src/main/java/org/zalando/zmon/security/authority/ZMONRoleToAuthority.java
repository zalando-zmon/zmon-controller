package org.zalando.zmon.security.authority;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.ImmutableSet;

public final class ZMONRoleToAuthority {

    private static final Map<String, GrantedAuthorityFactory> factories = new HashMap<>();

    static {
        factories.put(ZMonRole.ADMIN.name(), ZMonAdminAuthority.FACTORY);
        factories.put(ZMonRole.API_READER.name(), ZMonApiReaderAuthority.FACTORY);
        factories.put(ZMonRole.API_WRITER.name(), ZMonApiWriterAuthority.FACTORY);
        factories.put(ZMonRole.LEAD.name(), ZMonLeadAuthority.FACTORY);
        factories.put(ZMonRole.USER.name(), ZMonUserAuthority.FACTORY);
        factories.put(ZMonRole.VIEWER.name(), ZMonViewerAuthority.FACTORY);
    }

    public static GrantedAuthority createAutority(String rolename, String username, Set<String> teams) {
        return factories.get(rolename).create(username, ImmutableSet.copyOf(teams));
    }

}
