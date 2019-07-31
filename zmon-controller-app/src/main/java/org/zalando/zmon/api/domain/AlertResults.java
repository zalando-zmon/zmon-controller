package org.zalando.zmon.api.domain;

import java.util.List;

public class AlertResults {
    private final List<AlertResult> alertResults;

    public AlertResults(List<AlertResult> alertResults) {
        this.alertResults = alertResults;
    }
}
