package org.zalando.zmon;

//import com.instana.opentracing.InstanaTracer;
//import com.instana.opentracing.InstanaTracerFactory;
import io.opentracing.NoopTracer;
import io.opentracing.util.GlobalTracer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Bean;
import com.uber.jaeger.Configuration;
import com.uber.jaeger.samplers.ProbabilisticSampler;

/**
 * Entry for Zmon.
 *
 * @author jbellmann
 */
@SpringBootApplication
@ComponentScan(basePackages = {"org.zalando.zmon"})
public class ZmonApplication {
	@Bean
	public io.opentracing.Tracer jaegerTracer() {

		io.opentracing.Tracer tracer = new Configuration("zmon-controller", new Configuration.SamplerConfiguration(ProbabilisticSampler.TYPE, 1),
				new Configuration.ReporterConfiguration(true, "compose_localhost_1", 5775, 100, 100))
				.getTracer();

    	/*return new Configuration("zmon-controller", new Configuration.SamplerConfiguration(ProbabilisticSampler.TYPE, 1),
        	new Configuration.ReporterConfiguration(true, "compose_localhost_1", 5775, 100, 100))
        	.getTracer();*/
    	GlobalTracer.register(tracer);

    	return tracer;
	}

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ZmonApplication.class, args);
    }
}
