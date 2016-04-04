package org.zalando.zmon.domain;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

import java.util.Date;

/**
 * Created by jmussler on 10.03.16.
 */
@DatabaseType
public class OnetimeTokenInfo {

    @DatabaseField
    private String token;

    @DatabaseField
    private Date created;

    @DatabaseField
    private String boundIp;

    @DatabaseField
    private Date boundAt;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getBoundIp() {
        return boundIp;
    }

    public void setBoundIp(String boundIp) {
        this.boundIp = boundIp;
    }

    public Date getBoundAt() {
        return boundAt;
    }

    public void setBoundAt(Date boundAt) {
        this.boundAt = boundAt;
    }
}
