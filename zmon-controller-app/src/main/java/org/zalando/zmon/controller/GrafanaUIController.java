package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zalando.zmon.api.domain.ResourceNotFoundException;
import org.zalando.zmon.config.AppdynamicsProperties;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.config.EumTracingProperties;
import org.zalando.zmon.config.KairosDBProperties;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.persistence.GrafanaDashboardSprocService;
import org.zalando.zmon.service.VisualizationService;
import org.zalando.zmon.service.ZMonService;
import org.zalando.zmon.service.impl.Grafana;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
public class GrafanaUIController {

    private AppdynamicsProperties appdynamicsProperties;
    private ControllerProperties controllerProperties;
    private EumTracingProperties eumTracingProperties;
    private GrafanaDashboardSprocService grafanaService;
    private VisualizationService visualizationService;
    private ZMonService zMonService;

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
                               GrafanaDashboardSprocService grafanaService,
                               VisualizationService visualizationService,
                               ZMonService zMonService) {
        this.controllerProperties = controllerProperties;
        this.appdynamicsProperties = appdynamicsProperties;
        this.eumTracingProperties = eumTracingProperties;
        this.grafanaService = grafanaService;
        this.visualizationService = visualizationService;
        this.zMonService = zMonService;

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

    @RequestMapping(value = "/grafana3")
    public String grafana(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        model.addAttribute(IndexController.KAIROSDB_SERVICES, kairosdbServices);
        model.addAttribute(IndexController.APPDYNAMICS_CONFIG, appdynamicsProperties);
        model.addAttribute(IndexController.APPDYNAMICS_ENABLED, controllerProperties.enableAppdynamics);
        model.addAttribute(IndexController.EUM_TRACING_ENABLED, controllerProperties.enableEumTracing);
        model.addAttribute(IndexController.EUM_GRAFANA_TRACING_CONFIG, eumTracingProperties.grafanaConfig);
        return "grafana";
    }

    @RequestMapping(value = {"/grafana3/dashboard/db/**"})
    public String grafanaDeepLinks(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        model.addAttribute(IndexController.KAIROSDB_SERVICES, kairosdbServices);
        model.addAttribute(IndexController.APPDYNAMICS_CONFIG, appdynamicsProperties);
        model.addAttribute(IndexController.APPDYNAMICS_ENABLED, controllerProperties.enableAppdynamics);
        model.addAttribute(IndexController.EUM_TRACING_ENABLED, controllerProperties.enableEumTracing);
        model.addAttribute(IndexController.EUM_GRAFANA_TRACING_CONFIG, eumTracingProperties.grafanaConfig);
        return "grafana";
    }

    @RequestMapping(value = "/grafana6/dashboard/db/{id}")
    public String grafana6Redirect(HttpServletRequest request, @PathVariable(value = "id") String id) {
        if (id.startsWith("zmon-check-")) {
            String[] parts = id.split("-", 4);
            String checkId = parts[2];
            final Optional<String> entityId = parts.length > 3 ? Optional.of(parts[3]) : Optional.empty();
            Optional<CheckDefinition> checkDefinitionOptional = zMonService.getCheckDefinitionById(Integer.valueOf(checkId));
            if (checkDefinitionOptional.isPresent()) {
                return visualizationService.dynamicDashboardRedirect(new HashMap<String, String>() {{
                    this.put("checkId", checkId);
                    this.put("entityName", entityId.orElse(""));
                    this.put("checkName", checkDefinitionOptional.get().getName());
                }});
            }
        }

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
