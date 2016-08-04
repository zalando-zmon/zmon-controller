package org.zalando.github.zmon.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

public abstract class EnvironmentTestUtils {
    
    /**
     * Add additional (high priority) values to an {@link Environment} owned by an
     * {@link ApplicationContext}. Name-value pairs can be specified with colon (":") or
     * equals ("=") separators.
     * @param context the context with an environment to modify
     * @param pairs the name:value pairs
     */
    public static void addEnvironment(ConfigurableApplicationContext context,
            String... pairs) {
        addEnvironment(context.getEnvironment(), pairs);
    }

    /**
     * Add additional (high priority) values to an {@link Environment}. Name-value pairs
     * can be specified with colon (":") or equals ("=") separators.
     * @param environment the environment to modify
     * @param pairs the name:value pairs
     */
    public static void addEnvironment(ConfigurableEnvironment environment,
            String... pairs) {
        addEnvironment("test", environment, pairs);
    }

    /**
     * Add additional (high priority) values to an {@link Environment}. Name-value pairs
     * can be specified with colon (":") or equals ("=") separators.
     * @param environment the environment to modify
     * @param name the property source name
     * @param pairs the name:value pairs
     */
    public static void addEnvironment(String name, ConfigurableEnvironment environment,
            String... pairs) {
        MutablePropertySources sources = environment.getPropertySources();
        Map<String, Object> map = getOrAdd(sources, name);
        for (String pair : pairs) {
            int index = getSeparatorIndex(pair);
            String key = pair.substring(0, index > 0 ? index : pair.length());
            String value = index > 0 ? pair.substring(index + 1) : "";
            map.put(key.trim(), value.trim());
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getOrAdd(MutablePropertySources sources,
            String name) {
        if (sources.contains(name)) {
            return (Map<String, Object>) sources.get(name).getSource();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        sources.addFirst(new MapPropertySource(name, map));
        return map;
    }

    private static int getSeparatorIndex(String pair) {
        int colonIndex = pair.indexOf(":");
        int equalIndex = pair.indexOf("=");
        if (colonIndex == -1) {
            return equalIndex;
        }
        if (equalIndex == -1) {
            return colonIndex;
        }
        return Math.min(colonIndex, equalIndex);
    }


}
