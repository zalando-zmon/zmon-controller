package org.zalando.zmon.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.service.VisualizationService;

import java.util.Map;

@Service
public class Grafana implements VisualizationService {
    private final Logger log = LoggerFactory.getLogger(Grafana.class);

    @Autowired
    private ControllerProperties controllerProperties;

    @Override
    public String homeRedirect() {
        return "redirect:" + controllerProperties.visualizationHost;
    }

    @Override
    public String dynamicDashboardRedirect(Map<String, String> params) {
        String endpoint = "/dashboard/script/zmon-check.js";
        UriComponents uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(controllerProperties.visualizationHost)
                .path(endpoint)
                .queryParam("orgId", "1")
                .queryParam("checkId", params.containsKey("checkId") ? params.get("checkId") : "")
                .queryParam("entityName", params.containsKey("entityName") ? params.get("entityName") : "")
                .queryParam("checkName", params.containsKey("checkName") ? params.get("checkName") : "")
                .queryParam("refresh", "1m")
                .build();

        return "redirect:" + uri.toUriString();
    }

    @Override
    public void getAllDashboards() {

    }

    @Override
    public void getDashboard(String id) {

    }

    @Override
    public void createDashboard() {

    }

    @Override
    public void updateDashboard() {

    }

    @Override
    public void deleteDashboard(String id) {

    }
}
