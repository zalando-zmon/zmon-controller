package org.zalando.zmon.security;

import org.springframework.social.connect.Connection;

/**
 * 
 * @author jbellmann
 *
 */
public abstract class AbstractSignupCondition<T> implements SignupCondition<T> {

	private Class<T> clazz;

	public AbstractSignupCondition(Class<T> clazz) {
		this.clazz = clazz;
	}

	public boolean matches(Connection<?> connection) {
		return matches(getApi(connection));
	}

	public abstract boolean matches(T api);

	@Override
	public boolean supportsConnection(Connection<?> connection) {
		return clazz.isAssignableFrom(connection.getApi().getClass());
	}

	protected T getApi(Connection<?> connection) {
		return clazz.cast(connection.getApi());
	}

}
