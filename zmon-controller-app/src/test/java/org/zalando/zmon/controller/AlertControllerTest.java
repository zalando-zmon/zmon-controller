package org.zalando.zmon.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.zmon.service.AlertService;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class AlertControllerTest {
    @Mock
    private AlertService alertService;
    @InjectMocks
    private AlertController alertController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(alertController).alwaysDo(print()).build();
    }

    @Test
    public void testAckAlert() throws Exception {
        mockMvc.perform(put("/rest/alertNotificationsAck")
                .param("alert_id", "42")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(alertService).acknowledgeAlert(eq(42));
    }
}