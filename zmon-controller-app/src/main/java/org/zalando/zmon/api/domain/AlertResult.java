package org.zalando.zmon.api.domain;

public class AlertResult {
    private final String alertDefinitionId;
    private final String entityId;
    private final String entityType;
    private final String checkDefinitionId;
    private final String title;
    private final String applicationId;
    private final boolean triggered;
    private final String priority;

    public AlertResult(String alertDefinitionId, String entityId, String entityType, String checkDefinitionId, String title, String applicationId, boolean triggered, String priority) {
        this.alertDefinitionId = alertDefinitionId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.checkDefinitionId = checkDefinitionId;
        this.title = title;
        this.applicationId = applicationId;
        this.triggered = triggered;
        this.priority = priority;
    }

    public String getAlertDefinitionId() {
        return alertDefinitionId;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getCheckDefinitionId() {
        return checkDefinitionId;
    }

    public String getTitle() {
        return title;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public String getPriority() {
        return priority;
    }
}

