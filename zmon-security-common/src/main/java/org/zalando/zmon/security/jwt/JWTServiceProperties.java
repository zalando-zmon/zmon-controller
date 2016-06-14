package org.zalando.zmon.security.jwt;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import com.nimbusds.jose.util.ByteUtils;

@ConfigurationProperties("zmon.jwt")
public class JWTServiceProperties {

    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @PostConstruct
    public void postConstruct() {
        Assert.hasText(secret, "'secret' should never be null or empty");
        Assert.isTrue(ByteUtils.bitLength(secret.length()) >= 256, "'secret'-bit-length should be at least 256");
    }
}
