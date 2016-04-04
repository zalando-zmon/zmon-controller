package org.zalando.zauth.zmon.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hjacobs on 2/4/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {
    private String id;

    @JsonProperty("id_name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
