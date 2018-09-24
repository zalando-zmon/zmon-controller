package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentry")
public class SentryProperties {

    private String enabled;
    private String dsn;
    private String dsnUI;
    private String sampleRate;
    private String environment;
    private String servername;
    private String tags;

}
