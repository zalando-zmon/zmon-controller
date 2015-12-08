package de.zalando.zmon.security.permission;

import javax.annotation.Nonnull;

import com.google.common.base.Function;

import de.zalando.zmon.security.authority.ZMonAuthority;

/**
 * Extracted to increase readability.
 * 
 * @author jbellmann
 *
 */
class AuthorityFunctions {

	 static final Function<ZMonAuthority, Boolean> TRIAL_RUN_PERMISSION_FUNCTION =

	new Function<ZMonAuthority, Boolean>() {
		@Override
		public Boolean apply(@Nonnull final ZMonAuthority input) {
			return input.hasTrialRunPermission();
		}
	};

	 static final Function<ZMonAuthority, Boolean> ADD_COMMENT_PERMISSION_FUNCTION =

	new Function<ZMonAuthority, Boolean>() {
		@Override
		public Boolean apply(@Nonnull final ZMonAuthority input) {
			return input.hasAddCommentPermission();
		}
	};

	 static final Function<ZMonAuthority, Boolean> ADD_ALERT_DEFINITION_PERMISSION_FUNCTION =

	new Function<ZMonAuthority, Boolean>() {
		@Override
		public Boolean apply(@Nonnull final ZMonAuthority input) {
			return input.hasAddAlertDefinitionPermission();
		}
	};

	 static final Function<ZMonAuthority, Boolean> SCHEDULE_DOWNTIME_PERMISSION_FUNCTION =

	new Function<ZMonAuthority, Boolean>() {
		@Override
		public Boolean apply(@Nonnull final ZMonAuthority input) {
			return input.hasScheduleDowntimePermission();
		}
	};

	 static final Function<ZMonAuthority, Boolean> DELETE_DOWNTIME_PERMISSION_FUNCTION =

	new Function<ZMonAuthority, Boolean>() {
		@Override
		public Boolean apply(@Nonnull final ZMonAuthority input) {
			return input.hasDeleteDowntimePermission();
		}
	};

	 static final Function<ZMonAuthority, Boolean> ADD_DASHBOARD_PERMISSION_FUNCTION =

	new Function<ZMonAuthority, Boolean>() {
		@Override
		public Boolean apply(@Nonnull final ZMonAuthority input) {
			return input.hasAddDashboardPermission();
		}
	};

	 static final Function<ZMonAuthority, Boolean> HISTORY_REPORT_ACCESS_FUNCTION =

	new Function<ZMonAuthority, Boolean>() {
		@Override
		public Boolean apply(@Nonnull final ZMonAuthority input) {
			return input.hasHistoryReportAccess();
		}
	};

	 static final Function<ZMonAuthority, Boolean> INSTANTANEOUS_ALERT_EVALUATION_FUNCTION =

	new Function<ZMonAuthority, Boolean>() {
		@Override
		public Boolean apply(@Nonnull final ZMonAuthority input) {
			return input.hasInstantaneousAlertEvaluationPermission();
		}
	};

}
