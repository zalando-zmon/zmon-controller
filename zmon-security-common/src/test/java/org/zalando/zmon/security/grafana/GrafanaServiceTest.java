package org.zalando.zmon.security.grafana;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.zalando.zmon.security.grafanatoken.GrafanaTokenService;
import org.zalando.zmon.security.jwt.JWTServiceProperties;

public class GrafanaServiceTest {

    private Connection<?> oauthConnection;

    private GrafanaTokenService service;

    @Before
    public void setUp() {
        oauthConnection = Mockito.mock(Connection.class);
        ConnectionData data = Mockito.mock(ConnectionData.class);
        Mockito.when(data.getProviderId()).thenReturn("zauth");
        Mockito.when(oauthConnection.createData()).thenReturn(data);

        // default service for tests
        JWTServiceProperties props = new JWTServiceProperties();
        props.setKairosdbSignKey("28PI9q068f2qCbT38hnGX279Wei5YU5n");
        service = new GrafanaTokenService(props);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGrafanaTokenServicePropertiesSignKeyTooShort() {
        JWTServiceProperties props = new JWTServiceProperties();
        props.setKairosdbSignKey("012345678901234");
        // normally invoked by spring
        props.postConstruct();
    }

    @Test
    public void testGrafanaTokenServicePropertiesSignKey() {
        JWTServiceProperties props = new JWTServiceProperties();
        props.setKairosdbSignKey("28PI9q068f2qCbT38hnGX279Wei5YU5n");
        // normally invoked by spring
        props.postConstruct();
    }
}
