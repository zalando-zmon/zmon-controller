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
        public static final String SECOND_LEVEL_WARNING = "Metrics visualization is currently only enabled for metrics classified as \"important\" or \"critical\" and is temporarily limited to a 12 hour span.";
        public static final String FIRST_LEVEL_WARNING = "Metrics visualization is currently limited to a 12 hour span.";
        public static final String THIRD_LEVEL_WARNING = "Metrics visualization & storage is currently only available for metrics classified as \"important\" or \"critical\" and is limited to a 12 hour span.";
        public static final String FOURTH_LEVEL_WARNING = "Storage of metrics is currently only available for metrics classified as \"important\" or \"critical\". Metrics visualization is currently only enabled for metrics classified as \"critical\" and is limited to a 12 hour span.";
        public static final String FIFTH_LEVEL_WARNING = "Metrics visualization & storage is currently only available for metrics classified as \"critical\" and is limited to a 12 hour span.";

        private Integer ingestMaxCheckTier = 3;
        private Integer queryDistanceHoursLimit = 0;
        private Integer queryMaxCheckTier = 3;
        private final HashMap<Integer, String> checkTiers;
        private String message;

        public ServiceLevelStatusData() {
            this.checkTiers = new HashMap<>();
            this.checkTiers.put(2, "metrics classified as \"important\" and \"critical\"");
            this.checkTiers.put(1, "metrics classified as \"critical\"");
        }

        public String getMessage() {
            return message;
        }

        public void fillMessage() {
            this.message = "";

            if (this.queryMaxCheckTier != 3 || this.ingestMaxCheckTier != 3 || this.queryDistanceHoursLimit != 0) {
                this.message = "SERVICE DEGRADATION: ";
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 3 && this.ingestMaxCheckTier == 3) {
                // level 1
                this.message += FIRST_LEVEL_WARNING;
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 3 && this.ingestMaxCheckTier == 2) {
                // level 2
                this.message += SECOND_LEVEL_WARNING;
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 2 && this.ingestMaxCheckTier == 2) {
                // level 3
                this.message += THIRD_LEVEL_WARNING;
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 3 && this.ingestMaxCheckTier == 2) {
                // level 4
                this.message += FOURTH_LEVEL_WARNING;
            }

            if (this.queryDistanceHoursLimit == 12 && this.ingestMaxCheckTier == 3 && this.ingestMaxCheckTier == 2) {
                // level 5
                this.message += FIFTH_LEVEL_WARNING;
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


        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ServiceLevelStatus{");
            sb.append("ingestMaxCheckTier=").append(ingestMaxCheckTier);
            sb.append(", queryDistanceHoursLimit=").append(queryDistanceHoursLimit);
            sb.append(", queryMaxCheckTier=").append(queryMaxCheckTier);
            sb.append(", message=").append(message);
            sb.append('}');
            return sb.toString();
        }
    }
}

