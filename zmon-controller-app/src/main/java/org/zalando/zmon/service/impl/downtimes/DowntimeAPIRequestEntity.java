package org.zalando.zmon.service.impl.downtimes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jmussler on 27.06.16.
 */
public class DowntimeAPIRequestEntity {
    private Integer alertDefinitionId;
    private Map<String, String> entityIds = new HashMap<>();

    public DowntimeAPIRequestEntity(Integer alertDefinitionId) {
        this.alertDefinitionId = alertDefinitionId;
    }

    public Integer getAlertDefinitionId() {
        return alertDefinitionId;
    }

    public Map<String, String> getEntityIds() {
        return entityIds;
    }
}
