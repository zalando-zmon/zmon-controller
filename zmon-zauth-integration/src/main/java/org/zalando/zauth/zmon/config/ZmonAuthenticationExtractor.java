package org.zalando.zauth.zmon.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;
import org.zalando.stups.oauth2.spring.server.DefaultAuthenticationExtractor;
import org.zalando.zmon.security.AuthorityService;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author jbellmann
 */
public class ZmonAuthenticationExtractor extends DefaultAuthenticationExtractor {

    private static final String UID = "uid";

    private final AuthorityService userService;

    public ZmonAuthenticationExtractor(AuthorityService userService) {
        this.userService = userService;
    }

    @Override
    protected List<GrantedAuthority> createAuthorityList(Map<String, Object> tokenInfoResponse) {
        Assert.notNull(tokenInfoResponse, "'tokenInfoResponse' should never be null");
        String uid = tokenInfoResponse.get(UID).toString();
        Assert.hasText(uid, "'uid' should never be null or empty.");

        return newArrayList(userService.getAuthorities(uid));
    }

}
