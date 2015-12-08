package org.zalando.github.zmon.security;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;

import java.util.Arrays;
import java.util.List;

import org.springframework.social.github.api.GitHub;
import org.zalando.zmon.security.AbstractSignupCondition;
import org.zalando.zmon.security.SignupCondition;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * 
 * @author jbellmann
 *
 */
public abstract class GithubSignupCondition extends AbstractSignupCondition<GitHub> implements Predicate<GitHub> {

	public GithubSignupCondition() {
		super(GitHub.class);
	}

	/**
	 * Makes a {@link SignupCondition} behaves like an {@link Predicate}.
	 */
	@Override
	public boolean apply(GitHub input) {
		return matches(input);
	}
	
	public static GithubSignupCondition and(GithubSignupCondition... conditions){
		return and(Arrays.asList(conditions));
	}

	public static GithubSignupCondition and(List<GithubSignupCondition> conditions) {
		return new PredicateSignupConditionAdapter(Predicates.and(filter(conditions, notNull())));
	}

	/**
	 * Internal {@link Predicates} adapter.
	 *
	 */
	private static class PredicateSignupConditionAdapter extends GithubSignupCondition {

		private final Predicate<GitHub> predicates;

		public PredicateSignupConditionAdapter(Predicate<GitHub> predicates) {
			this.predicates = predicates;
		}

		@Override
		public boolean matches(GitHub api) {
			return predicates.apply(api);
		}

	}
}
