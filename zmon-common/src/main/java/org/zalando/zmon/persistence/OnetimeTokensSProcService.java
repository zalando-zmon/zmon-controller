package org.zalando.zmon.persistence;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

import java.util.List;

import org.zalando.zmon.domain.OnetimeTokenInfo;

/**
 * Created by jmussler on 26.02.16.
 */
@SProcService
public interface OnetimeTokensSProcService {
    @SProcCall
    Integer createOnetimeToken(@SProcParam String user, @SProcParam String ip, @SProcParam String token, @SProcParam int expiresInDays);

    @SProcCall
    List<OnetimeTokenInfo> bindOnetimeToken(@SProcParam String token, @SProcParam String bindIp, @SProcParam String sessionId);

    @SProcCall
    List<OnetimeTokenInfo> getOnetimeTokensByUser(@SProcParam String user);
}
