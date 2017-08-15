package org.zalando.zauth.zmon.config;

import org.springframework.social.oauth2.ClientCredentialsSupplier;

/**
 * ChainCredentialsSupplier provides client IDs and secrets trying to load them from chain of underlying
 * credential suppliers.
 * Problem here: reading client ID and secret IS NOT TRANSACTIONAL!!!! Meaning that client ID can come from JSON file
 * while client secret comes from statically defined secret value.
 * This can be fixed if ClientCredentialsSupplier interface changes to this:
 * <p>
 * public interface ClientCredentialsSupplier {
 * ClientCredentials getClientCredentials();
 * }
 * <p>
 * This change ensures that client ID and secret always come as a tuple. However, it requires refactoring of
 * spring-social-zauth library and affects too many clients of that library.
 */
public class ChainCredentialsSupplier implements ClientCredentialsSupplier {
    private final ClientCredentialsSupplier[] suppliers;

    public ChainCredentialsSupplier(final ClientCredentialsSupplier... suppliers) {
        this.suppliers = suppliers;
    }


    @Override
    public String getClientId() {
        Exception latest = null;
        for (ClientCredentialsSupplier supplier : suppliers) {
            try {
                return supplier.getClientId();
            } catch (Exception e) {
                latest = e;
            }
        }

        throw new RuntimeException(String.format("could not read client ID using %d credential suppliers", suppliers.length), latest);
    }

    @Override
    public String getClientSecret() {
        Exception latest = null;
        for (ClientCredentialsSupplier supplier : suppliers) {
            try {
                return supplier.getClientSecret();
            } catch (Exception e) {
                latest = e;
            }
        }

        throw new RuntimeException(String.format("could not read client ID using %d credential suppliers", suppliers.length), latest);

    }
}
