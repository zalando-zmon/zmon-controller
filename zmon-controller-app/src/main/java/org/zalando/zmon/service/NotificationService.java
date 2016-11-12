package org.zalando.zmon.service;

import org.apache.http.client.fluent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.zmon.config.NotificationServiceProperties;

/**
 * Created by jmussler on 09.11.16.
 */
@Service
public class NotificationService {

    private final NotificationServiceProperties config;
    private final Executor executor;

    @Autowired
    public NotificationService(NotificationServiceProperties config) {
        this.config = config;
        this.executor = Executor.newInstance(config.getHttpClient());
    }

    public boolean register(String user, String deviceId) {
        return false;
    }

    public boolean subscribe(String user, int alertId) {
        return false;
    }

    public boolean unsubscribe(String user, int alertId) {
        return false;
    }
}
