package org.zalando.zmon.domain;

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
        static final String THIRD_LEVEL_WARNING = "Metrics storage for metrics classified as \"other\" is currently sampled by 50%. Metrics visualization is currently only enabled for metrics classified as \"important\" or \"critical\" and is temporarily limited to the last 12 hours.";
        static final String FOURTH_LEVEL_WARNING = "Metrics visualization & storage is currently only available for metrics classified as \"important\" or \"critical\" and is temporarily limited to the last 12 hours.";
        static final String FIFTH_LEVEL_WARNING = "Storage of metrics is currently only available for metrics classified as \"important\" or \"critical\". Metrics visualization is currently only enabled for metrics classified as \"critical\" and is temporarily limited to the last 12 hours.";
        static final String SIXTH_LEVEL_WARNING = "Metrics visualization & storage is currently only available for metrics classified as \"critical\" and is temporarily limited to the last 12 hours.";

        private Integer ingestMaxCheckTier = 3;
        private Integer queryDistanceHoursLimit = 0;
        private Integer queryMaxCheckTier = 3;
        private Integer sampledCheckTier = 0;
        private Double sampledCheckRate = 0d;
        private String message;

        public String getMessage() {
            return message;
        }

        public void fillMessage() {
            this.message = "";

            if (this.queryMaxCheckTier != 3 || this.ingestMaxCheckTier != 3 || this.queryDistanceHoursLimit != 0) {
                this.message = "SERVICE DEGRADATION: ";
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 3 && this.queryMaxCheckTier == 3) {
                this.message += FIRST_LEVEL_WARNING;
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 3 && this.queryMaxCheckTier == 2 && this.sampledCheckTier == 0) {
                this.message += SECOND_LEVEL_WARNING;
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 3 && this.queryMaxCheckTier == 2 && this.sampledCheckTier == 3) {
                this.message += THIRD_LEVEL_WARNING;
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 2 && this.queryMaxCheckTier == 2) {
                this.message += FOURTH_LEVEL_WARNING;
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 2 && this.queryMaxCheckTier == 1) {
                this.message += FIFTH_LEVEL_WARNING;
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 1 && this.queryMaxCheckTier == 1) {
                this.message += SIXTH_LEVEL_WARNING;
            }
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

