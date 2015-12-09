package org.zalando.github.zmon.security;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.UserOperations;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

/**
 * 
 * @author jbellmann
 *
 */
public class SignupConditionTest {

	private List<String> allowedGroups = Lists.newArrayList();
	
	private GithubSignupConditionProperties signupProperties;
	
	@Before
	public void setUp(){
		signupProperties = new GithubSignupConditionProperties();
		signupProperties.setAllowedOrgas(Lists.newArrayList("zalando", "zalando-stups"));
		signupProperties.setAllowedUsers(Lists.newArrayList("kmeier", "rruessel"));
	}

	@Test
	public void allowedUsers() {

		Connection connection = Mockito.mock(Connection.class);
		GitHub github = Mockito.mock(GitHub.class);
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(connection.getApi()).thenReturn(github);
		Mockito.when(github.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getProfileId()).thenReturn("kmeier").thenReturn("rwrong");

		IsAllowedUserSignupCondition condition = new IsAllowedUserSignupCondition(signupProperties);
		Assertions.assertThat(condition.supportsConnection(connection));
		Assertions.assertThat(condition.matches(connection)).isTrue();
		Assertions.assertThat(condition.matches(connection)).isFalse();
	}

	@Test
	public void allowedGroups() {

		Connection connection = Mockito.mock(Connection.class);
		GitHub github = Mockito.mock(GitHub.class);
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(connection.getApi()).thenReturn(github);
		Mockito.when(github.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getProfileId()).thenReturn("kmeier");

		IsInGroupSignupCondition condition = new IsInGroupSignupCondition(allowedGroups);
		Assertions.assertThat(condition.supportsConnection(connection));
		Assertions.assertThat(condition.matches(connection)).isTrue();
	}

	@Test
	public void combineConditions() {

		Connection connection = Mockito.mock(Connection.class);
		GitHub github = Mockito.mock(GitHub.class);
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(connection.getApi()).thenReturn(github);
		Mockito.when(github.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getProfileId()).thenReturn("kmeier");

		IsAllowedUserSignupCondition allowedUserCondition = new IsAllowedUserSignupCondition(signupProperties);
		IsInGroupSignupCondition inGroupCondition = new IsInGroupSignupCondition(allowedGroups);
		Predicate<GitHub> predicate = Predicates.and(allowedUserCondition, inGroupCondition);
		Assertions.assertThat(predicate.apply(github)).isTrue();
		
		GithubSignupCondition condition = GithubSignupCondition.and(inGroupCondition, allowedUserCondition);
		Assertions.assertThat(condition.matches(github)).isTrue();
	}

}
