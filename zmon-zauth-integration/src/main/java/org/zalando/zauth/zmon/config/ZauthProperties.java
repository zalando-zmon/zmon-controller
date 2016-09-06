package org.zalando.zauth.zmon.config;

import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by hjacobs on 2/4/16.
 */
@ConfigurationProperties(prefix = "zmon.zauth")
public class ZauthProperties {

    private URL userServiceUrl;
    private URL teamServiceUrl;
    private URL oauth2AccessTokenUrl;
    private Map<String, List<String>> teamOverlay = Maps.newHashMap();
    private Map<String, List<String>> teamExtension = Maps.newHashMap();

    private String adminsGroup = "Apps/ZMON/Admins";
    private String usersGroup = "Apps/ZMON/Users";

    public Map<String, List<String>> getTeamOverlay() {
        return teamOverlay;
    }

    public void setTeamOverlay(Map<String, List<String>> teamOverlay) {
        this.teamOverlay = teamOverlay;
    }

    public URL getUserServiceUrl() {
        return userServiceUrl;
    }

    public void setUserServiceUrl(URL userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }

    public URL getTeamServiceUrl() {
        return teamServiceUrl;
    }

    public void setTeamServiceUrl(URL teamServiceUrl) {
        this.teamServiceUrl = teamServiceUrl;
    }

    public URL getOauth2AccessTokenUrl() {
        return oauth2AccessTokenUrl;
    }

    public void setOauth2AccessTokenUrl(URL oauth2AccessTokenUrl) {
        this.oauth2AccessTokenUrl = oauth2AccessTokenUrl;
    }

    public Map<String, List<String>> getTeamExtension() {
        return teamExtension;
    }

    public void setTeamExtension(Map<String, List<String>> teamExtension) {
        this.teamExtension = teamExtension;
    }

    public String getAdminsGroup() {
        return adminsGroup;
    }

    public void setAdminsGroup(String adminsGroup) {
        this.adminsGroup = adminsGroup;
    }

    public String getUsersGroup() {
        return usersGroup;
    }

    public void setUsersGroup(String usersGroup) {
        this.usersGroup = usersGroup;
    }
}
