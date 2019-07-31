package org.zalando.zmon.domain;

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
}