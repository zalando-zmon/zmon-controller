package org.zalando.zmon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.domain.OnetimeTokenInfo;
import org.zalando.zmon.persistence.OnetimeTokensSProcService;

import java.security.SecureRandom;
import java.util.List;

/**
 * Created by jmussler on 03.07.16.
 */
@Component
public class OneTimeTokenService {

    private final static Logger LOG = LoggerFactory.getLogger(OneTimeTokenService.class);

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final SecureRandom rnd = new SecureRandom();

    public static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    OnetimeTokensSProcService dbService;
    ControllerProperties config;
    MailService mail;

    @Autowired
    public OneTimeTokenService(OnetimeTokensSProcService dbService, ControllerProperties config, MailService mail) {
        this.dbService = dbService;
        this.mail = mail;
        this.config = config;
    }

    public TokenRequestResult storeToken(String userName, String fromIp, String token, int lifeTime) {
        int result = dbService.createOnetimeToken(userName, fromIp, token, lifeTime);
        if (result == -1) {
            return TokenRequestResult.RATE_LIMIT_HIT;
        }

        if (result <= 0) {
            LOG.error("Writing token to database failed");
            return TokenRequestResult.FAILED;
        }

        return TokenRequestResult.OK;
    }

    public TokenRequestResult sendByEmail(String emailPrefix, String ip) {
        if (!config.emailTokenEnabled) {
            return TokenRequestResult.FAILED;
        }

        final String emailAddress = emailPrefix + config.emailTokenDomain;
        final String token = randomString(config.getEmailTokenLength());

        final TokenRequestResult result = storeToken("EMAIL_REQUEST", ip, token, 1);

        switch(result) {
            case RATE_LIMIT_HIT: return result;
            case FAILED: return result;
            case OK: {
                boolean sendResult = mail.sendTokenEmail(token, emailAddress);
                if (sendResult) {
                    return result;
                }
            }
        }

        return TokenRequestResult.FAILED;
    }

    public List<OnetimeTokenInfo> getTokensByUser(String user) {
        return dbService.getOnetimeTokensByUser(user);
    }
}
