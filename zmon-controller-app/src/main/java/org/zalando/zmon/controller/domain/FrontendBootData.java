package org.zalando.zmon.controller.domain;

import java.util.List;

public class FrontendBootData {
    private List<Integer> subMinuteChecks;

    public List<Integer> getSubMinuteChecks() {
        return subMinuteChecks;
    }
    public void setSubMinuteChecks(List<Integer> subMinuteChecks) {
        this.subMinuteChecks = subMinuteChecks;
    }
}
