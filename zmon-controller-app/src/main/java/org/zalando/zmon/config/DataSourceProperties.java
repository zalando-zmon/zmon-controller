package org.zalando.zmon.config;

import com.jolbox.bonecp.BoneCPConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zmon.datasource")
public class DataSourceProperties extends BoneCPConfig {
}
