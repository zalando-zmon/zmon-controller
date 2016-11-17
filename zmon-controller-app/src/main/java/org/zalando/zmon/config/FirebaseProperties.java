package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by jmussler on 11.11.16.
 */
@ConfigurationProperties(prefix = "zmon.firebase")
public class FirebaseProperties {
    public String apiKey;
    public String authDomain;
    public String databaseURL;
    public String storageBucket;
    public String messagingSenderId;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAuthDomain() {
        return authDomain;
    }

    public void setAuthDomain(String authDomain) {
        this.authDomain = authDomain;
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public String getStorageBucket() {
        return storageBucket;
    }

    public void setStorageBucket(String storageBucket) {
        this.storageBucket = storageBucket;
    }

    public String getMessagingSenderId() {
        return messagingSenderId;
    }

    public void setMessagingSenderId(String messagingSenderId) {
        this.messagingSenderId = messagingSenderId;
    }
}
