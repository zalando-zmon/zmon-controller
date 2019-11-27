package org.zalando.zmon.domain;

import java.util.HashMap;

public class ServiceLevelStatus {
    private ServiceLevelStatusData data;
    private String lastModified;
    private String lastModifiedBy;

    public ServiceLevelStatusData getData() {
        if (data == null) {
            return new ServiceLevelStatusData();
        }
        return data;
    }

    public void setData(ServiceLevelStatusData data) {
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


    public static class ServiceLevelStatusData {
        private int ingestMaxCheckTier;
        private int queryDistanceHoursLimit;
        private int queryMaxCheckTier;
        private int sampledCheckTier;
        private double sampledCheckRate;
        private String message;
        private final HashMap<Integer, String> checkTiers;

        public ServiceLevelStatusData() {
            this.checkTiers = new HashMap<>();
            this.checkTiers.put(2, "\"important\" and \"critical\"");
            this.checkTiers.put(1, "\"critical\"");
        }

        public String getMessage() {
            return message;
        }

        public void fillMessage() {
            if (isEnabled(this.queryMaxCheckTier) || isEnabled(this.ingestMaxCheckTier) || this.queryDistanceHoursLimit != 0) {
                this.message = "SERVICE DEGRADATION: " + this.getQueryPathMessage() + " " + this.getWritePathMessage();
                this.message = this.message.trim();
            } else {
                this.message = "";
            }
        }

        String getQueryPathMessage() {
            String message = "";

            if (isEnabled(this.queryMaxCheckTier)) {
                message += "Metrics visualization is currently only available for metrics classified as " + this.checkTiers.get(this.queryMaxCheckTier);
                if (this.queryDistanceHoursLimit == 0) {
                    message += ".";
                }
            }

            if (this.queryDistanceHoursLimit != 0) {
                if (isEnabled(this.queryMaxCheckTier)) {
                    message += " and is temporarily be limited to the last " + this.queryDistanceHoursLimit + " hours.";
                } else {
                    message += "Metrics visualization is temporarily limited to the last " + this.queryDistanceHoursLimit + " hours.";
                }
            }
            return message;
        }

        String getWritePathMessage() {
            String message = "";

            if (isEnabled(this.ingestMaxCheckTier)) {
                message += "Metrics storage & visualization is currently only enabled for metrics classified as " + this.checkTiers.get(this.ingestMaxCheckTier) + ".";
            }
            return message;
        }

        private Boolean isEnabled(final Integer serviceDegradationLevelValue) {
            return serviceDegradationLevelValue == 2 || serviceDegradationLevelValue == 1;
        }

        public Integer getIngestMaxCheckTier() {
            return ingestMaxCheckTier;
        }

        public void setIngestMaxCheckTier(Integer ingestMaxCheckTier) {
            this.ingestMaxCheckTier = ingestMaxCheckTier;
        }

        public Integer getQueryDistanceHoursLimit() {
            return queryDistanceHoursLimit;
        }

        public void setQueryDistanceHoursLimit(Integer queryDistanceHoursLimit) {
            this.queryDistanceHoursLimit = queryDistanceHoursLimit;
        }

        public Integer getQueryMaxCheckTier() {
            return queryMaxCheckTier;
        }

        public void setQueryMaxCheckTier(Integer queryMaxCheckTier) {
            this.queryMaxCheckTier = queryMaxCheckTier;
        }

        public Integer getSampledCheckTier() {
            return sampledCheckTier;
        }

        public void setSampledCheckTier(Integer sampledCheckTier) {
            this.sampledCheckTier = sampledCheckTier;
        }

        public Double getSampledCheckRate() {
            return sampledCheckRate;
        }

        public void setSampledCheckRate(Double sampledCheckRate) {
            this.sampledCheckRate = sampledCheckRate;
        }

        @Override
        public String toString() {
            return "ServiceLevelStatus{" +
                    "ingestMaxCheckTier=" + ingestMaxCheckTier +
                    ", queryDistanceHoursLimit=" + queryDistanceHoursLimit +
                    ", queryMaxCheckTier=" + queryMaxCheckTier +
                    ", message=" + message +
                    '}';
        }
    }
}

