package org.zalando.zmon.config;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class OAuth2TokenExtractorTest {

    private final OAuth2TokenExtractor extractor = new OAuth2TokenExtractor();

    @Test
    public void testExtractor() {
        Optional<String> result = extractor.apply(Optional.of("Bearer 123456789"));
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get()).isNotNull();
        Assertions.assertThat(result.get()).isEqualTo("123456789");
    }

    @Test
    public void testExtractorOnEmptyString() {
        Optional<String> result = extractor.apply(Optional.of(""));
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.isPresent()).isFalse();
        result.ifPresent(t -> Assertions.fail("Not expecting any value here"));
    }

}
