package de.zalando.zauth.zmon.service;

import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.connect.Connection;
import org.springframework.social.zauth.api.ZAuth;
import org.zalando.zmon.security.service.AccountConnectionSignupService;

/**
 * 
 * @author jbellmann
 *
 */
public class ZauthAccountConnectionSignupService extends AccountConnectionSignupService {

	public ZauthAccountConnectionSignupService(UserDetailsManager userDetailsManager) {
		super(userDetailsManager);
	}

	@Override
	protected String getLoginFromConnection(Connection<?> connection) {
		Object api = connection.getApi();

		// use the api if you can
		if (api instanceof ZAuth) {
			ZAuth zAuth = (ZAuth) api;
			return zAuth.getCurrentLogin();
		}

		return super.getLoginFromConnection(connection);
	}

}
