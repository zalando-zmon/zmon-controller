package org.zalando.zmon.service;

import java.util.Map;

public interface VisualizationService {
    String homeRedirect();

    String dynamicDashboardRedirect(Map<String, String> params);

    void getAllDashboards();

    void getDashboard(String id);

    void createDashboard();

    void updateDashboard();

    void deleteDashboard(String id);
}
