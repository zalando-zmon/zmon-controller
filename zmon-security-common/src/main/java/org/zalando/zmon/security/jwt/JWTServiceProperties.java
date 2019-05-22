package org.zalando.zmon.security.jwt;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import com.nimbusds.jose.util.ByteUtils;

import java.util.UUID;

@ConfigurationProperties("zmon.jwt")
public class JWTServiceProperties {

    private final Logger log = LoggerFactory.getLogger(JWTServiceProperties.class);

    private String secret;
    private String kairosdbSignKey;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getKairosdbSignKey() {
        return kairosdbSignKey;
    }

    public void setKairosdbSignKey(String kairosdbSignKey) {
        this.kairosdbSignKey = kairosdbSignKey;
    }

    @PostConstruct
    public void postConstruct() {
        if (StringUtils.isEmpty(secret)) {
            log.info("ZMON_JWT_SECRET configuration is missing. Generating a random secret.");
            secret = UUID.randomUUID().toString();
        }
        Assert.isTrue(ByteUtils.bitLength(secret.length()) >= 256, "'secret'-bit-length should be at least 256");
        Assert.isTrue(ByteUtils.bitLength(kairosdbSignKey.length()) >= 256, "'secret'-bit-length should be at least 256");
    }
}
