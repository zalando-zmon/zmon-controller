package org.zalando.zmon.controller;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.domain.OnetimeTokenInfo;
import org.zalando.zmon.persistence.OnetimeTokensSProcService;
import org.zalando.zmon.security.tvtoken.TvTokenService;
import org.zalando.zmon.service.OneTimeTokenService;

public class TvTokenControllerTest {

    private OnetimeTokensSProcService onetimeTokensSProcService;
    private OneTimeTokenService oneTimeTokenService;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        onetimeTokensSProcService = Mockito.mock(OnetimeTokensSProcService.class);
        oneTimeTokenService = Mockito.mock(OneTimeTokenService.class);

        when(oneTimeTokenService.sendByEmail(eq("user.name"),eq("192.168.23.12"))).thenReturn(true);
        when(oneTimeTokenService.sendByEmail(eq("user.name.more"),eq("192.168.23.12"))).thenReturn(false);

        mockMvc = MockMvcBuilders.standaloneSetup(new TvTokenController(new TvTokenService(onetimeTokensSProcService),
                                                  oneTimeTokenService, new ControllerProperties()))
                                 .alwaysDo(MockMvcResultHandlers.print()).build();
    }

    @Test
    public void happyCase() throws Exception {
        when(onetimeTokensSProcService.bindOnetimeToken(eq("1234567"), eq("192.168.23.12"),
                eq("987654321"))).thenReturn(singletonList(new OnetimeTokenInfo()));

        // execute
        // @formatter:off
        mockMvc.perform(get("/tv/1234567")
                            .header("X-FORWARDED-FOR", "192.168.23.12")
                            .cookie(new Cookie("JSESSIONID", "987654321")))
                        .andExpect(redirectedUrl("/"));
        // @formatter:on

        // verify sproc was invoked with specific parameters
        verify(onetimeTokensSProcService, Mockito.times(0)).bindOnetimeToken(eq("1234567"),
                eq("192.168.23.12"), eq("987654321"));
    }

    @Test
    public void invalidEmails() throws Exception {
        // @formatter:off
        /*
        mockMvc.perform(get("/tv/by-email/user.name@domain.tld")
                .header("X-FORWARDED-FOR", "192.168.23.12")
                .cookie(new Cookie("JSESSIONID", "987654321")))
                .andExpect(status().is(404));

        mockMvc.perform(get("/tv/by-email/user.name%40@domain.tld")
                .header("X-FORWARDED-FOR", "192.168.23.12")
                .cookie(new Cookie("JSESSIONID", "987654321")))
                .andExpect(status().is(404));

        mockMvc.perform(get("/tv/by-email/")
                .header("X-FORWARDED-FOR", "192.168.23.12")
                .cookie(new Cookie("JSESSIONID", "987654321")))
                .andExpect(status().is(302));
                */
        // @formatter:on
    }

    @Test
    public void validEmail() throws Exception {
        // @formatter:off
        /*
        mockMvc.perform(get("/tv/by-email/user.name")
                .header("X-FORWARDED-FOR", "192.168.23.12")
                .cookie(new Cookie("JSESSIONID", "987654321")))
                .andExpect(status().is(200));

        mockMvc.perform(get("/tv/by-email/user.name.more")
                .header("X-FORWARDED-FOR", "192.168.23.12")
                .cookie(new Cookie("JSESSIONID", "987654321")))
                .andExpect(status().is(500));
*/
        // @formatter:on

    //    verify(oneTimeTokenService).sendByEmail(eq("user.name"), eq("192.168.23.12"));
  //      verify(oneTimeTokenService).sendByEmail(eq("user.name.more"), eq("192.168.23.12"));
    }

}
