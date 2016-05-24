package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.config.KairosDBProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jmussler on 25.02.16.
 */

@Controller
public class GrafanaUIController {

    private ControllerProperties controllerProperties;

    private KairosDBProperties kairosdbProperties;

    public static class KairosDBEntry {
        public String name;
        public String url;

        public KairosDBEntry(String n, String u) {
            name = n;
            url = u;
        }
    }

    private final List<KairosDBEntry> kairosdbServices = new ArrayList<>();

    @Autowired
    public GrafanaUIController(KairosDBProperties kairosdbProperties, ControllerProperties controllerProperties) {
        this.kairosdbProperties = kairosdbProperties;
        this.controllerProperties = controllerProperties;

        for(KairosDBProperties.KairosDBServiceConfig c : kairosdbProperties.getKairosdbs()) {
            kairosdbServices.add(new KairosDBEntry(c.getName(), "/rest/kairosdbs/" + c.getName() + "/api/v1/"));
        }
    }

    @RequestMapping(value = "/grafana")
    public String grafana(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        model.addAttribute(IndexController.KAIROSDB_SERVICES, kairosdbServices);
        return "grafana";
    }

    @RequestMapping(value = "/grafana/dashboard/db/**")
    public String grafanaDeepLinks(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        model.addAttribute(IndexController.KAIROSDB_SERVICES, kairosdbServices);
        return "grafana";
    }

    @RequestMapping(value = "/grafana2")
    public String grafana2(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        model.addAttribute(IndexController.KAIROSDB_SERVICES, kairosdbServices);
        return "grafana2";
    }

    @RequestMapping(value = "/grafana2/dashboard/db/**")
    public String grafana2DeepLinks(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        model.addAttribute(IndexController.KAIROSDB_SERVICES, kairosdbServices);
        return "grafana2";
    }
}
