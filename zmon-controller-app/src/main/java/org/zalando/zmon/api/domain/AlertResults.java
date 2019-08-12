package org.zalando.zmon.api.domain;

import java.util.List;

public class AlertResults {
    private List<AlertResult> alertResults;

    public AlertResults(List<AlertResult> alertResults) {
        this.alertResults = alertResults;
    }

    public List<AlertResult> getAlertResults() {
        return alertResults;
    }

    public void setAlertResults(List<AlertResult> alertResults) {
        this.alertResults = alertResults;
    }
}
