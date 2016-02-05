package org.zalando.github.zmon.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.zalando.zmon.security.AuthorityService;

/**
 * Created by hjacobs on 14.12.15.
 */
public class GithubResourceServerTokenServices implements ResourceServerTokenServices {

    private AuthorityService authorityService;

    private Environment environment;

    public GithubResourceServerTokenServices(AuthorityService authorityService, Environment environment) {
        this.authorityService = authorityService;
        this.environment = environment;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken)
            throws AuthenticationException, InvalidTokenException {
        GitHubTemplate tpl = new GitHubTemplate(accessToken);

        // TODO: Important: we need to check the GitHub user's organization etc
        // to comply with out configured "SignupConditions"
        String username;
        try {
            username = tpl.userOperations().getProfileId();
        } catch (HttpClientErrorException ex) {
            if (HttpStatus.UNAUTHORIZED == ex.getStatusCode()) {
                throw new InvalidTokenException("Invalid GitHub access token");
            }
            throw ex;
        }
        Collection<? extends GrantedAuthority> authorities = authorityService.getAuthorities(username);

        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(username, "N/A",
                authorities);

        Set<String> scopes = Sets.newHashSet("uid");
        Map<String, Object> map = Maps.newHashMap();
        map.put("scopes", scopes);
        map.put("realm", "/employee");
        user.setDetails(map);

        OAuth2Request request = new OAuth2Request((Map) null, "NOT_NEEDED", (Collection) null, true, scopes, (Set) null,
                (String) null, (Set) null, (Map) null);
        return new OAuth2Authentication(request, user);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }
}
