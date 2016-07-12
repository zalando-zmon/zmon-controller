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

/**
 * Created by jmussler on 08.07.16.
 */
@Component
public class MailService {

    private final ControllerProperties config;

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    public MailService(ControllerProperties config) {
        this.config = config;
    }

    public boolean sendTokenEmail(String token, String emailAdress) {
        try {
            Email email = new SimpleEmail();
            email.setHostName(config.getEmailHost());
            email.setSmtpPort(config.getEmailPort());
            email.setAuthenticator(new DefaultAuthenticator(config.getEmailUserName(), config.getEmailPassword()));
            email.setSSLOnConnect(true);
            email.setFrom(config.getEmailTokenFrom());
            email.setSubject("ZMON LOGIN TOKEN");
            email.setMsg("Login: " + config.getEmailLoginLink() + "/" + token);
            email.addTo(emailAdress);
            email.send();

            return true;
        }
        catch(EmailException ex) {
            log.error("Sending email failed: host={} addr={} msg={}", config.getEmailHost(), emailAdress, ex.getMessage());
        }
        return false;
    }
}
