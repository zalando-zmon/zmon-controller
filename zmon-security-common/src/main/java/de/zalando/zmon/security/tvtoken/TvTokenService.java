package de.zalando.zmon.security.tvtoken;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import de.zalando.zmon.persistence.OnetimeTokensSProcService;

public class TvTokenService {

    public static final String ZMON_TV_ID = "ZMON_TV_ID";

    public static final String ZMON_TV = "ZMON_TV";

    public static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

    private final OnetimeTokensSProcService oneTimeTokenSProcService;

    private final Joiner cookieValueJoiner = Joiner.on("|").useForNull("UNKNOWN");
    private final Splitter cookieValueSplitter = Splitter.on("|").omitEmptyStrings();

    public TvTokenService(OnetimeTokensSProcService onetimeTokensSProcService) {
        this.oneTimeTokenSProcService = onetimeTokensSProcService;
    }

    public static String remoteIp(HttpServletRequest request) {
        if (request != null) {
            return request.getRemoteAddr();
        }
        return "UNKNOWN";
    }

    // TODO use sproc
    public boolean isValidToken(String token, String bindIp, String sessionId) {
        return true;
        // List<Integer> result =
        // oneTimeTokenSProcService.bindOnetimeToken(token, bindIp, sessionId);
        // return result.size() > 0 ? true : false;
    }


}
