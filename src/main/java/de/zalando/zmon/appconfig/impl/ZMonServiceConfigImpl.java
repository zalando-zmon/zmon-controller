package de.zalando.zmon.appconfig.impl;

import de.zalando.zmon.appconfig.ZMonServiceConfig;
import org.springframework.stereotype.Service;

/**
 *
 * @author hjacobs
 */
@Service
public class ZMonServiceConfigImpl implements ZMonServiceConfig {

    @Override
    public boolean writeToKairosDB() {
        return false;
    }

    @Override
    public int getEventLogPort() {
        return 8081;
    }

    @Override
    public String getEventLogHost() {
        return "localhost";
    }
}
