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
        static final String FIRST_LEVEL_WARNING = "Metrics visualization is currently limited to the last 12 hours.";
        static final String SECOND_LEVEL_WARNING = "Metrics visualization is currently only enabled for metrics classified as \"important\" or \"critical\" and is temporarily limited to the last 12 hours.";
        static final String THIRD_LEVEL_WARNING = "Storage of metrics classified as \"other\" is currently sampled by 50%. Metrics visualization is currently only enabled for metrics classified as \"important\" or \"critical\" and is temporarily limited to the last 12 hours.";
        static final String FOURTH_LEVEL_WARNING = "Metrics visualization & storage is currently only available for metrics classified as \"important\" or \"critical\" and is temporarily limited to the last 12 hours.";
        static final String FIFTH_LEVEL_WARNING = "Storage of metrics is currently only available for metrics classified as \"important\" or \"critical\". Metrics visualization is currently only enabled for metrics classified as \"critical\" and is temporarily limited to the last 12 hours.";
        static final String SIXTH_LEVEL_WARNING = "Metrics visualization & storage is currently only available for metrics classified as \"critical\" and is temporarily limited to the last 12 hours.";

        private Integer ingestMaxCheckTier = 0;
        private Integer queryDistanceHoursLimit = 0;
        private Integer queryMaxCheckTier = 0;
        private Integer sampledCheckTier = 0;
        private Double sampledCheckRate = 0d;
        private String message;
        private final HashMap<Integer, String> checkTiers;

        public ServiceLevelStatusData(){
            this.checkTiers = new HashMap<>();
            this.checkTiers.put(2, "\"important\" and \"critical\"");
            this.checkTiers.put(1, "\"critical\"");
        }

        public String getMessage() {
            return message;
        }

        public void fillMessage() {
            if (isEnabled(this.queryMaxCheckTier) || isEnabled(this.ingestMaxCheckTier) || this.queryDistanceHoursLimit != 0) {
                this.message = "SERVICE DEGRADATION: " + this.getQueryPathMessage() + " " +this.getWritePathMessage();
            } else {
                this.message = "";
            }
        }

        private String getQueryPathMessage() {
            String message = "";

            if (isEnabled(this.queryMaxCheckTier)) {
                message += "Metrics visualization is only available for metrics classified as " + this.checkTiers.get(this.queryMaxCheckTier);
                if (this.queryDistanceHoursLimit == 0) {
                    message += ".";
                }
            }

            if (this.queryDistanceHoursLimit != 0) {
                if (isEnabled(this.queryMaxCheckTier)) {
                    message += " and will temporarily be limited to " + this.queryDistanceHoursLimit + " hours.";
                } else {
                    message += "Metrics visualization is temporarily limited to the last " + this.queryDistanceHoursLimit + " hours.";
                }
            }
            return message;
        }

        private String getWritePathMessage() {
            String message = "";

            if (isEnabled(this.ingestMaxCheckTier)) {
                message += "Only metrics classified as " + this.checkTiers.get(this.ingestMaxCheckTier) + " are being stored.";
            }
            return message;
        }

        private Boolean isEnabled(final Integer serviceDegradationLevelValue)  {
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

