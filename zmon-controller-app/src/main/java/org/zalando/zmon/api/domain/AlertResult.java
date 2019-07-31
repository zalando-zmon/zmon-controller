package org.zalando.zmon.api.domain;

public class AlertResult {
    private String alertDefinitionId;
    private String entityId;
    private String entityType;
    private String checkDefinitionId;
    private String title;
    private boolean triggered;
    private String priority;

    public AlertResult(String alertDefinitionId, String entityId, String entityType, String checkDefinitionId, String title, boolean triggered, String priority) {
        this.alertDefinitionId = alertDefinitionId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.checkDefinitionId = checkDefinitionId;
        this.title = title;
        this.triggered = triggered;
        this.priority = priority;
    }

    public String getAlertDefinitionId() {
        return alertDefinitionId;
    }

    public void setAlertDefinitionId(String alertDefinitionId) {
        this.alertDefinitionId = alertDefinitionId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getCheckDefinitionId() {
        return checkDefinitionId;
    }

    public void setCheckDefinitionId(String checkDefinitionId) {
        this.checkDefinitionId = checkDefinitionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}

