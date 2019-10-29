package org.zalando.zmon.domain;

public class ServiceLevelStatus {
    private ServiceLevelStatusData data;
    private String lastModified;
    private String lastModifiedBy;

    public ServiceLevelStatusData getData() {
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
            sb.append('}');
            return sb.toString();
        }
    }
}

