package org.zalando.github.zmon.service;

import com.google.common.collect.Sets;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.social.github.api.impl.GitHubTemplate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by hjacobs on 14.12.15.
 */
public class GithubResourceServerTokenServices implements ResourceServerTokenServices {

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        GitHubTemplate tpl = new GitHubTemplate(accessToken);


        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(tpl.userOperations().getProfileId(), "N/A", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
        //user.setDetails(map);
        Set scopes = Sets.newHashSet("uid");

        OAuth2Request request = new OAuth2Request((Map)null, "NOT_NEEDED", (Collection)null, true, scopes, (Set)null, (String)null, (Set)null, (Map)null);
        return new OAuth2Authentication(request, user);

    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }
}
