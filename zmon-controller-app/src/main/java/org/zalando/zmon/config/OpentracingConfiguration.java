package org.zalando.zmon.config;


import io.opentracing.util.GlobalTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class OpentracingConfiguration {

    @Bean
    public io.opentracing.Tracer jaegerTracer() {

        com.uber.jaeger.Configuration config = com.uber.jaeger.Configuration.fromEnv();

        io.opentracing.Tracer tracer = config.getTracer();

        GlobalTracer.register(tracer);

        return tracer;
    }
}
