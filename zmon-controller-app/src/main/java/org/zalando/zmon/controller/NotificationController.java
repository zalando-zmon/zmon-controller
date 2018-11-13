package org.zalando.zmon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zmon.config.NotificationServiceProperties;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jmussler on 09.11.16.
 */

@Controller
@RequestMapping(value = "/rest/notifications")
public class NotificationController {

    private final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final DefaultZMonPermissionService authorityService;

    private final NotificationServiceProperties config;

    private final AccessTokens accessTokens;

    private final ObjectMapper mapper;

    private final Executor executor;

    @Autowired
    public NotificationController(DefaultZMonPermissionService authorityService,
                                  NotificationServiceProperties config,
                                  AccessTokens accessTokens,
                                  ObjectMapper mapper,
                                  HttpClient httpClient) {
        this.authorityService = authorityService;
        this.config = config;
        this.accessTokens = accessTokens;
        this.mapper = mapper;
        this.executor = Executor.newInstance(httpClient);
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

    public static class PriorityBody {
        public int priority;
    }

    @ResponseBody
    @RequestMapping(path = "/devices", method = RequestMethod.POST)
    public void registerDevice(@RequestBody DeviceRegistrationBody body) throws IOException {
        log.info("Registering device for user: registrationToken={} user={}", body.registrationToken.substring(0, 5), authorityService.getUserName());

        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/devices";
        HttpEntity entity = new StringEntity(mapper.writeValueAsString(body), "UTF-8");
        Request request = Request.Post(url).body(entity).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        executor.execute(request);
    }

    @ResponseBody
    @RequestMapping(path = "/devices", method = RequestMethod.DELETE)
    public void unregisterDevice(@RequestParam(name = "registration_token") String registrationToken) throws IOException {
        final String url = config.getUrl() + "/api/v1/device/" + registrationToken;
        Request request = Request.Delete(url).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        executor.execute(request);
    }

    @ResponseBody
    @RequestMapping(path = "/teams", method = RequestMethod.POST)
    public void subscribeToTeam(@RequestBody TeamRegistrationBody body) throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/teams";
        HttpEntity entity = new StringEntity(mapper.writeValueAsString(body), "UTF-8");
        Request request = Request.Post(url).body(entity).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        executor.execute(request);
    }

    @ResponseBody
    @RequestMapping(path = "/teams", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getSubscribedTeams() throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/teams";
        Request request = Request.Get(url).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service"));
        Response r = executor.execute(request);
        return new ResponseEntity<>(mapper.readTree(r.returnContent().asString()), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(path = "/teams", method = RequestMethod.DELETE)
    public void unsubscribeTeam(@RequestParam(name = "team") String team) throws IOException, URISyntaxException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/teams";
        URI uri = new URIBuilder(url).addParameter("team", team).build();
        Request request = Request.Delete(uri).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        executor.execute(request);
    }

    @ResponseBody
    @RequestMapping(path = "/alerts", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getSubscribedAlerts() throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/alerts";
        Request request = Request.Get(url).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service"));
        Response r = executor.execute(request);
        return new ResponseEntity<>(mapper.readTree(r.returnContent().asString()), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(path = "/priority", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getPriority() throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/priority";
        Request request = Request.Get(url).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service"));
        Response r = executor.execute(request);
        return new ResponseEntity<>(mapper.readTree(r.returnContent().asString()), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(path = "/priority", method = RequestMethod.POST)
    public void setPriority(@RequestBody PriorityBody body) throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/priority";
        HttpEntity entity = new StringEntity(mapper.writeValueAsString(body), "UTF-8");
        Request request = Request.Post(url).body(entity).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        executor.execute(request);
    }

    @ResponseBody
    @RequestMapping(path = "/alerts", method = RequestMethod.POST)
    public void subscribeToAlert(@RequestBody AlertRegistrationBody body) throws IOException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/alerts";
        final String alertBody = mapper.writeValueAsString(body);
        HttpEntity entity = new StringEntity(alertBody, "UTF-8");
        Request request = Request.Post(url).body(entity).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        executor.execute(request);
    }

    @ResponseBody
    @RequestMapping(path = "/alerts", method = RequestMethod.DELETE)
    public void unsubscribeTeam(@RequestParam(name = "alert_id") int alertId) throws IOException, URISyntaxException {
        final String url = config.getUrl() + "/api/v1/users/" + authorityService.getUserName() + "/alerts";
        URI uri = new URIBuilder(url).addParameter("alertId", "" + alertId).build();
        Request request = Request.Delete(uri).addHeader("Authorization", "Bearer " + accessTokens.get("notification-service")).addHeader("Content-Type", "application/json");
        executor.execute(request);
    }
}
