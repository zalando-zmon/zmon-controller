package org.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.config.FirebaseProperties;
import org.zalando.zmon.config.ManifestJsonConfig;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import com.google.common.base.Joiner;

/**
 * @author jbellmann
 */
@Controller
public class IndexController {

    private static final Joiner COMMA_JOINER = Joiner.on(',');

    // parameters
    public static final String STATIC_URL = "staticUrl";
    public static final String KAIROSDB_SERVICES = "KairosDBServices";
    private static final String HAS_SCHEDULE_DOWNTIME_PERMISSION = "hasScheduleDowntimePermission";
    private static final String HAS_DELETE_DOWNTIME_PERMISSION = "hasDeleteDowntimePermission";
    private static final String HAS_TRIAL_RUN_PERMISSION = "hasTrialRunPermission";
    private static final String HAS_ADD_COMMENT_PERMISSION = "hasAddCommentPermission";
    private static final String HAS_ADD_ALERT_DEFINITION_PERMISSION = "hasAddAlertDefinitionPermission";
    private static final String HAS_ADD_DASHBOARD_PERMISSION = "hasAddDashboardPermission";
    private static final String HAS_INSTANTANEOUS_ALERT_EVALUATION_PERMISSION =
            "hasInstantaneousAlertEvaluationPermission";

    private static final String USER_NAME = "userName";
    private static final String TEAMS = "teams";

    @Autowired
    private DefaultZMonPermissionService authorityService;

    @Autowired
    private ControllerProperties controllerProperties;

    @Autowired
    private FirebaseProperties firebaseProperties;

    @Value("${zmon.cloud.checkid}")
    private int cloudCheckId;

    @RequestMapping(value = {"/"})
    public String index(Model model) {

        model.addAttribute(STATIC_URL, controllerProperties.getStaticUrl());

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

        return "index";
    }

    private static final ManifestJsonConfig manifest = new ManifestJsonConfig();

    @RequestMapping(value = {"/manifest.json"})
    @ResponseBody
    public ManifestJsonConfig manifestJson() {
        return manifest;
    }
}
