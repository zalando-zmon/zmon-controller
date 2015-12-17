package org.zalando.zmon.security.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by hjacobs on 17.12.15.
 */
public class ChainedResourceServerTokenServices implements ResourceServerTokenServices {

    List<ResourceServerTokenServices> chain;

    public ChainedResourceServerTokenServices(List<ResourceServerTokenServices> chain) {
        Assert.notEmpty(chain, "'chain' should never be empty");
        this.chain = chain;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        OAuth2Authentication result = null;
        int i = 0;
        for (ResourceServerTokenServices service : chain) {
            try {
                result = service.loadAuthentication(accessToken);
                break;
            } catch (InvalidTokenException e) {
                if (i >= chain.size()-1) {
                    throw e;
                }
            }  catch (AuthenticationException e) {
                if (i >= chain.size()-1) {
                    throw e;
                }
             }
            i++;
        }
        return result;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String s) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }
}
