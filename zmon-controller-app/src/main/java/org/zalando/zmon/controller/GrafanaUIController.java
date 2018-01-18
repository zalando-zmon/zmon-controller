package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zalando.zmon.config.AppdynamicsProperties;
import org.zalando.zmon.config.InstanaProperties;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.config.KairosDBProperties;
import org.zalando.zmon.config.LightstepProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GrafanaUIController {

    private AppdynamicsProperties appdynamicsProperties;
    private InstanaProperties instanaProperties;
    private LightstepProperties lightstepProperties;
    private ControllerProperties controllerProperties;

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
                               InstanaProperties instanaProperties,
                               LightstepProperties lightstepProperties) {
        this.controllerProperties = controllerProperties;
        this.appdynamicsProperties = appdynamicsProperties;
        this.instanaProperties = instanaProperties;
        this.lightstepProperties = lightstepProperties;

        for (KairosDBProperties.KairosDBServiceConfig c : kairosdbProperties.getKairosdbs()) {
            kairosdbServices.add(new KairosDBEntry(c.getName(), "/rest/kairosdbs/" + c.getName()));
        }
    }

    @RequestMapping(value = "/grafana")
    public String grafana(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        model.addAttribute(IndexController.KAIROSDB_SERVICES, kairosdbServices);
        model.addAttribute(IndexController.APPDYNAMICS_CONFIG, appdynamicsProperties);
        model.addAttribute(IndexController.APPDYNAMICS_ENABLED, controllerProperties.enableAppdynamics);
        model.addAttribute(IndexController.INSTANA_CONFIG, instanaProperties);
        model.addAttribute(IndexController.INSTANA_ENABLED, controllerProperties.enableInstana);
        model.addAttribute(IndexController.LIGHTSTEP_CONFIG, lightstepProperties);
        model.addAttribute(IndexController.LIGHTSTEP_ENABLED, controllerProperties.enableLightstep);

        return "grafana";
    }

    @RequestMapping(value = {"/grafana/dashboard/db/**", "/grafana/dashboard-solo/db/**"})
    public String grafanaDeepLinks(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        model.addAttribute(IndexController.KAIROSDB_SERVICES, kairosdbServices);
        model.addAttribute(IndexController.APPDYNAMICS_CONFIG, appdynamicsProperties);
        model.addAttribute(IndexController.APPDYNAMICS_ENABLED, controllerProperties.enableAppdynamics);
        model.addAttribute(IndexController.INSTANA_CONFIG, instanaProperties);
        model.addAttribute(IndexController.INSTANA_ENABLED, controllerProperties.enableInstana);
        model.addAttribute(IndexController.LIGHTSTEP_CONFIG, lightstepProperties);
        model.addAttribute(IndexController.LIGHTSTEP_ENABLED, controllerProperties.enableLightstep);

        return "grafana";
    }

    @RequestMapping(value = "/grafana2/**")
    public String grafana2Redirect(HttpServletRequest request) {
        return "redirect:" + request.getRequestURI().replace("/grafana2/", "/grafana/");
    }
}
