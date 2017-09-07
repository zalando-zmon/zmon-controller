package org.zalando.zmon.controller;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.zalando.zmon.config.AppdynamicsProperties;
import org.zalando.zmon.config.InstanaProperties;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.config.KairosDBProperties;

import static org.mockito.Mockito.mock;

public class GrafanaUIControllerTest {

    @Test
    public void TestControllerInjectsAppDynamicsConfiguration() {
        AppdynamicsProperties appdynamicsProperties = new AppdynamicsProperties();
        InstanaProperties instanaProperties = new InstanaProperties();
        ControllerProperties controllerProperties = new ControllerProperties();
        controllerProperties.setEnableAppdynamics(true);

        GrafanaUIController controller = new GrafanaUIController(
                mock(KairosDBProperties.class),
                controllerProperties,
                appdynamicsProperties,
                instanaProperties
        );

        ExtendedModelMap model = new ExtendedModelMap();
        controller.grafana(model);

        Assertions.assertThat(model.get(IndexController.APPDYNAMICS_ENABLED)).isEqualTo(true);
        Assertions.assertThat(model.get(IndexController.APPDYNAMICS_CONFIG)).isEqualTo(appdynamicsProperties);
    }

    @Test
    public void TestControllerInjectsAppDynamicsConfigurationInDeepLinks() {
        AppdynamicsProperties appdynamicsProperties = new AppdynamicsProperties();
        InstanaProperties instanaProperties = new InstanaProperties();
        ControllerProperties controllerProperties = new ControllerProperties();
        controllerProperties.setEnableAppdynamics(true);

        GrafanaUIController controller = new GrafanaUIController(
                mock(KairosDBProperties.class),
                controllerProperties,
                appdynamicsProperties,
                instanaProperties
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
                mock(InstanaProperties.class)
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
                mock(InstanaProperties.class)
        );

        ExtendedModelMap model = new ExtendedModelMap();
        controller.grafanaDeepLinks(model);

        Assertions.assertThat(model.get(IndexController.APPDYNAMICS_ENABLED)).isEqualTo(false);
    }
}
