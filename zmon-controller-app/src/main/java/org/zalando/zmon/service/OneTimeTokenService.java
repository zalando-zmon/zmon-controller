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
import java.util.List;

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

    public boolean storeNewToken(String userName, String fromIp, String token, int lifeTime) {
        List<Integer> ids = dbService.createOnetimeToken(userName, fromIp, token, lifeTime);
        return ids.size() > 0;
    }

    public boolean sendByEmail(String emailPrefix, String ip) {
        if (!controllerProperties.emailTokenEnabled) {
            return false;
        }

        final String emailAddress = emailPrefix + controllerProperties.emailTokenDomain;
        final String token = randomString(controllerProperties.getEmailTokenLength());
        LOG.info("Sending token: email={} token={}...", emailAddress, token.substring(3));

        if (!storeNewToken("EMAIL_REQUEST", ip, token, 1)) {
            return false;
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

            return true;
        }
        catch(EmailException ex) {
            LOG.error("Sending email failed: host={} addr={} msg={}", controllerProperties.getEmailHost(), emailAddress, ex.getMessage());
        }

        return false;
    }
}
