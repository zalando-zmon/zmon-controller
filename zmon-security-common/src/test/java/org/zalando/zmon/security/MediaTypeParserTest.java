package org.zalando.zmon.security;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.MediaType;

public class MediaTypeParserTest {

    @Test
    public void testParseMediaType() {
        MediaType mt = MediaType.parseMediaType("application/json");
        Assertions.assertThat(mt).isNotNull();
        Assertions.assertThat(mt).isEqualTo(MediaType.APPLICATION_JSON);
    }

}
