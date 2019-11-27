package org.zalando.zmon.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.zalando.zmon.domain.ServiceLevelStatus.ServiceLevelStatusData;

public class ServiceLevelStatusTest {
    @Test
    public void empty() {
        ServiceLevelStatusData data = new ServiceLevelStatusData();

        data.fillMessage();

        assertEquals(data.getMessage(), "");
    }

    @Test
    public void query_tierImportant() {
        ServiceLevelStatusData data = new ServiceLevelStatusData();
        data.setQueryMaxCheckTier(2);

        data.fillMessage();

        assertEquals(data.getMessage(),
                "SERVICE DEGRADATION: Metrics visualization is currently only available for metrics classified as \"important\" and \"critical\".");
    }

    @Test
    public void query_tierCritical() {
        ServiceLevelStatusData data = new ServiceLevelStatusData();
        data.setQueryMaxCheckTier(1);

        data.fillMessage();
        assertEquals(data.getMessage(),
                "SERVICE DEGRADATION: Metrics visualization is currently only available for metrics classified as \"critical\".");
    }

    @Test
    public void query_tierImportant_distanceLimited() {
        ServiceLevelStatusData data = new ServiceLevelStatusData();
        data.setQueryMaxCheckTier(2);
        data.setQueryDistanceHoursLimit(8);

        data.fillMessage();

        assertEquals(data.getMessage(),
                "SERVICE DEGRADATION: Metrics visualization is currently only available for metrics classified as \"important\" and \"critical\" and is temporarily be limited to the last 8 hours.");
    }

    @Test
    public void query_tierCritical_distanceLimited() {
        ServiceLevelStatusData data = new ServiceLevelStatusData();
        data.setQueryMaxCheckTier(1);
        data.setQueryDistanceHoursLimit(12);

        data.fillMessage();

        assertEquals(data.getMessage(),
                "SERVICE DEGRADATION: Metrics visualization is currently only available for metrics classified as \"critical\" and is temporarily be limited to the last 12 hours.");
    }

    @Test
    public void ingest_tierCritical() {
        ServiceLevelStatusData data = new ServiceLevelStatusData();
        data.setIngestMaxCheckTier(2);

        data.fillMessage();

        assertEquals(data.getMessage(),
                "SERVICE DEGRADATION:  Metrics storage & visualization is currently only enabled for metrics classified as \"important\" and \"critical\".");
    }

    @Test
    public void ingest_tierImportant() {
        ServiceLevelStatusData data = new ServiceLevelStatusData();
        data.setIngestMaxCheckTier(1);

        data.fillMessage();

        assertEquals(data.getMessage(),
                "SERVICE DEGRADATION:  Metrics storage & visualization is currently only enabled for metrics classified as \"critical\".");
    }

    @Test
    public void both_tierCritical() {
        ServiceLevelStatusData data = new ServiceLevelStatusData();
        data.setIngestMaxCheckTier(1);
        data.setQueryMaxCheckTier(1);

        data.fillMessage();

        assertEquals(data.getMessage(),
                "SERVICE DEGRADATION: Metrics visualization is currently only available for metrics classified as \"critical\". Metrics storage & visualization is currently only enabled for metrics classified as \"critical\".");
    }
}