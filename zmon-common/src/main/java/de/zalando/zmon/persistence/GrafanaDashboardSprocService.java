package de.zalando.zmon.persistence;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

import java.util.List;

/**
 * Created by jmussler on 3/25/15.
 */
@SProcService
public interface GrafanaDashboardSprocService {

    @DatabaseType
    public static class GrafanaDashboard {
        @DatabaseField
        public String id;

        @DatabaseField
        public String title;

        @DatabaseField
        public String dashboard;

        @DatabaseField
        public String user;
    }

    @SProcCall
    void createOrUpdateGrafanaDashboard(@SProcParam String id, @SProcParam String title, @SProcParam String dashboard, @SProcParam String userName);

    @SProcCall
    List<GrafanaDashboard> getGrafanaDashboards();

    @SProcCall
    List<GrafanaDashboard> getGrafanaDashboard(@SProcParam String id);
}
