package org.zalando.zmon.persistence;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

import java.util.List;

/**
 * Created by jmussler on 1/28/15.
 */
@SProcService
public interface EntitySProcService {

    @SProcCall
    String createOrUpdateEntity(
        @SProcParam String data, @SProcParam List<String> teams, @SProcParam String userName, @SProcParam boolean user_is_admin);

    @SProcCall
    List<String> getEntityById(@SProcParam String id);

    @SProcCall
    List<String> getEntities(@SProcParam String filter);

    @SProcCall
    List<String> deleteEntity(
        @SProcParam String id, @SProcParam List<String> teams, @SProcParam String userName, @SProcParam boolean user_is_admin);
}
