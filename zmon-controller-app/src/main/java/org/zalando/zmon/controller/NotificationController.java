package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.NotificationService;

/**
 * Created by jmussler on 09.11.16.
 */

@Controller
@RequestMapping(value = "/rest/notifications")
public class NotificationController {

    @Autowired
    private DefaultZMonPermissionService authorityService;

    @Autowired
    NotificationService notificationService;

    public static class DeviceRequestBody {
        public String registrationToken;
    }

    public static class SubscriptionRequestBody {
        public int alertId;
    }

    @RequestMapping(path="/register", method= RequestMethod.POST)
    public void registerDevice(@RequestBody DeviceRequestBody body) {

    }

    @RequestMapping(path="/subscription", method=RequestMethod.POST)
    public void subscribe(@RequestBody SubscriptionRequestBody body) {
        notificationService.subscribe(authorityService.getUserName(), body.alertId);
    }

    @RequestMapping(value = "/subscription/{alert_id}", method = RequestMethod.DELETE)
    public void unsubscribe() {

    }

    public void getAlertSubscriptions() {

    }
}
