package org.zalando.zmon.domain;

public class RestoreCheckDefinitionRequest {
    private int checkDefinitionHistoryId;

    public int getCheckDefinitionHistoryId() {
        return checkDefinitionHistoryId;
    }

    public void setCheckDefinitionHistoryId(int checkDefinitionHistoryId) {
        this.checkDefinitionHistoryId = checkDefinitionHistoryId;
    }
}
