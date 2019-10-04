package org.zalando.zmon.controller.domain;

import java.util.List;

public class FrontendBootData {
    public class CheckData {
        private MinInterval minInterval = new MinInterval();

        public MinInterval getMinInterval() {
            return minInterval;
        }

        public void setMinInterval(MinInterval minInterval) {
            this.minInterval = minInterval;
        }
    }

    public class MinInterval {
        private Integer normal;
        private Integer whitelisted;
        private List<Integer> whitelistedChecks;

        public Integer getNormal() {
            return normal;
        }

        public void setNormal(Integer normal) {
            this.normal = normal;
        }

        public Integer getWhitelisted() {
            return whitelisted;
        }

        public void setWhitelisted(Integer whitelisted) {
            this.whitelisted = whitelisted;
        }

        public List<Integer> getWhitelistedChecks() {
            return whitelistedChecks;
        }

        public void setWhitelistedChecks(List<Integer> whitelistedChecks) {
            this.whitelistedChecks = whitelistedChecks;
        }
    }

    private CheckData check = new CheckData();

    public CheckData getCheck() {
        return check;
    }

    public void setCheck(CheckData check) {
        this.check = check;
    }
}
