package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zalando.zmon.api.domain.ResourceNotFoundException;
import org.zalando.zmon.config.AppdynamicsProperties;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.config.EumTracingProperties;
import org.zalando.zmon.config.KairosDBProperties;
import org.zalando.zmon.persistence.GrafanaDashboardSprocService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GrafanaUIController {

    private AppdynamicsProperties appdynamicsProperties;
    private ControllerProperties controllerProperties;
    private EumTracingProperties eumTracingProperties;
    private GrafanaDashboardSprocService grafanaService;

    public static class KairosDBEntry {
        public String name;
        public String url;

        public KairosDBEntry(String n, String u) {
            name = n;
            url = u;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }

    private final List<KairosDBEntry> kairosdbServices = new ArrayList<>();

    @Autowired
    public GrafanaUIController(KairosDBProperties kairosdbProperties,
                               ControllerProperties controllerProperties,
                               AppdynamicsProperties appdynamicsProperties,
                               EumTracingProperties eumTracingProperties,
                               GrafanaDashboardSprocService grafanaService) {
        this.controllerProperties = controllerProperties;
        this.appdynamicsProperties = appdynamicsProperties;
        this.eumTracingProperties = eumTracingProperties;
        this.grafanaService = grafanaService;

        for (KairosDBProperties.KairosDBServiceConfig c : kairosdbProperties.getKairosdbs()) {
            kairosdbServices.add(new KairosDBEntry(c.getName(), "/rest/kairosdbs/" + c.getName()));
        }
    }

    @RequestMapping(value = "/grafana")
    public String grafana(HttpServletRequest request) {
        return "redirect:" + request.getRequestURI().replace("/grafana", "/grafana6");
    }

    @RequestMapping(value = {"/grafana/dashboard/db/**", "/grafana/dashboard-solo/db/**"})
    public String grafanaDeepLinks(HttpServletRequest request) {
        String redirect = request.getRequestURI().replace("/grafana/", "/grafana6/");
        String query = request.getQueryString();
        if (null != query) {
            redirect += "?" + query;
        }
        return "redirect:" + redirect;
    }

    @RequestMapping(value = "/grafana2/**")
    public String grafana2Redirect(HttpServletRequest request) {
        return "redirect:" + request.getRequestURI().replace("/grafana2/", "/grafana/");
    }

    @RequestMapping(value = "/grafana6/dashboard/db/{id}")
    public String grafana6Redirect(HttpServletRequest request, @PathVariable(value = "id") String id) {
        String uid = grafanaService.getGrafanaMapping(id);
        if (null == uid) {
            throw new ResourceNotFoundException();
        }
        String redirect = controllerProperties.grafanaHost + "/d/" + uid;
        String query = request.getQueryString();
        if (null != query) {
            redirect += "?" + query;
        }
        return "redirect:" + redirect;
    }

    @RequestMapping(value = "/grafana6")
    public String grafana6HomeRedirect(HttpServletRequest request) {
        return "redirect:" + controllerProperties.grafanaHost;
    }
}
