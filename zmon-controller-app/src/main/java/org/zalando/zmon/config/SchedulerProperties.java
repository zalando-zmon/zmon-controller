package org.zalando.zmon.config;

import io.opentracing.contrib.apache.http.client.TracingHttpClientBuilder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by hjacobs on 2/5/16.
 */
@ConfigurationProperties(prefix = "zmon.scheduler")
public class SchedulerProperties {

    private URL url;

    private int connectTimeout = 1000; // 1 second
    private int socketTimeout = 5000; // 5 seconds
    private int maxConnectionsPerRoute = 100;
    private int maxConnectionsTotal = 200;
    private long connectionTimeToLive = 2 * 60 * 1000; // 2 minutes

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
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

    public long getConnectionTimeToLive() {
        return connectionTimeToLive;
    }

    public void setConnectionTimeToLive(long connectionTimeToLive) {
        this.connectionTimeToLive = connectionTimeToLive;
    }

    /**
     * get HttpClient with appropriate timeouts and TTL
     * @return CloseableHttpClient
     */
    public CloseableHttpClient getHttpClient() {
        RequestConfig config = RequestConfig.custom().
                setSocketTimeout(getSocketTimeout()).
                setConnectTimeout(getConnectTimeout()).
                build();
        return new TracingHttpClientBuilder().
                setMaxConnPerRoute(maxConnectionsPerRoute).
                setMaxConnTotal(maxConnectionsTotal).
                setConnectionTimeToLive(getConnectionTimeToLive(), TimeUnit.MILLISECONDS).
                setDefaultRequestConfig(config).build();
    }
}
