package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jmussler on 26.02.16.
 */
@ConfigurationProperties(prefix = "zmon")
public class ControllerProperties {

    public String staticUrl = "";
    public String logoutUrl = "";
    public int grafanaMinInterval;

    public boolean emailTokenEnabled = false;
    public String emailTokenDomain = "@example.com";
    public String emailTokenFrom = "zmon@example.com";
    public String emailUserName = "";
    public String emailPassword = "";
    public String emailHost = "";
    public String emailLoginLink = "https://demo.zmon.io/tv";
    public int emailTokenLength = 15;
    public int emailPort = 465;

    public boolean enableFirebase = false;

    public boolean enableAppdynamics = false;

    public boolean enableGoogleanalytics = false;

    public List<String> workerQueueKeys = Arrays.asList("zmon:queue:default");

    public List<String> getWorkerQueueKeys() {
        return workerQueueKeys;
    }

    public boolean isEnableFirebase() {
        return enableFirebase;
    }

    public void setEnableFirebase(boolean enableFirebase) {
        this.enableFirebase = enableFirebase;
    }

    public boolean isEnableAppdynamics() {
        return enableAppdynamics;
    }

    public void setEnableAppdynamics(boolean enableAppdynamics) {
        this.enableAppdynamics = enableAppdynamics;
    }

    public boolean isEnableGoogleanalytics() {
        return enableGoogleanalytics;
    }

    public void setEnableGoogleanalytics(boolean enableGoogleanalytics) {
        this.enableGoogleanalytics = enableGoogleanalytics;
    }

    public void setWorkerQueueKeys(List<String> workerQueueKeys) {
        this.workerQueueKeys = workerQueueKeys;
    }

    public String getStaticUrl() {
        return staticUrl;
    }

    public void setStaticUrl(String staticUrl) {
        this.staticUrl = staticUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }


    public int getGrafanaMinInterval() {
        return grafanaMinInterval;
    }

    public void setGrafanaMinInterval(int grafanaMinInterval) {
        this.grafanaMinInterval = grafanaMinInterval;
    }

    public boolean isEmailTokenEnabled() {
        return emailTokenEnabled;
    }

    public void setEmailTokenEnabled(boolean emailTokenEnabled) {
        this.emailTokenEnabled = emailTokenEnabled;
    }

    public String getEmailTokenDomain() {
        return emailTokenDomain;
    }

    public void setEmailTokenDomain(String emailTokenDomain) {
        this.emailTokenDomain = emailTokenDomain;
    }

    public String getEmailUserName() {
        return emailUserName;
    }

    public void setEmailUserName(String emailUserName) {
        this.emailUserName = emailUserName;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public int getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(int emailPort) {
        this.emailPort = emailPort;
    }

    public String getEmailTokenFrom() {
        return emailTokenFrom;
    }

    public void setEmailTokenFrom(String emailTokenFrom) {
        this.emailTokenFrom = emailTokenFrom;
    }

    public String getEmailLoginLink() {
        return emailLoginLink;
    }

    public void setEmailLoginLink(String emailLoginLink) {
        this.emailLoginLink = emailLoginLink;
    }

    public int getEmailTokenLength() {
        return emailTokenLength;
    }

    public void setEmailTokenLength(int emailTokenLength) {
        this.emailTokenLength = emailTokenLength;
    }
}
