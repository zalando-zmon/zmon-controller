package de.zalando.zmon.controller;

import de.zalando.zmon.config.ControllerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jmussler on 25.02.16.
 */
@Controller
public class GrafanaUIController {
    @Autowired
    private ControllerProperties controllerProperties;

    @RequestMapping(value = "/grafana")
    public String grafana(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        return "grafana";
    }

    @RequestMapping(value = "/grafana2")
    public String grafana2(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        return "grafana2";
    }

    @RequestMapping(value = "/grafana2/dashboard/db/**")
    public String grafana2DeepLinks(Model model) {
        model.addAttribute(IndexController.STATIC_URL, controllerProperties.getStaticUrl());
        return "grafana2";
    }
}
