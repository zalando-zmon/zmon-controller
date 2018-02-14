package org.zalando.zmon.config;


import com.lightstep.tracer.shared.Options;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.MalformedURLException;

@Configuration
@AutoConfigureBefore(OpentracingConfiguration.class)
@EnableConfigurationProperties(LightstepProperties.class)
public class OpentracingConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OpentracingConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(Tracer.class)
    @ConditionalOnProperty(prefix = "tracer.lightstep", name = "enabled", havingValue = "true")
    public io.opentracing.Tracer lightStepTracer(LightstepProperties properties) throws MalformedURLException{


        log.info(properties.toString());

        Tracer tracer;

        com.lightstep.tracer.shared.Options.OptionsBuilder options = new com.lightstep.tracer.shared.Options.OptionsBuilder()
                .withAccessToken(properties.getAccessToken())
                .withCollectorHost(properties.getCollectorHost())
                .withCollectorPort(properties.getCollectorPort())
                //.withCollectorProtocol(properties.getCollectorProtocol())
                .withComponentName(properties.getComponentName())
                .withVerbosity(4);

        tracer = new com.lightstep.tracer.jre.JRETracer(options.build());

        GlobalTracer.register(tracer);

        return tracer;
    }
}