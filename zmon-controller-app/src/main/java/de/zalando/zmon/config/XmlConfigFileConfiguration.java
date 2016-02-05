package de.zalando.zmon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Move step-by-step to replace the xml.
 * 
 * @author jbellmann
 *
 */
@Configuration
@PropertySource("classpath:/db_api_version.properties")
@PropertySource("classpath:/zmon.properties")

// @ImportResource({"classpath:/backendContext.xml"})
public class XmlConfigFileConfiguration {
}
