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
    class GrafanaDashboard {
        @DatabaseField
        public String id;

        @DatabaseField
        public String title;

        @DatabaseField
        public String dashboard;

        @DatabaseField
        public String user;

        @DatabaseField
        public String tags;

        @DatabaseField
        public boolean starred;
    }

    @DatabaseType
    class GrafanaTag {
        @DatabaseField
        public String tag;
        @DatabaseField
        public int count;
    }


    @SProcCall
    void createOrUpdateGrafanaDashboard(@SProcParam String id, @SProcParam String title, @SProcParam String dashboard, @SProcParam String userName, @SProcParam String version);

    @SProcCall
    List<GrafanaDashboard> getGrafanaDashboards(@SProcParam String title, @SProcParam String tags, @SProcParam String starredBy, @SProcParam String user);

    @SProcCall
    List<GrafanaDashboard> getGrafanaDashboard(@SProcParam String id, @SProcParam String user);

    @SProcCall
    List<String> deleteGrafanaDashboard(@SProcParam String id, @SProcParam String user);

    @SProcCall
    List<GrafanaTag> getTagsWithCount();

    @SProcCall
    List<String> starGrafanaDashboard(@SProcParam String id, @SProcParam String user);

    @SProcCall
    List<String> unstarGrafanaDashboard(@SProcParam String id, @SProcParam String user);

}
