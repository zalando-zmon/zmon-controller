package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.zmon.service.VisualizationService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping(value = "/visualization")
public class VisualizationController {
    private VisualizationService visualizationService;

    @Autowired
    public VisualizationController(VisualizationService visualizationService) {

        this.visualizationService = visualizationService;
    }

    @RequestMapping(value = "/")
    public String visualizationHomeRedirect(HttpServletRequest request) {
        return visualizationService.homeRedirect();
    }

    @RequestMapping(value = "/script")
    public String grafanaScriptedDashboardRedirect(HttpServletRequest request, @RequestParam Map<String, String> params) {
        return visualizationService.dynamicDashboardRedirect(params);
    }
}
