package org.zalando.zmon.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmussler on 11.11.16.
 */
public class ManifestJsonConfig {
    private String shortName = "ZMON";
    private String name = "ZMON";
    private String startUrl = "/";
    private String gcmSenderId = "103953800507";

    public List<ManifestIcon> icons = new ArrayList<>();

    public static class ManifestIcon {
        public String src = "";
        public String type = "";
        public String sizes = "";

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSizes() {
            return sizes;
        }

        public void setSizes(String sizes) {
            this.sizes = sizes;
        }
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getGcmSenderId() {
        return gcmSenderId;
    }

    public void setGcmSenderId(String gcmSenderId) {
        this.gcmSenderId = gcmSenderId;
    }

    public List<ManifestIcon> getIcons() {
        return icons;
    }

    public void setIcons(List<ManifestIcon> icons) {
        this.icons = icons;
    }
}
