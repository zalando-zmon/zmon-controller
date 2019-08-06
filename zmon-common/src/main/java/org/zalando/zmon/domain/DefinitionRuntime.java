package org.zalando.zmon.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum DefinitionRuntime {
    PYTHON_2("Python 2"),
    PYTHON_3("Python 3");

    private final String label;

    DefinitionRuntime(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Map<String, String> asMap(DefinitionRuntime runtime) {
        return new HashMap<String, String>() {{
            put("name", runtime.name());
            put("label", runtime.getLabel());
        }};
    }

    public static List<Map<String, String>> asListOfMaps(List<DefinitionRuntime> runtimes) {
        return runtimes.stream().map(DefinitionRuntime::asMap).collect(Collectors.toList());
    }
}