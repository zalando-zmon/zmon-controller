package org.zalando.zmon.controller;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.domain.OnetimeTokenInfo;
import org.zalando.zmon.persistence.OnetimeTokensSProcService;
import org.zalando.zmon.security.jwt.JWTService;
import org.zalando.zmon.security.jwt.JWTServiceProperties;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.security.tvtoken.TvTokenService;
import org.zalando.zmon.service.OneTimeTokenService;
import org.zalando.zmon.service.TokenRequestResult;

import javax.servlet.http.Cookie;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.zalando.zauth.zmon.config.ZauthSecurityConfig.LOGIN_COOKIE_NAME;

public class TvTokenControllerTest {

    private OnetimeTokensSProcService onetimeTokensSProcService;
    private OneTimeTokenService oneTimeTokenService;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        onetimeTokensSProcService = mock(OnetimeTokensSProcService.class);
        oneTimeTokenService = mock(OneTimeTokenService.class);
        when(oneTimeTokenService.sendByEmail(eq("user.name"), eq("192.168.23.12"))).thenReturn(TokenRequestResult.OK);
        when(oneTimeTokenService.sendByEmail(eq("user.name.more"), eq("192.168.23.12"))).thenReturn(TokenRequestResult.FAILED);

        final JWTServiceProperties jwtServiceProperties = mock(JWTServiceProperties.class);
        when(jwtServiceProperties.getSecret()).thenReturn("28PI9q068f2qCbT38hnGX279Wei5YU5n");
        mockMvc = MockMvcBuilders.standaloneSetup(new TvTokenController(new TvTokenService(onetimeTokensSProcService),
                oneTimeTokenService, new ControllerProperties(),
                mock(DefaultZMonPermissionService.class), new JWTService(jwtServiceProperties)))
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
        mockMvc.perform(post("/tv/by-email")
                .header("X-FORWARDED-FOR", "192.168.23.12")
                .cookie(new Cookie("JSESSIONID", "987654321"))
                .param("mail", "test@example.com"))
                .andExpect(status().is(400));

        mockMvc.perform(post("/tv/by-email")
                .header("X-FORWARDED-FOR", "192.168.23.12")
                .cookie(new Cookie("JSESSIONID", "987654321"))
                .param("mail", "test.summary%40example.com"))
                .andExpect(status().is(400));
    }

    @Test
    public void validEmail() throws Exception {
        mockMvc.perform(post("/tv/by-email")
                .header("X-FORWARDED-FOR", "192.168.23.12")
                .cookie(new Cookie("JSESSIONID", "987654321"))
                .param("mail", "user.name"))
                .andExpect(status().is(200));

        // verify rate limit hit
        mockMvc.perform(post("/tv/by-email")
                .header("X-FORWARDED-FOR", "192.168.23.12")
                .cookie(new Cookie("JSESSIONID", "987654321"))
                .param("mail", "user.name"))
                .andExpect(status().is(429));
    }

    @Test
    public void testConvertToTvMode() throws Exception {
        when(oneTimeTokenService.storeToken(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(TokenRequestResult.OK);
        when(onetimeTokensSProcService.bindOnetimeToken(anyString(), anyString(), anyString()))
                .thenReturn(ImmutableList.of(mock(OnetimeTokenInfo.class)));
        mockMvc.perform(get("/tv/switch")
                .header("X-Forwarded-For", "127.0.0.1")
                .cookie(new Cookie(LOGIN_COOKIE_NAME, "987654321"))
                .cookie(new Cookie(JWTService.COOKIE_NAME, "foo")))
                .andExpect(cookie().maxAge(LOGIN_COOKIE_NAME, 0)) // deleted
                .andExpect(cookie().value(LOGIN_COOKIE_NAME, ""))
                .andExpect(cookie().maxAge(JWTService.COOKIE_NAME, 0)) // deleted
                .andExpect(cookie().value(JWTService.COOKIE_NAME, ""))
                .andExpect(cookie().exists(TvTokenService.ZMON_TV))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testConvertFailureToStoreToken() throws Exception {
        when(oneTimeTokenService.storeToken(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(TokenRequestResult.FAILED);
        mockMvc.perform(get("/tv/switch")
                .header("X-Forwarded-For", "127.0.0.1"))
                .andExpect(status().is5xxServerError());
    }
}
