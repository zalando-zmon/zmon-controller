package org.zalando.zmon.controller.domain;

import org.zalando.zmon.domain.MinCheckInterval;

public class FrontendBootData {
    private MinCheckInterval.Data minCheckInterval;

    public MinCheckInterval.Data getMinCheckInterval() {
        return minCheckInterval;
    }
    public void setMinCheckInterval(MinCheckInterval.Data minCheckInterval) {
        this.minCheckInterval = minCheckInterval;
    }
}
