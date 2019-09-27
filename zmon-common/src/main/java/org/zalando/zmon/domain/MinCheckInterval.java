package org.zalando.zmon.domain;

import java.util.List;

public class MinCheckInterval {
    private Data data = null;
    private String lastModified;
    private String lastModifiedBy;

    public static class Data {
        private List<Integer> whitelistedChecks;
        private Integer minCheckInterval = 60;
        private Integer minWhitelistedCheckInterval = 15;

        public List<Integer> getWhitelistedChecks() {
            return whitelistedChecks;
        }

        public void setWhitelistedChecks(List<Integer> whitelistedChecks) {
            this.whitelistedChecks = whitelistedChecks;
        }

        public Integer getMinCheckInterval() {
            return minCheckInterval;
        }

        public void setMinCheckInterval(Integer minCheckInterval) {
            this.minCheckInterval = minCheckInterval;
        }

        public Integer getMinWhitelistedCheckInterval() {
            return minWhitelistedCheckInterval;
        }

        public void setMinWhitelistedCheckInterval(Integer minWhitelistedCheckInterval) {
            this.minWhitelistedCheckInterval = minWhitelistedCheckInterval;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}
