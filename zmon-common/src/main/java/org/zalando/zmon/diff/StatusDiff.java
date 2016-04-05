package org.zalando.zmon.diff;

import org.zalando.zmon.domain.DefinitionStatus;

public interface StatusDiff {

    Integer getId();

    DefinitionStatus getStatus();

}
