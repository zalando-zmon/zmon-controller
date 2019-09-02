package org.zalando.zmon.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
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

    public static Map<DefinitionRuntime, String> labeledValues() {
        return Arrays.stream(DefinitionRuntime.values()).collect(Collectors.toMap(
            Function.identity(),
            DefinitionRuntime::getLabel
        ));
    }
}
