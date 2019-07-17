package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zalando.zmon.config.ControllerProperties;

import javax.servlet.http.HttpServletRequest;

@Controller
public class VisualizationController {

    private ControllerProperties controllerProperties;

    @Autowired
    public VisualizationController(ControllerProperties controllerProperties) {
        this.controllerProperties = controllerProperties;
    }

    @RequestMapping(value = "/grafana")
    public String visualizationHomeRedirect(HttpServletRequest request) {
        return "redirect:" + controllerProperties.visualizationHost;
    }

    @RequestMapping(value = "/scripteddashboard")
    public String grafanaScriptedDashboardRedirect(HttpServletRequest request) {
        return "redirect:" + controllerProperties.visualizationHost;
    }
}
