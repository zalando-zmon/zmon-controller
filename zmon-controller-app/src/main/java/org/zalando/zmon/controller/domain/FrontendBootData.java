package org.zalando.zmon.controller.domain;

import org.zalando.zmon.domain.MinCheckInterval;

public class FrontendBootData {
    private MinCheckInterval.MinCheckIntervalData minCheckInterval;

    public MinCheckInterval.MinCheckIntervalData getMinCheckInterval() {
        return minCheckInterval;
    }
    public void setMinCheckInterval(MinCheckInterval.MinCheckIntervalData minCheckInterval) {
        this.minCheckInterval = minCheckInterval;
    }
}
