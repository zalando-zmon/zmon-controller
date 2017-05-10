package org.zalando.zmon.event;

import java.util.List;

import com.google.common.collect.Lists;

public enum ZMonEventType /*implements EventType*/ {

    // worker events
    ALERT_STARTED(0x34001, "checkId", "alertId", "value"),
    ALERT_ENDED(0x34002, "checkId", "alertId", "value"),
    ALERT_ENTITY_STARTED(0x34003, "checkId", "alertId", "value", "entity"),
    ALERT_ENTITY_ENDED(0x34004, "checkId", "alertId", "value", "entity"),
    DOWNTIME_STARTED(0x34005, "alertId", "entity", "startTime", "endTime", "userName", "comment"),
    DOWNTIME_ENDED(0x34006, "alertId", "entity", "startTime", "endTime", "userName", "comment"),
    ALERT_ACKNOWLEDGED(0x34007, "alertId", "entity", "startTime", "endTime", "userName", "comment"),

    // controller events
    DOWNTIME_SCHEDULED(0x34101, "alertId", "entity", "startTime", "endTime", "userName", "comment"),
    DOWNTIME_REMOVED(0x34102, "alertId", "entity", "startTime", "endTime", "userName", "comment"),
    TRIAL_RUN_SCHEDULED(0x34103, "checkCommand", "alertCondition", "entities", "period", "userName"),
    ALERT_COMMENT_CREATED(0x34104, "commentId", "comment", "alertId", "entity", "userName"),
    ALERT_COMMENT_REMOVED(0x34105, "commentId", "comment", "alertId", "entity", "userName"),
    CHECK_DEFINITION_CREATED(0x34106, "checkId", "entities", "checkCommand", "userName"),
    CHECK_DEFINITION_UPDATED(0x34107, "checkId", "entities", "checkCommand", "userName"),
    ALERT_DEFINITION_CREATED(0x34108, "alertId", "entities", "alertCondition", "userName"),
    ALERT_DEFINITION_UPDATED(0x34109, "alertId", "entities", "alertCondition", "userName"),
    CHECK_DEFINITION_DELETED(0x3410a, "checkId", "entities", "checkCommand", "userName"),
    ALERT_DEFINITION_DELETED(0x3410b, "alertId", "entities", "alertCondition", "userName"),
    DASHBOARD_CREATED(0x3410c, "dashboardId", "name", "widgetConfiguration", "alertTeams", "viewMode", "editOption",
        "sharedTeams", "userName"),
    DASHBOARD_UPDATED(0x3410d, "dashboardId", "name", "widgetConfiguration", "alertTeams", "viewMode", "editOption",
        "sharedTeams", "userName"),
    INSTANTANEOUS_ALERT_EVALUATION_SCHEDULED(0x3410e, "alertId", "userName"),
    GROUP_MODIFIED(0x3410f, "userName", "action","group","member","phone"),

    // Twilio Call Events
    CALL_TRIGGERED(0x34200, "alertId", "entityId", "phone", "level"),
    CALL_AGGR_TRIGGERED(0x34201, "alertId", "entityIds", "phone", "level"),
    CALL_ANSWERED(0x34202, "alertId", "entityIds", "phone"),
    CALL_ALERT_ACK_RECEIVED(0x3420A, "alertId", "phone", "incidentId"),
    CALL_ENTITY_ACK_RECEIVED(0x3420B, "alertId", "phone", "entityIds", "incidentId"),
    CALL_ALERT_RESOLVED(0x34210, "alertId", "incidentId");


    private final int id;
    private final List<String> fieldNames;

    ZMonEventType(final int id, final String... fieldNames) {
        this.id = id;
        this.fieldNames = Lists.newArrayList(fieldNames);
    }

//    @Override
    public String getName() {
        return name();
    }

//    @Override
    public int getId() {
        return id;
    }

//    @Override
    public List<String> getFieldNames() {
        return fieldNames;
    }
}
