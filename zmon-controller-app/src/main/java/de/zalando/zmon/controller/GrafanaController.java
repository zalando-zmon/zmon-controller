package de.zalando.zmon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GrafanaController {

    @RequestMapping(value = "/grafana")
    public String grafana() {
        return "grafana";
    }

    @RequestMapping(value = "/grafana2")
    public String grafanaTwo() {
        return "grafana2";
    }

    @RequestMapping(value = "/grafana2/dashboard/db/**")
    public String grafana2DirectLinks() {
        return "grafana2";
    }
}
