package de.zalando.zmon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"backendContext.xml"})
public class XmlConfigFileConfiguration {

}
