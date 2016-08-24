package org.zalando.zauth.zmon.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hjacobs on 2/4/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    private String dn;

    private String name;

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
