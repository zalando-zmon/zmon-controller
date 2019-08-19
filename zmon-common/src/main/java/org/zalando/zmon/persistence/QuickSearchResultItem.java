package org.zalando.zmon.persistence;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

/**
 * Created by jmussler on 08.09.16.
 */

@DatabaseType
public class QuickSearchResultItem {

    @DatabaseField
    public String id;

    @DatabaseField
    public String title;

    @DatabaseField
    public String team;

    public String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
