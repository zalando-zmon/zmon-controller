package org.zalando.zmon.config;

import java.util.Optional;
import java.util.function.Function;

public class OAuth2TokenExtractor implements Function<Optional<String>, Optional<String>> {

    @Override
    public Optional<String> apply(Optional<String> t) {
        return t.flatMap(header -> extractTokenFromHeader(header));
    }

    protected Optional<String> extractTokenFromHeader(String header) {
        try {
            return Optional.ofNullable(header.substring(7)); // Bearer
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }
}
