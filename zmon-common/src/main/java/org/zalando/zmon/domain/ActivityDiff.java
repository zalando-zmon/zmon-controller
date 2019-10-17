package org.zalando.zmon.domain;

import java.util.Map;

public class ActivityDiff extends Activity {
    private long historyId;

    private Integer recordId;

    private HistoryAction action;

    private Map<String, String> changedAttributes;

    private String lastModifiedBy;

    private String unifiedDiff;

    public long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(final Integer recordId) {
        this.recordId = recordId;
    }

    public HistoryAction getAction() {
        return action;
    }

    public void setAction(final HistoryAction action) {
        this.action = action;
    }

    public Map<String, String> getChangedAttributes() {
        return changedAttributes;
    }

    public void setChangedAttributes(final Map<String, String> changedAttributes) {
        this.changedAttributes = changedAttributes;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getUnifiedDiff() {
        return unifiedDiff;
    }

    public void setUnifiedDiff(String unifiedDiff) {
        this.unifiedDiff = unifiedDiff;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ActivityDiff{");
        sb.append("historyId=").append(historyId);
        sb.append(", recordId=").append(recordId);
        sb.append(", action=").append(action);
        sb.append(", changedAttributes=").append(changedAttributes);
        sb.append(", lastModifiedBy='").append(lastModifiedBy).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
