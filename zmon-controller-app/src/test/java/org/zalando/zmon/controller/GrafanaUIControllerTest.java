package org.zalando.zmon.controller;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.zalando.zmon.config.AppdynamicsProperties;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.config.EumTracingProperties;
import org.zalando.zmon.config.KairosDBProperties;
import org.zalando.zmon.persistence.GrafanaDashboardSprocService;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class GrafanaUIControllerTest {

    @Test
    public void TestControllerInjectsAppDynamicsConfiguration() {
        AppdynamicsProperties appdynamicsProperties = new AppdynamicsProperties();
        ControllerProperties controllerProperties = new ControllerProperties();
        EumTracingProperties eumTracingProperties = new EumTracingProperties();
        controllerProperties.setEnableAppdynamics(true);

        GrafanaUIController controller = new GrafanaUIController(
                mock(KairosDBProperties.class),
                controllerProperties,
                appdynamicsProperties,
                eumTracingProperties,
                mock(GrafanaDashboardSprocService.class)
        );

        ExtendedModelMap model = new ExtendedModelMap();
        controller.grafana(model);

        Assertions.assertThat(model.get(IndexController.APPDYNAMICS_ENABLED)).isEqualTo(true);
        Assertions.assertThat(model.get(IndexController.APPDYNAMICS_CONFIG)).isEqualTo(appdynamicsProperties);
    }

    @Test
    public void TestControllerInjectsAppDynamicsConfigurationInDeepLinks() {
        AppdynamicsProperties appdynamicsProperties = new AppdynamicsProperties();
        ControllerProperties controllerProperties = new ControllerProperties();
        EumTracingProperties eumTracingProperties = new EumTracingProperties();
        controllerProperties.setEnableAppdynamics(true);

        GrafanaUIController controller = new GrafanaUIController(
                mock(KairosDBProperties.class),
                controllerProperties,
                appdynamicsProperties,
                eumTracingProperties,
                mock(GrafanaDashboardSprocService.class)
        );

        ExtendedModelMap model = new ExtendedModelMap();
        controller.grafanaDeepLinks(model);

        Assertions.assertThat(model.get(IndexController.APPDYNAMICS_ENABLED)).isEqualTo(true);
        Assertions.assertThat(model.get(IndexController.APPDYNAMICS_CONFIG)).isEqualTo(appdynamicsProperties);
    }

    @Test
    public void TestControllerDisablesAppDynamicsByDefault() {
        GrafanaUIController controller = new GrafanaUIController(
                mock(KairosDBProperties.class),
                mock(ControllerProperties.class),
                mock(AppdynamicsProperties.class),
                mock(EumTracingProperties.class),
                mock(GrafanaDashboardSprocService.class)
        );

        ExtendedModelMap model = new ExtendedModelMap();
        controller.grafana(model);

        Assertions.assertThat(model.get(IndexController.APPDYNAMICS_ENABLED)).isEqualTo(false);
    }

    @Test
    public void TestControllerDisablesAppDynamicsInDeepLinksByDefault() {
        GrafanaUIController controller = new GrafanaUIController(
                mock(KairosDBProperties.class),
                mock(ControllerProperties.class),
                mock(AppdynamicsProperties.class),
                mock(EumTracingProperties.class),
                mock(GrafanaDashboardSprocService.class)
        );

        ExtendedModelMap model = new ExtendedModelMap();
        controller.grafanaDeepLinks(model);

        Assertions.assertThat(model.get(IndexController.APPDYNAMICS_ENABLED)).isEqualTo(false);
    }

    @Test
    public void TestGrafana6Redirect() throws Exception {
        GrafanaDashboardSprocService grafanaService = mock(GrafanaDashboardSprocService.class);
        GrafanaUIController controller = new GrafanaUIController(
                mock(KairosDBProperties.class),
                mock(ControllerProperties.class),
                mock(AppdynamicsProperties.class),
                mock(EumTracingProperties.class),
                grafanaService
        );

        when(grafanaService.getGrafanaMapping(anyString())).thenReturn("someuid");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysDo(print()).build();

        MvcResult result = mockMvc.perform(get("/grafana6/dashboard/db/testing?some-param=some-value")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        Assertions.assertThat(result.getResponse().getHeader("Location")).contains("/d/someuid?some-param=some-value");
    }

    @Test
    public void TestGrafana6RedirectUnknownUid() throws Exception {
        GrafanaUIController controller = new GrafanaUIController(
                mock(KairosDBProperties.class),
                mock(ControllerProperties.class),
                mock(AppdynamicsProperties.class),
                mock(EumTracingProperties.class),
                mock(GrafanaDashboardSprocService.class)
        );

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysDo(print()).build();

        mockMvc.perform(get("/grafana6/dashboard/db/testing?some-param=some-value")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
