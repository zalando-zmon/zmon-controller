package de.zalando.zmon.controller;

import de.zalando.zmon.security.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GrafanaController {

    @Autowired
    public AuthorityService authService;

    @ModelAttribute(value="authService")
    public AuthorityService getAuthService() {
        return authService;
    }

    @RequestMapping(value = "/grafana")
    public String grafana() {
        return "grafana";
    }

    @RequestMapping(value = "/grafana2")
    public String grafanaTwo() {
        return "grafana2";
    }
}
