package org.zalando.zmon.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;
import java.util.Map;

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
        private boolean oauth2=false;

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
    }

    private URL url;

    private boolean enabled;

    private int connectTimeout = 3000; // 3 seconds
    private int socketTimeout = 30000; // 30 seconds
    private int maxConnectionsPerRoute = 100;
    private int maxConnectionsTotal = 200;

    public String frontendUrl = "";

    public String getFrontendUrl() {
        return frontendUrl;
    }

    public void setFrontendUrl(String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    private Map<String, KairosDBServiceConfig> kairosdbs;

    public Map<String, KairosDBServiceConfig> getKairosdbs() {
        return kairosdbs;
    }

    public void setKairosdbs(Map<String, KairosDBServiceConfig> kairosdbs) {
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

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
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

    /**
     * get HttpClient with appropriate timeouts
     * @return
     */
    public CloseableHttpClient getHttpClient() {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(getSocketTimeout()).setConnectTimeout(getConnectTimeout()).build();
        return HttpClients.custom().setMaxConnPerRoute(maxConnectionsPerRoute).setMaxConnTotal(maxConnectionsTotal).setDefaultRequestConfig(config).build();
    }
}
