package org.zalando.zmon.event;

/**
 * Created by jmussler on 1/26/15.
 */
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class EventlogEvent {
    private Date time;
    private int typeId;
    private String typeName;
    private Map<String, JsonNode> attributes = new LinkedHashMap<>();
    private String flowId;

    public Map<String, JsonNode> getAttributes() {
        return attributes;
    }

    public void setFlowId(final String f) {
        flowId = f;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setAttributes(final Map<String, JsonNode> attributes) {
        this.attributes = attributes;
    }

    public EventlogEvent setAttribute(final String key, final JsonNode value) {
        attributes.put(key, value);
        return this;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(final Date time) {
        this.time = time;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(final int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "Event{" +
                "time=" + time +
                ", typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", attributes=" + attributes +
                ", flowId='" + flowId + '\'' +
                '}';
    }
}
