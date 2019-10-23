package org.zalando.zmon.domain;

import java.util.Collections;
import java.util.List;

public class MinCheckInterval {
    private MinCheckIntervalData data;
    private String lastModified;
    private String lastModifiedBy;

    public static class MinCheckIntervalData {
        private List<Integer> whitelistedChecks = Collections.emptyList();
        private Integer minCheckInterval = 5;
        private Integer minWhitelistedCheckInterval = 5;

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

    public MinCheckIntervalData getData() {
        return data;
    }

    public void setData(MinCheckIntervalData data) {
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
