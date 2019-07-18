package org.zalando.zmon.api.domain;

import java.util.List;

public class AlertInstances {
    private final List<AlertInstance> alertInstances;

    public AlertInstances(List<AlertInstance> alertInstances) {
        this.alertInstances = alertInstances;
    }
}