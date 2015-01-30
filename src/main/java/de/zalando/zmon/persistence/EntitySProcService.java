package de.zalando.zmon.persistence;

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
    void createOrUpdateEntity(@SProcParam String data, @SProcParam String teamName, @SProcParam String userName);

    @SProcCall
    List<String> getEntityById(@SProcParam String id);

    @SProcCall
    List<String> getEntities(@SProcParam String filter);
}
