package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zalando.zmon.config.*;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import com.google.common.base.Joiner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jbellmann
 */
@Controller
public class IndexController {

    private static final Joiner COMMA_JOINER = Joiner.on(',');

    // parameters
    public static final String STATIC_URL = "staticUrl";
    public static final String LOGOUT_URL = "logoutUrl";
    public static final String KAIROSDB_SERVICES = "KairosDBServices";
    public static final String APPDYNAMICS_ENABLED = "appdynamicsEnabled";
    public static final String APPDYNAMICS_CONFIG = "appdynamicsConfig";
    public static final String GOOGLEANALYTICS_ENABLED = "googleanalyticsEnabled";
    public static final String GOOGLEANALYTICS_CONFIG = "googleanalyticsConfig";
    public static final String CONSENT_ENABLED = "consentEnabled";
    public static final String CONSENT_CONFIG = "consentConfig";
    public static final String EUM_TRACING_ENABLED = "eumTracingEnabled";
    public static final String EUM_ZMON_TRACING_CONFIG = "eumZmonTracingConfig";
    public static final String EUM_GRAFANA_TRACING_CONFIG = "eumGrafanaTracingConfig";
    private static final String HAS_SCHEDULE_DOWNTIME_PERMISSION = "hasScheduleDowntimePermission";
    private static final String HAS_DELETE_DOWNTIME_PERMISSION = "hasDeleteDowntimePermission";
    private static final String HAS_TRIAL_RUN_PERMISSION = "hasTrialRunPermission";
    private static final String HAS_ADD_COMMENT_PERMISSION = "hasAddCommentPermission";
    private static final String HAS_ADD_ALERT_DEFINITION_PERMISSION = "hasAddAlertDefinitionPermission";
    private static final String HAS_ADD_DASHBOARD_PERMISSION = "hasAddDashboardPermission";
    private static final String HAS_INSTANTANEOUS_ALERT_EVALUATION_PERMISSION =
            "hasInstantaneousAlertEvaluationPermission";
    private static final String SENTRY_ENABLED="sentryEnabled";
    private static final String SENTRY_CONFIG="sentryConfig";

    private static final String USER_NAME = "userName";
    private static final String TEAMS = "teams";

    private static final String FEEDBACK_REDIRECT_URL = "feedbackRedirectUrl";
    private static final String FEEDBACK_ENABLED = "feedbackEnabled";

    @Autowired
    private DefaultZMonPermissionService authorityService;

    @Autowired
    private ControllerProperties controllerProperties;

    @Autowired
    private FirebaseProperties firebaseProperties;

    @Autowired
    private AppdynamicsProperties appdynamicsProperties;

    @Autowired
    private GoogleanalyticsProperties googleanalyticsProperties;

    @Autowired
    private ConsentProperties consentProperties;

    @Autowired
    private EumTracingProperties eumTracingProperties;

    @Autowired
    private SentryProperties sentryProperties;

    @Autowired
    private FeedbackMetadataProperties feedbackMetadataProperties;

    @Value("${zmon.cloud.checkid}")
    private int cloudCheckId;

    @RequestMapping(value = {"/"})
    public String index(Model model) {

        model.addAttribute(STATIC_URL, controllerProperties.getStaticUrl());

        model.addAttribute(LOGOUT_URL, controllerProperties.getLogoutUrl());
        // TODO load all permissions in a single shot
        model.addAttribute(USER_NAME, authorityService.getUserName())
                .addAttribute(TEAMS, COMMA_JOINER.join(authorityService.getTeams()))
                .addAttribute(HAS_SCHEDULE_DOWNTIME_PERMISSION,
                        authorityService.hasScheduleDowntimePermission())
                .addAttribute(HAS_DELETE_DOWNTIME_PERMISSION,
                        authorityService.hasDeleteDowntimePermission())
                .addAttribute(HAS_TRIAL_RUN_PERMISSION, authorityService.hasTrialRunPermission())
                .addAttribute(HAS_ADD_COMMENT_PERMISSION, authorityService.hasAddCommentPermission())
                .addAttribute(HAS_ADD_ALERT_DEFINITION_PERMISSION,
                        authorityService.hasAddAlertDefinitionPermission())
                .addAttribute(HAS_ADD_DASHBOARD_PERMISSION,
                        authorityService.hasAddDashboardPermission())
                .addAttribute(HAS_INSTANTANEOUS_ALERT_EVALUATION_PERMISSION,
                        authorityService.hasInstantaneousAlertEvaluationPermission());

        model.addAttribute("cloudCheckId", cloudCheckId);
        model.addAttribute("firebaseConfig", firebaseProperties);
        model.addAttribute("firebaseEnabled", controllerProperties.enableFirebase);
        model.addAttribute(EUM_TRACING_ENABLED, controllerProperties.enableEumTracing);
        model.addAttribute(EUM_ZMON_TRACING_CONFIG, eumTracingProperties.zmonConfig);
        model.addAttribute(APPDYNAMICS_CONFIG, appdynamicsProperties);
        model.addAttribute(APPDYNAMICS_ENABLED, controllerProperties.enableAppdynamics);
        model.addAttribute(GOOGLEANALYTICS_CONFIG, googleanalyticsProperties);
        model.addAttribute(GOOGLEANALYTICS_ENABLED, controllerProperties.enableGoogleanalytics);
        model.addAttribute(CONSENT_CONFIG, consentProperties);
        model.addAttribute(CONSENT_ENABLED, controllerProperties.enableConsent);
        model.addAttribute(SENTRY_CONFIG, sentryProperties);
        model.addAttribute(SENTRY_ENABLED, controllerProperties.enableSentry);
        model.addAttribute(FEEDBACK_ENABLED, feedbackMetadataProperties.getEnabled());
        model.addAttribute(FEEDBACK_REDIRECT_URL, feedbackMetadataProperties.getUrl());

        return "index";
    }

    private static final ManifestJsonConfig manifest = new ManifestJsonConfig();

    @RequestMapping(value = {"/manifest.json"})
    @ResponseBody
    public ManifestJsonConfig manifestJson() {
        return manifest;
    }

    @RequestMapping(value = {"/firebase-messaging-sw.js"}, produces = "application/javascript")
    @ResponseBody
    public String firebaseMessagingWorker() throws IOException {
        InputStream inputStream = IndexController.class.getResourceAsStream("/templates/firebase-messaging-sw.js");

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String s =  result.toString("UTF-8");
        return s.replace("[[${firebaseSenderId}]]", firebaseProperties.getMessagingSenderId());
    }
}
