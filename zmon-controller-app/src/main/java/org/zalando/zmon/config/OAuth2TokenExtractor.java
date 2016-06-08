package org.zalando.zmon.config;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class OAuth2TokenExtractor implements Function<Optional<String>, Optional<String>> {

    private static final String SPACE = " ";
    private static final String BEARER = "Bearer ";
    private static final Predicate<String> startsWithBearer = new StartsWithBearer();

    @Override
    public Optional<String> apply(Optional<String> t) {
        return t.filter(startsWithBearer).flatMap(this::extractTokenFromHeader);
    }

    protected Optional<String> extractTokenFromHeader(String header) {
        try {
            return ofNullable(header.split(SPACE)[1]);
        } catch (IndexOutOfBoundsException e) {
            return empty();
        }
    }

    private static class StartsWithBearer implements Predicate<String> {
        @Override
        public boolean test(String t) {
            return t.startsWith(BEARER);
        }
    }
}
