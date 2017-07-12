package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by elauria on 17.05.17.
 */
@ConfigurationProperties(prefix = "zmon.appdynamics")
public class AppdynamicsProperties {
    public String apiKey;
    public String adrumExtUrlHttp;
    public String adrumExtUrlHttps;
    public String beaconUrlHttp;
    public String beaconUrlHttps;
    public boolean xd;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAdrumExtUrlHttp() {
        return adrumExtUrlHttp;
    }

    public void setAdrumExtUrlHttp(String adrumExtUrlHttp) {
        this.adrumExtUrlHttp = adrumExtUrlHttp;
    }

    public String getAdrumExtUrlHttps() {
        return adrumExtUrlHttps;
    }

    public void setAdrumExtUrlHttps(String adrumExtUrlHttps) {
        this.adrumExtUrlHttps = adrumExtUrlHttps;
    }

    public String getBeaconUrlHttp() {
        return beaconUrlHttp;
    }

    public void setBeaconUrlHttp(String beaconUrlHttp) {
        this.beaconUrlHttp = beaconUrlHttp;
    }

    public String getBeaconUrlHttps() {
        return beaconUrlHttps;
    }

    public void setBeaconUrlHttps(String beaconUrlHttps) {
        this.beaconUrlHttps = beaconUrlHttps;
    }

    public boolean getXd() {
        return xd;
    }

    public void setXd(boolean xd) {
        this.xd = xd;
    }
}
