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
        private Integer ingestMaxCheckTier = 3;
        private Integer queryDistanceHoursLimit = 0;
        private Integer queryMaxCheckTier = 3;
        private final HashMap<Integer, String> checkTiers;

        public ServiceLevelStatusData() {
            this.checkTiers = new HashMap<>();
            this.checkTiers.put(2, "metrics classified as \"important\" and \"critical\"");
            this.checkTiers.put(1, "metrics classified as \"critical\"");
        }

        public String getMessage() {
            return message;
        }

        public void setMessage() {
            this.message = "";

            if (this.ingestMaxCheckTier != 3 && this.queryMaxCheckTier != 3) {
                this.message = "Metric visualization is currently only available for " + this.checkTiers.get(this.queryMaxCheckTier) + ", " +
                        "metric storage only for " +  this.checkTiers.get(this.ingestMaxCheckTier);
            }

            if (this.ingestMaxCheckTier != 3 && this.queryMaxCheckTier == 3) {
                this.message = "Metric visualization is currently only available for " + this.checkTiers.get(this.ingestMaxCheckTier) + " ";
            }

            if (this.ingestMaxCheckTier == 3 && this.queryMaxCheckTier != 3) {
                this.message = "Metric storage is currently only available for " + this.checkTiers.get(this.ingestMaxCheckTier) + " ";
            }

            if (this.queryDistanceHoursLimit != 0) {
                if (this.ingestMaxCheckTier != 3 || this.queryMaxCheckTier != 3) {
                    this.message += " and ";
                } else {
                  this.message = "Metric visualization ";
                }
                this.message += "is temporarily limited to a " + this.queryDistanceHoursLimit + " hour span.";
            }

        }

        private String message;

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

