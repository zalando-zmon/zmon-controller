package de.zalando.zmon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jmussler on 25.02.16.
 */
@Controller
public class GrafanaUIController {
    @RequestMapping(value = "/grafana")
    public String grafana() {
        return "grafana";
    }

    @RequestMapping(value = "/grafana2")
    public String grafana2() {
        return "grafana2";
    }

    @RequestMapping(value = "/grafana2/dashboard/db/**")
    public String grafana2DeepLinks() {
        return "grafana2";
    }
}
