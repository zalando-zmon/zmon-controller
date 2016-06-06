package org.zalando.zmon.service;

import java.util.List;
import java.util.Set;

import org.zalando.zmon.domain.DowntimeDetails;
import org.zalando.zmon.domain.DowntimeRequest;
import org.zalando.zmon.api.DowntimeGroup;

public interface DowntimeService {

    List<String> scheduleDowntime(DowntimeRequest request);

    DowntimeGroup scheduleDowntimeGroup(DowntimeGroup group);

    List<DowntimeDetails> getDowntimes(Set<Integer> alertDefinitionIds);

    DowntimeGroup deleteDowntimeGroup(String groupId);

    void deleteDowntimes(Set<String> downtimeId);
}
