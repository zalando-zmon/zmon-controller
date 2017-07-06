package org.zalando.zmon.service;

import org.zalando.zmon.event.Event;

import javax.annotation.Nullable;
import java.util.List;

public interface EventLogService {
    List<Event> getAlertEvents(int alertDefinitionId,
                               @Nullable Integer limit, @Nullable Long fromMillis, @Nullable Long toMillis);

    List<Event> getCheckEvents(int checkDefinitionId,
                               @Nullable Integer limit, @Nullable Long fromMillis, @Nullable Long toMillis);
}
