package de.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;

/**
 * FIXME: hardcoded config for Vagrant box
 *
 * @author hjacobs
 */
@ConfigurationProperties(prefix = "zmon.kairosdb")
public class KairosDBProperties {

    private URL url;

    private boolean enabled;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
