package org.zalando.zmon.api.domain;

import java.util.List;
import java.util.Map;

/**
 * Created by jmussler on 10.01.17.
 */
public class EntityFilterRequest {
    public List<List<Map<String, String>>> includeFilters;
    public List<List<Map<String, String>>> excludeFilters;
    public boolean local = false;
}
