package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by hjacobs on 2/5/16.
 */
@ConfigurationProperties(prefix = "zmon.notificationservice")
public class NotificationServiceProperties {

    private String url = "http://zmon-notification-service:8095";
    private int socketTimeout = 5000;
    private int connectTimeout = 1000;
    private int maxConnPerRoute = 100;
    private int maxConnTotal = 200;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public int getMaxConnTotal() {
        return maxConnTotal;
    }

    public void setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }
}
