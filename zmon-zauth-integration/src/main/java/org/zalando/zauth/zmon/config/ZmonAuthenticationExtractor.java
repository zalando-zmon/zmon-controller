package org.zalando.zauth.zmon.config;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;
import org.zalando.stups.oauth2.spring.server.DefaultAuthenticationExtractor;
import org.zalando.zmon.security.AuthorityService;
import org.zalando.zmon.security.DynamicTeamService;
import org.zalando.zmon.security.authority.ZMonAuthority;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author jbellmann
 */
public class ZmonAuthenticationExtractor extends DefaultAuthenticationExtractor {

    private static final String UID = "uid";
    private static final String REALM = "realm";
    private final AuthorityService userService;
    private final DynamicTeamService dynamicTeamService;

    public ZmonAuthenticationExtractor(AuthorityService userService, DynamicTeamService dynamicTeamService) {
        this.userService = userService;
        this.dynamicTeamService = dynamicTeamService;
    }

    @Override
    protected List<GrantedAuthority> createAuthorityList(Map<String, Object> tokenInfoResponse) {
        Assert.notNull(tokenInfoResponse, "'tokenInfoResponse' should never be null");
        String realm = (String) tokenInfoResponse.getOrDefault(REALM, "unknown");
        String uid = (String) tokenInfoResponse.get(UID);
        Assert.hasText(uid, "'uid' should never be null or empty.");

        if ("/employees".equals(realm)) {
            return Lists.newArrayList(userService.getAuthorities(uid));
        } else {
            final List<String> teams = dynamicTeamService.getTeams(uid).orElse(Collections.emptyList());
            final ZMonAuthority authority = new ZMonUserAuthority(uid, ImmutableSet.copyOf(teams));
            return Lists.newArrayList(authority);
        }
    }

}
