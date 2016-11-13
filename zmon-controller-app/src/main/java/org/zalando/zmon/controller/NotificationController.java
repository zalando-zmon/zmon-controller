package org.zalando.zmon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zmon.config.NotificationServiceProperties;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import java.io.IOException;

/**
 * Created by jmussler on 09.11.16.
 */

@Controller
@RequestMapping(value = "/rest/notifications")
public class NotificationController {

    private final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private DefaultZMonPermissionService authorityService;

    @Autowired
    NotificationServiceProperties config;

    @Autowired
    AccessTokens accessTokens;

    @Autowired
    ObjectMapper mapper;

    public NotificationController() {

    }

    public static class DeviceRegistrationBody {
        public String deviceToken;
    }

    public static class TeamRegistrationBody {
        public String team;
    }

    @ResponseBody
    @RequestMapping(path="/devices", method=RequestMethod.POST)
    public void registerDevice(@RequestBody DeviceRegistrationBody body) throws IOException {
        log.info("Registering device for user: deviceToken={} user={}", body.deviceToken, authorityService.getUserName());

        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/devices";
        HttpEntity entity = new StringEntity(mapper.writeValueAsString(body), "UTF-8");
        Request request = Request.Post(url).body(entity).addHeader("Authorization", "Bearer: " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        Executor.newInstance().execute(request);
    }

    @ResponseBody
    @RequestMapping(path="/devices/{deviceId}", method=RequestMethod.DELETE)
    public void unregisterDevice(@RequestParam(name="deviceId") String deviceId) throws IOException {
        final String url = config.getUrl() + "/api/v1/device/" + deviceId;
        Request request = Request.Delete(url).addHeader("Authorization", "Bearer: " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        Executor.newInstance().execute(request);
    }

    @ResponseBody
    @RequestMapping(path="/teams", method=RequestMethod.POST)
    public void subscribeToTeam(@RequestBody TeamRegistrationBody body) throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/teams";
        HttpEntity entity = new StringEntity(mapper.writeValueAsString(body), "UTF-8");
        Request request = Request.Post(url).body(entity).addHeader("Authorization", "Bearer: " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        Executor.newInstance().execute(request);
    }

    @ResponseBody
    @RequestMapping(path="/teams/{team}", method=RequestMethod.DELETE)
    public void unsubscribeTeam(@RequestParam(name="team") String team) throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/teams/" + team;
        Request request = Request.Delete(url).addHeader("Authorization", "Bearer: " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        Executor.newInstance().execute(request);
    }
}
