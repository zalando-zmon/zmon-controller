package org.zalando.zmon.domain;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;

public class CheckResults {

    private String entity;
    private List<JsonNode> results;
    private Set<Integer> activeAlertIds;
    private Map<Integer, Long> entitiesCount;

    public CheckResults() {

    }

    public CheckResults(String entity) {
        this.entity = entity;
        this.activeAlertIds = new HashSet<>();
        this.results = new ArrayList<>();
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(final String entity) {
        this.entity = entity;
    }

    public void setEntitiesCount(final Map<Integer, Long> entitiesCount) {
        this.entitiesCount = entitiesCount;
    }

    public Map<Integer, Long> getEntitiesCount() {
        return this.entitiesCount;
    }

    public List<JsonNode> getResults() {
        return results;
    }

    public void setResults(final List<JsonNode> results) {
        this.results = results;
    }

    public Set<Integer> getActiveAlertIds() {
        return activeAlertIds;
    }

    public void setActiveAlertIds(final Set<Integer> activeAlertIds) {
        this.activeAlertIds = activeAlertIds;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CheckResults [entity=");
        builder.append(entity);
        builder.append(", results=");
        builder.append(results);
        builder.append(", activeAlertIds=");
        builder.append(activeAlertIds);
        builder.append("]");
        return builder.toString();
    }
}
