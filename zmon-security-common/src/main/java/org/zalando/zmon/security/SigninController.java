package org.zalando.zmon.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author  jbellmann
 */
@Controller
public class SigninController {

    @Autowired
    Environment env;

    @RequestMapping(value = "/signin")
    public String signin() {
        final String activeProfile = env.getActiveProfiles()[0];
        return "signin_" + activeProfile;
    }
}
