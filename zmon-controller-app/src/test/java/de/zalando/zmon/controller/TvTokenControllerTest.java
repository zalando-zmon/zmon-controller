package de.zalando.zmon.controller;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.zalando.zmon.persistence.OnetimeTokensSProcService;
import de.zalando.zmon.security.tvtoken.TvTokenService;

public class TvTokenControllerTest {

    private OnetimeTokensSProcService onetimeTokensSProcService;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        onetimeTokensSProcService = Mockito.mock(OnetimeTokensSProcService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new TvTokenController(new TvTokenService(onetimeTokensSProcService)))
                .alwaysDo(MockMvcResultHandlers.print()).build();
    }

    @Test
    public void happyCase() throws Exception {
        when(onetimeTokensSProcService.bindOnetimeToken(Mockito.eq("1234567"), Mockito.eq("192.168.23.12"),
                Mockito.eq("987654321"))).thenReturn(singletonList(1));

        // execute
        // @formatter:off
        mockMvc.perform(get("/tv/1234567")
                            .header("X-FORWARDED-FOR", "192.168.23.12")
                            .cookie(new Cookie("JSESSIONID", "987654321")))
                        .andExpect(redirectedUrl("/"));
        // @formatter:on

        // verify sproc was invoked with specific parameters
        verify(onetimeTokensSProcService, Mockito.times(0)).bindOnetimeToken(Mockito.eq("1234567"),
                Mockito.eq("192.168.23.12"), Mockito.eq("987654321"));
    }

}
