package de.zalando.zmon.security.tvtoken;

import org.springframework.util.Base64Utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import de.zalando.zmon.persistence.OnetimeTokensSProcService;

public class TvTokenService {

    private final OnetimeTokensSProcService oneTimeTokenSProcService;

    private final Joiner cookieValueJoiner = Joiner.on("|").useForNull("UNKNOWN");
    private final Splitter cookieValueSplitter = Splitter.on("|").omitEmptyStrings();

    public TvTokenService(OnetimeTokensSProcService onetimeTokensSProcService) {
        this.oneTimeTokenSProcService = onetimeTokensSProcService;
    }

    public String createCookieValue(String token, String bindIp, String sessionId) {
        String cookieValue = cookieValueJoiner.join(token, bindIp, sessionId);
        String encodedValue = Base64Utils.encodeToString(cookieValue.getBytes());
        return encodedValue;
    }

    public Iterable<String> decodeCookieValue(String encodedCookieValue) {
        String decodedValue = new String(Base64Utils.decodeFromString(encodedCookieValue));
        return cookieValueSplitter.split(decodedValue);
    }

    // TODO use sproc
    public boolean isValidToken(String token, String bindIp, String sessionId) {
        return true;
        // List<Integer> result =
        // oneTimeTokenSProcService.bindOnetimeToken(token, bindIp, sessionId);
        // return result.size() > 0 ? true : false;
    }


}
