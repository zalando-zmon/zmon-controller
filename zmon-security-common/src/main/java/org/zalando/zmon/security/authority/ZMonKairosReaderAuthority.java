package org.zalando.zmon.security.authority;

import com.google.common.collect.ImmutableSet;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public class ZMonKairosReaderAuthority extends ZMonViewerAuthority {

    private static final long serialVersionUID = 1L;

    public static final GrantedAuthorityFactory FACTORY = new GrantedAuthorityFactory() {
        @Override
        public GrantedAuthority create(final String username, final Set<String> projects) {
            return new ZMonKairosReaderAuthority(username, ImmutableSet.copyOf(projects));
        }
    };

    public ZMonKairosReaderAuthority(final String userName, final ImmutableSet<String> teams) {
        super(userName, teams);
    }

    @Override
    protected ZMonRole getZMonRole() {
        return ZMonRole.KAIROS_READER;
    }
}
