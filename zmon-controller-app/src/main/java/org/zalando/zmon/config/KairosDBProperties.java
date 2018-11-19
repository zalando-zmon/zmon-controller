package org.zalando.zmon.config;

import io.opentracing.contrib.apache.http.client.TracingHttpClientBuilder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * FIXME: hardcoded config for Vagrant box
 *
 * @author hjacobs
 */
@ConfigurationProperties(prefix = "zmon.kairosdb")
public class KairosDBProperties {

    public static class KairosDBServiceConfig {
        private String name;
        private String url;
        private boolean oauth2 = false;
        private int maxWindowLength = 0;

        public KairosDBServiceConfig() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isOauth2() {
            return oauth2;
        }

        public void setOauth2(boolean oauth2) {
            this.oauth2 = oauth2;
        }

        public int getMaxWindowLength() {
            return maxWindowLength;
        }

        public void setMaxWindowLength(int maxWindowLength) {
            this.maxWindowLength = maxWindowLength;
        }
    }

    private boolean enabled;

    private int connectTimeout = 3000; // 3 seconds
    private int socketTimeout = 30000; // 30 seconds
    private int maxConnectionsPerRoute = 100;
    private int maxConnectionsTotal = 200;
    private long connectionTimeToLive = 2 * 60 * 1000; // 2 minutes

    private List<KairosDBServiceConfig> kairosdbs = new ArrayList<>(1);

    public List<KairosDBServiceConfig> getKairosdbs() {
        return kairosdbs;
    }

    public void setKairosdbs(List<KairosDBServiceConfig> kairosdbs) {
        this.kairosdbs = kairosdbs;
    }

    public int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public int getMaxConnectionsTotal() {
        return maxConnectionsTotal;
    }

    public void setMaxConnectionsTotal(int maxConnectionsTotal) {
        this.maxConnectionsTotal = maxConnectionsTotal;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getConnectionTimeToLive() {
        return connectionTimeToLive;
    }

    public void setConnectionTimeToLive(long connectionTimeToLive) {
        this.connectionTimeToLive = connectionTimeToLive;
    }

    /**
     * get HttpClient with appropriate timeouts and TTL
     *
     * @return CloseableHttpClient
     */
    public CloseableHttpClient getHttpClient() {
        RequestConfig config = RequestConfig.custom().
                setSocketTimeout(getSocketTimeout()).
                setConnectTimeout(getConnectTimeout()).
                build();
        return new TracingHttpClientBuilder().
                setMaxConnPerRoute(getMaxConnectionsPerRoute()).
                setMaxConnTotal(getMaxConnectionsTotal()).
                setConnectionTimeToLive(getConnectionTimeToLive(), TimeUnit.MILLISECONDS).
                setDefaultRequestConfig(config).
                build();
    }
}
