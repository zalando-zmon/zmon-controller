package de.zalando.zmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.base.Joiner;

import de.zalando.zmon.security.legacy.DefaultZMonAuthorityService;

/**
 * 
 * @author jbellmann
 *
 */
@Controller
public class IndexController {
	
	private static final Joiner COMMA_JOINER = Joiner.on(',');
	
    // parameters
    private static final String HAS_SCHEDULE_DOWNTIME_PERMISSION = "hasScheduleDowntimePermission";
    private static final String HAS_DELETE_DOWNTIME_PERMISSION = "hasDeleteDowntimePermission";
    private static final String HAS_TRIAL_RUN_PERMISSION = "hasTrialRunPermission";
    private static final String HAS_ADD_COMMENT_PERMISSION = "hasAddCommentPermission";
    private static final String HAS_ADD_ALERT_DEFINITION_PERMISSION = "hasAddAlertDefinitionPermission";
    private static final String HAS_ADD_DASHBOARD_PERMISSION = "hasAddDashboardPermission";
    private static final String HAS_HISTORY_REPORT_ACCESS = "hasHistoryReportAccess";
    private static final String HAS_INSTANTANEOUS_ALERT_EVALUATION_PERMISSION =
        "hasInstantaneousAlertEvaluationPermission";

    private static final String USER_NAME = "userName";
    private static final String TEAMS = "teams";
    
    @Autowired
    private DefaultZMonAuthorityService authorityService;

	@RequestMapping(value={"/index","/"})
	public String index(Model model){
		

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
                                      .addAttribute(HAS_HISTORY_REPORT_ACCESS, authorityService.hasHistoryReportAccess())
                                      .addAttribute(HAS_INSTANTANEOUS_ALERT_EVALUATION_PERMISSION,
                                          authorityService.hasInstantaneousAlertEvaluationPermission());
		
		
		
		
		return "index";
	}
}
