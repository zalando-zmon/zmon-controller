package org.zalando.zmon.api.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.zmon.domain.DashboardImport;
import org.zalando.zmon.domain.EditOption;
import org.zalando.zmon.domain.ViewMode;

import java.io.IOException;
import java.util.*;

/**
 * Created by jmussler on 3/11/15.
 */
public class DashboardRest {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardRest.class);

    public Integer id;
    public String name;
    public String createdBy;
    public Date lastModified;
    public String lastModifiedBy;
    public ViewMode viewMode;
    public EditOption editOption;
    public List<String> alertTeams;
    public List<String> tags;
    public List<String> sharedTeams;
    public List<JsonNode> widgetConfiguration;

    public static DashboardRest from(DashboardImport d, ObjectMapper mapper) {
        DashboardRest a = new DashboardRest();

        a.id = d.getId();
        a.alertTeams = d.getAlertTeams();
        a.createdBy = d.getCreatedBy();
        a.editOption = d.getEditOption();
        a.viewMode = d.getViewMode();
        a.lastModified = d.getLastModified();
        a.lastModifiedBy = d.getLastModifiedBy();
        a.name = d.getName();
        a.tags = d.getTags();
        a.sharedTeams = d.getSharedTeams();

        try {
            a.widgetConfiguration = mapper.readValue(d.getWidgetConfiguration(), new TypeReference<List<JsonNode>>() {});
        }
        catch(IOException ex) {
            LOG.warn("Widget configuration invalid", ex);
            a.widgetConfiguration = new ArrayList<>();
        }

        return a;
    }

    public DashboardImport toDashboard(ObjectMapper mapper) {
        DashboardImport d = new DashboardImport();

        d.setId(id);
        d.setName(name);
        d.setAlertTeams(alertTeams);
        d.setTags(tags);
        d.setEditOption(editOption);
        d.setViewMode(viewMode);
        d.setLastModified(lastModified);
        d.setLastModifiedBy(lastModifiedBy);
        d.setSharedTeams(sharedTeams);

        try {
            d.setWidgetConfiguration(mapper.writeValueAsString(widgetConfiguration));
        }
        catch(IOException ex) {
            LOG.warn("JSON error", ex);
        }

        return d;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Dashboard{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", createdBy='").append(createdBy).append('\'');
        sb.append(", lastModified=").append(lastModified);
        sb.append(", lastModifiedBy='").append(lastModifiedBy).append('\'');
        sb.append(", widgetConfiguration='").append(widgetConfiguration).append('\'');
        sb.append(", alertTeams=").append(alertTeams);
        sb.append(", viewMode=").append(viewMode);
        sb.append(", editOption=").append(editOption);
        sb.append(", sharedTeams=").append(sharedTeams);
        sb.append(", tags=").append(tags);
        sb.append('}');
        return sb.toString();
    }
}
