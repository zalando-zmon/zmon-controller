package org.zalando.zmon.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.persistence.OnetimeTokensSProcService;

import java.security.SecureRandom;

/**
 * Created by jmussler on 03.07.16.
 */
@Component
public class OneTimeTokenService {

    private final static Logger LOG = LoggerFactory.getLogger(OneTimeTokenService.class);

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    private static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    OnetimeTokensSProcService dbService;
    ControllerProperties controllerProperties;

    @Autowired
    public OneTimeTokenService(OnetimeTokensSProcService dbService, ControllerProperties controllerProperties) {
        this.dbService = dbService;
        this.controllerProperties = controllerProperties;
    }

    public int storeNewToken(String userName, String fromIp, String token, int lifeTime) {
        return dbService.createOnetimeToken(userName, fromIp, token, lifeTime);
    }

    public TokenRequestResult sendByEmail(String emailPrefix, String ip) {
        if (!controllerProperties.emailTokenEnabled) {
            return TokenRequestResult.FAILED;
        }

        final String emailAddress = emailPrefix + controllerProperties.emailTokenDomain;
        final String token = randomString(controllerProperties.getEmailTokenLength());

        final int result = storeNewToken("EMAIL_REQUEST", ip, token, 1);

        if (result == -1) {
            return TokenRequestResult.RATE_LIMIT_HIT;
        }

        if (result <= 0) {
            LOG.error("Writing token to database failed");
            return TokenRequestResult.FAILED;
        }

        try {
            Email email = new SimpleEmail();
            email.setHostName(controllerProperties.getEmailHost());
            email.setSmtpPort(controllerProperties.getEmailPort());
            email.setAuthenticator(new DefaultAuthenticator(controllerProperties.getEmailUserName(), controllerProperties.getEmailPassword()));
            email.setSSLOnConnect(true);
            email.setFrom(controllerProperties.getEmailTokenFrom());
            email.setSubject("ZMON LOGIN TOKEN");
            email.setMsg("Login: " + controllerProperties.getEmailLoginLink() + "/" + token);
            email.addTo(emailAddress);
            email.send();

            return TokenRequestResult.OK;
        }
        catch(EmailException ex) {
            LOG.error("Sending email failed: host={} addr={} msg={}", controllerProperties.getEmailHost(), emailAddress, ex.getMessage());
        }

        return TokenRequestResult.FAILED;
    }
}
