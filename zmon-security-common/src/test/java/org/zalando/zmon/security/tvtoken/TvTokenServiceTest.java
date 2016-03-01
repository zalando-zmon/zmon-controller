package org.zalando.zmon.security.tvtoken;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.zalando.zmon.persistence.OnetimeTokensSProcService;
import de.zalando.zmon.security.tvtoken.TvTokenService;

public class TvTokenServiceTest {

    private OnetimeTokensSProcService oneTimeTokensSProcService;

    @Before
    public void setUp() {
        oneTimeTokensSProcService = Mockito.mock(OnetimeTokensSProcService.class);
    }

    @Test
    public void roundTrip() {

        when(oneTimeTokensSProcService.bindOnetimeToken(Mockito.eq("1234567"), Mockito.eq("192.168.23.12"),
                Mockito.eq("987654321"))).thenReturn(singletonList(1));

        TvTokenService service = new TvTokenService(oneTimeTokensSProcService);
        String cookieValue = service.createCookieValue("1234567", "192.168.23.12", "987654321");
        Assertions.assertThat(cookieValue).isNotNull();
        Assertions.assertThat(cookieValue).isNotEmpty();

        Iterable<String> decodedAsParts = service.decodeCookieValue(cookieValue);
        Assertions.assertThat(decodedAsParts).isNotNull();
        Assertions.assertThat(decodedAsParts).isNotEmpty();
    }

}
