package org.zalando.zmon.service.impl.downtimes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.zalando.zmon.domain.DowntimeEntities;
import org.zalando.zmon.domain.DowntimeRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by jmussler on 27.06.16.
 */
public class DowntimeAPIRequest {
    private String comment;
    private Long startTime;
    private Long endTime;
    private String createdBy;
    private String groupId;
    private List<DowntimeAPIRequestEntity> downtimeEntities = new ArrayList<>();

    private DowntimeAPIRequest(String groupId, String createdBy, Long startTime, Long endTime, String comment) {
        this.groupId = groupId;
        this.createdBy = createdBy;
        this.endTime = endTime;
        this.startTime = startTime;
        this.comment = comment;
    }

    public static DowntimeAPIRequest convert(String groupId, DowntimeRequest original) {
        DowntimeAPIRequest request = new DowntimeAPIRequest(groupId, original.getComment(), original.getStartTime(), original.getEndTime(), original.getCreatedBy());
        for (DowntimeEntities es : original.getDowntimeEntities()) {
            final DowntimeAPIRequestEntity entitiesForAlert = new DowntimeAPIRequestEntity(es.getAlertDefinitionId());
            for (String entityId : es.getEntityIds()) {
                entitiesForAlert.getEntityIds().put(entityId, UUID.randomUUID().toString());
            }
            request.getDowntimeEntities().add(entitiesForAlert);
        }
        return request;
    }

    public List<DowntimeAPIRequestEntity> getDowntimeEntities() {
        return downtimeEntities;
    }

    @JsonIgnore
    public List<String> getDowntimeIds() {
        return downtimeEntities.stream().map(x->x.getEntityIds().values()).flatMap(x->x.stream()).collect(Collectors.toList());
    }
}
