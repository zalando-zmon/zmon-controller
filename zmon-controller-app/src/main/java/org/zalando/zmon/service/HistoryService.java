package org.zalando.zmon.service;

import org.zalando.zmon.domain.Activity;
import org.zalando.zmon.domain.ActivityDiff;
import org.zalando.zmon.domain.HistoryAction;

import javax.annotation.Nullable;
import java.util.List;

public interface HistoryService {
    List<Activity> getHistory(int alertDefinitionId, @Nullable Integer limit, @Nullable Long from, @Nullable Long to);

    List<ActivityDiff> getCheckDefinitionHistory(int checkDefinitionId, @Nullable Integer limit, @Nullable Long from,
                                                 @Nullable Long to, @Nullable HistoryAction action);

    List<ActivityDiff> getCheckDefinitionHistory(int checkDefinitionId, @Nullable Integer limit, @Nullable Long from,
            @Nullable Long to);

    List<ActivityDiff> getAlertDefinitionHistory(int alertDefinitionId, @Nullable Integer limit, @Nullable Long from,
            @Nullable Long to);

    boolean restoreCheckDefinition(int checkDefinitionHistoryId, String userName, List<String> teams, boolean isAdmin);
}
