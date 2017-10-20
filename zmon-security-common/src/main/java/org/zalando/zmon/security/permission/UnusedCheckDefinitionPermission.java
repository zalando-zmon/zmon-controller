package org.zalando.zmon.security.permission;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.security.authority.ZMonAuthority;

/**
 * Base for {@link CheckDefinition}-functions.
 */
abstract class UnusedCheckDefinitionPermission implements Function<ZMonAuthority, Boolean> {

    protected final CheckDefinition checkDefinition;

    UnusedCheckDefinitionPermission(CheckDefinition checkDefinition) {
        Preconditions.checkNotNull(checkDefinition, "'checkDefinition' should never be null");
        this.checkDefinition = checkDefinition;
    }

}
