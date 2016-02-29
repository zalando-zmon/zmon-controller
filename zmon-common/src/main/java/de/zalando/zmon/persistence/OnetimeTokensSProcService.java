package de.zalando.zmon.persistence;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

import java.util.List;

/**
 * Created by jmussler on 26.02.16.
 */
@SProcService
public interface OnetimeTokensSProcService {
    @SProcCall
    List<Integer> createOnetimeToken(@SProcParam String user, @SProcParam String ip, @SProcParam String token);

    @SProcCall
    List<Integer> bindOnetimeToken(@SProcParam String token, @SProcParam String bindIp, @SProcParam String sessionId);
}
