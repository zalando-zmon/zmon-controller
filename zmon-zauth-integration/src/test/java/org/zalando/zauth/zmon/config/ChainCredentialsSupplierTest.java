package org.zalando.zauth.zmon.config;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.social.oauth2.JsonCredentialFileReader;
import org.springframework.social.oauth2.StaticClientCredentialsSupplier;

public class ChainCredentialsSupplierTest {

    @Test
    public void testCredentialsReadPrimaryProvider() {
        ChainCredentialsSupplier supplier = new ChainCredentialsSupplier(
                new StaticClientCredentialsSupplier("foo", "bar"),
                new JsonCredentialFileReader("./")
        );

        Assertions.assertThat(supplier.getClientId()).isEqualTo("foo");
        Assertions.assertThat(supplier.getClientSecret()).isEqualTo("bar");
    }

    @Test
    public void testCredentialsReadFromfallbackProvider() {
        ChainCredentialsSupplier supplier = new ChainCredentialsSupplier(
                new JsonCredentialFileReader("./"),
                new StaticClientCredentialsSupplier("foo", "bar")
        );

        Assertions.assertThat(supplier.getClientId()).isEqualTo("foo");
        Assertions.assertThat(supplier.getClientSecret()).isEqualTo("bar");

    }

    @Test(expected = RuntimeException.class)
    public void testCredentialsThrowsExceptionIfAllProvidersFail() {
        ChainCredentialsSupplier supplier = new ChainCredentialsSupplier(
                new JsonCredentialFileReader("./")
        );
        supplier.getClientId();
    }

}
