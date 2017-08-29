package org.zalando.zmon.config;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ZmonOAuth2PropertiesTest {

    @Test
    public void testAdditionalParametersParsedFromAuthorizeUrl() {
        ZmonOAuth2Properties props = new ZmonOAuth2Properties();

        props.setAuthorizeUrl("https://foo.bar/baz?param1=bro&param2=pro");
        props.setAccessTokenUrl("https://baz.bar/foo?param1=pro1");

        props.init();

        assertThat(props.getAdditionalParams()).containsOnly(entry("param1", "bro"),
                entry("param2", "pro"));
    }


    @Test
    public void testAuthorizeUrlAndAccessTokenUrlAreStrippedOfQueryParams() {
        ZmonOAuth2Properties props = new ZmonOAuth2Properties();

        props.setAuthorizeUrl("https://foo.bar/baz?param1=bro&param2=pro");
        props.setAccessTokenUrl("https://baz.bar/foo?param1=pro1");

        props.init();

        assertThat(props.getAuthorizeUrl()).isEqualTo("https://foo.bar/baz");
        assertThat(props.getAccessTokenUrl()).isEqualTo("https://baz.bar/foo");
    }

}
