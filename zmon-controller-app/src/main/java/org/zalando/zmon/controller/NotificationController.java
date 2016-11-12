package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.zalando.zmon.config.NotificationServiceProperties;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jmussler on 09.11.16.
 */

@Controller
@RequestMapping(value = "/rest/notifications")
public class NotificationController {

    @Autowired
    private DefaultZMonPermissionService authorityService;

    @Autowired
    NotificationServiceProperties config;

    RestTemplate restTemplate;

    public NotificationController() {
        restTemplate = new RestTemplate();
    }

    @ResponseBody
    @RequestMapping(path="/**")
    public String mirrorRest(@RequestBody String body, HttpMethod method, HttpServletRequest request,
                             HttpServletResponse response) throws URISyntaxException
    {

        URI uri = new URI(config.getScheme(), null, config.getHost(), config.getPort(), request.getRequestURI(), request.getQueryString(), null);

        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, method, new HttpEntity<>(body), String.class);

        return responseEntity.getBody();
    }
}
