package org.zalando.zmon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
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
        public String registrationToken;
    }

    public static class TeamRegistrationBody {
        public String team;
    }

    public static class AlertRegistrationBody {
        public String alertId;
    }

    @ResponseBody
    @RequestMapping(path="/devices", method=RequestMethod.POST)
    public void registerDevice(@RequestBody DeviceRegistrationBody body) throws IOException {
        log.info("Registering device for user: registrationToken={} user={}", body.registrationToken, authorityService.getUserName());

        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/devices";
        HttpEntity entity = new StringEntity(mapper.writeValueAsString(body), "UTF-8");
        Request request = Request.Post(url).body(entity).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        Response r = Executor.newInstance().execute(request);
    }

    @ResponseBody
    @RequestMapping(path="/devices/{registrationToken}", method=RequestMethod.DELETE)
    public void unregisterDevice(@RequestParam(name="registrationToken") String registrationToken) throws IOException {
        final String url = config.getUrl() + "/api/v1/device/" + registrationToken;
        Request request = Request.Delete(url).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        Executor.newInstance().execute(request);
    }

    @ResponseBody
    @RequestMapping(path="/teams", method=RequestMethod.POST)
    public void subscribeToTeam(@RequestBody TeamRegistrationBody body) throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/teams";
        HttpEntity entity = new StringEntity(mapper.writeValueAsString(body), "UTF-8");
        Request request = Request.Post(url).body(entity).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        Executor.newInstance().execute(request);
    }

    @ResponseBody
    @RequestMapping(path="/teams", method=RequestMethod.GET)
    public String getSubscribedTeams() throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/teams";
        Request request = Request.Get(url).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service");
        Response r = Executor.newInstance().execute(request);
        return r.returnContent().asString();
    }

    @ResponseBody
    @RequestMapping(path="/teams/{team}", method=RequestMethod.DELETE)
    public void unsubscribeTeam(@RequestParam(name="team") String team) throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/teams/" + team;
        Request request = Request.Delete(url).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        Executor.newInstance().execute(request);
    }

    @ResponseBody
    @RequestMapping(path="/alerts", method=RequestMethod.GET)
    public String getSubscribedAlerts() throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/alerts";
        Request request = Request.Get(url).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service");
        Response r = Executor.newInstance().execute(request);
        return r.returnContent().asString();
    }

    @ResponseBody
    @RequestMapping(path="/alerts", method=RequestMethod.POST)
    public void subscribeToAlert(@RequestBody AlertRegistrationBody body) throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/alerts";
        HttpEntity entity = new StringEntity(mapper.writeValueAsString(body), "UTF-8");
        Request request = Request.Post(url).body(entity).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        Executor.newInstance().execute(request);
    }
}
