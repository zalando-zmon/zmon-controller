package org.zalando.zauth.zmon.config;

import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

@Configuration
@EnableConfigurationProperties({LightstepConfiguration.class})
public class LightstepConfiguration {

    @Autowired
    LightstepProperties lightstepProperties;

    @Bean
    public io.opentracing.Tracer lightStepTracer() throws MalformedURLException {

        com.lightstep.tracer.shared.Options.OptionsBuilder options = new com.lightstep.tracer.shared.Options.OptionsBuilder()
                .withAccessToken(lightstepProperties.getAccessToken())
                .withCollectorHost(lightstepProperties.getCollectorHost())
                .withCollectorPort(lightstepProperties.getCollectorPort())
                .withCollectorProtocol(lightstepProperties.getCollectorProtocol())
                .withComponentName(lightstepProperties.getComponentName());

        final Tracer tracer = new com.lightstep.tracer.jre.JRETracer(options.build());

        GlobalTracer.register(tracer);

        return tracer;
    }
}



