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
 * @author  jbellmann
 */
public abstract class GithubSignupCondition extends AbstractSignupCondition<GitHub> implements Predicate<GitHub> {

    public GithubSignupCondition() {
        super(GitHub.class);
    }

    public static final String ALL_AUTHORIZED = "*";

    /**
     * Makes a {@link SignupCondition} behaves like an {@link Predicate}.
     */
    @Override
    public boolean apply(final GitHub input) {
        return matches(input);
    }

    public static GithubSignupCondition and(final GithubSignupCondition... conditions) {
        return and(Arrays.asList(conditions));
    }

    public static GithubSignupCondition and(final List<GithubSignupCondition> conditions) {
        if (conditions.isEmpty()) {
            return new PredicateSignupConditionAdapter(Predicates.alwaysTrue());
        }

        return new PredicateSignupConditionAdapter(Predicates.and(filter(conditions, notNull())));
    }

    /**
     * Internal {@link Predicates} adapter.
     */
    private static class PredicateSignupConditionAdapter extends GithubSignupCondition {

        private final Predicate<GitHub> predicates;

        public PredicateSignupConditionAdapter(final Predicate<GitHub> predicates) {
            this.predicates = predicates;
        }

        @Override
        public boolean matches(final GitHub api) {
            return predicates.apply(api);
        }

    }
}
