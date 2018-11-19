package org.zalando.zmon.config;

import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;
import java.util.List;

@ConfigurationProperties(prefix = "zmon.eventlog")
public class EventLogProperties {

    private URL url;

    private List<Integer> alertHistoryEventsFilter = Lists.newArrayList();

    private List<Integer> checkHistoryEventsFilter = Lists.newArrayList();

    private int connectTimeout;

    private int requestConnectTimeout;

    private int socketTimeout;
    private long connectionTimeToLive = 2 * 60 * 1000;


    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public List<Integer> getAlertHistoryEventsFilter() {
        return alertHistoryEventsFilter;
    }

    public void setAlertHistoryEventsFilter(final List<Integer> alertHistoryEventsFilter) {
        this.alertHistoryEventsFilter = alertHistoryEventsFilter;
    }

    public List<Integer> getCheckHistoryEventsFilter() {
        return checkHistoryEventsFilter;
    }

    public void setCheckHistoryEventsFilter(final List<Integer> checkHistoryEventsFilter) {
        this.checkHistoryEventsFilter = checkHistoryEventsFilter;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getRequestConnectTimeout() {
        return requestConnectTimeout;
    }

    public void setRequestConnectTimeout(final int requestConnectTimeout) {
        this.requestConnectTimeout = requestConnectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(final int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public long getConnectionTimeToLive() {
        return connectionTimeToLive;
    }

    public void setConnectionTimeToLive(long connectionTimeToLive) {
        this.connectionTimeToLive = connectionTimeToLive;
    }
}
