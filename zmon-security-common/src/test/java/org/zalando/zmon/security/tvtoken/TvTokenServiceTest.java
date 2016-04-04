package org.zalando.zmon.security.tvtoken;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.zalando.zmon.persistence.OnetimeTokensSProcService;

public class TvTokenServiceTest {

    private OnetimeTokensSProcService oneTimeTokensSProcService;

    @Before
    public void setUp() {
        oneTimeTokensSProcService = Mockito.mock(OnetimeTokensSProcService.class);
    }

    @Test
    public void roundTrip() {

        // invalid
    }

}
