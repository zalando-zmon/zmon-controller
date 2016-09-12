package org.zalando.zmon.security.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.zalando.zmon.security.AuthorityService;
import org.zalando.zmon.security.authority.ZMonAdminAuthority;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by hjacobs on 17.12.15.
 */
public class PresharedTokensResourceServerTokenServices implements ResourceServerTokenServices {

    private AuthorityService authorityService;

    private Environment environment;

    public PresharedTokensResourceServerTokenServices(final AuthorityService authorityService, final Environment environment) {
        this.authorityService = authorityService;
        this.environment = environment;
    }

    @Override
    public OAuth2Authentication loadAuthentication(final String accessToken) throws AuthenticationException, InvalidTokenException {

        final String uid = environment.getProperty(String.format("preshared_tokens.%s.uid", accessToken));

        if (uid == null) {
            throw new InvalidTokenException("Invalid pre-shared token");
        }

        // expires_at is timestamp in seconds
        final Long expiresAt = environment.getProperty(String.format("preshared_tokens.%s.expires_at", accessToken), Long.class);
        if (expiresAt == null || System.currentTimeMillis() > expiresAt * 1000) {
            throw new InvalidTokenException("Pre-shared token expired");
        }

        Collection<? extends GrantedAuthority> authorities;

        // allow overwritting the authority of the preshared token UID
        final String authority = environment.getProperty(String.format("preshared_tokens.%s.authority", accessToken));

        if (authority != null) {
            // we don't know what team to assign.. => empty list
            ImmutableSet<String> teams = ImmutableSet.of();
            if (authority.toUpperCase().contains("ADMIN")) {
                authorities = Lists.newArrayList(new ZMonAdminAuthority(uid, teams));
            } else {
                authorities = Lists.newArrayList(new ZMonUserAuthority(uid, teams));
            }
        } else {
            authorities = authorityService.getAuthorities(uid);
        }

        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(uid, "N/A", authorities);
        final Set scopes = Sets.newHashSet("uid");

        final Map<String, Object> map = Maps.newHashMap();
        map.put("scopes", scopes);
        // we assume pre-shared tokens are only used for services
        map.put("realm", "/services");
        user.setDetails(map);

        OAuth2Request request = new OAuth2Request((Map) null, "NOT_NEEDED", (Collection) null, true, scopes, (Set) null, (String) null, (Set) null, (Map) null);
        return new OAuth2Authentication(request, user);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }
}

