package org.zalando.zmon.service.impl;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.domain.DashboardRecord;
import org.zalando.zmon.domain.DashboardIsEqual;
import org.zalando.zmon.generator.DashboardGenerator;
import org.zalando.zmon.generator.DataGenerator;
import org.zalando.zmon.service.DashboardService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@DirtiesContext
public class DashboardServiceImplIT {


    @Autowired
    private DashboardService service;

    private final DataGenerator<DashboardRecord> dashboardGenerator = new DashboardGenerator();

    @Test
    public void testCreateDashboard() throws Exception {
        final DashboardRecord dashboard = service.createOrUpdateDashboard(dashboardGenerator.generate());
        final List<DashboardRecord> allDashboards = service.getAllDashboards();

        MatcherAssert.assertThat(allDashboards, Matchers.contains(DashboardIsEqual.equalTo(dashboard)));
    }

    @Test
    public void testUpdateDashboard() throws Exception {
        final DashboardRecord genDashboard = dashboardGenerator.generate();

        // create first dashboard
        final DashboardRecord dashboard0 = service.createOrUpdateDashboard(genDashboard);

        // change the name of the new dashboard
        genDashboard.setName(genDashboard.getName() + " NEW");

        // and create the second dashboard
        DashboardRecord dashboard1 = service.createOrUpdateDashboard(genDashboard);

        // update the name of the dasboard
        dashboard1.setName(genDashboard.getName() + " UPDATE");

        // and update the second dashboard
        dashboard1 = service.createOrUpdateDashboard(dashboard1);

        final List<DashboardRecord> allDashboards = service.getAllDashboards();

        // Assertions.assertThat(allDashboards).contains(dashboard0,
        // dashboard1);

        MatcherAssert.assertThat(allDashboards,
            Matchers.containsInAnyOrder(DashboardIsEqual.equalTo(dashboard0), DashboardIsEqual.equalTo(dashboard1)));
    }

    @Test
    public void testGetAllDashboards() throws Exception {
        final DashboardRecord genDashboard = dashboardGenerator.generate();

        // create first dashboard
        final DashboardRecord dashboard0 = service.createOrUpdateDashboard(genDashboard);

        // change the name of the new dashboard
        genDashboard.setName(genDashboard.getName() + " NEW");

        // and create the second dashboard
        final DashboardRecord dashboard1 = service.createOrUpdateDashboard(genDashboard);

        final List<DashboardRecord> allDashboards = service.getAllDashboards();

        MatcherAssert.assertThat(allDashboards,
            Matchers.containsInAnyOrder(DashboardIsEqual.equalTo(dashboard0), DashboardIsEqual.equalTo(dashboard1)));
    }

    @Test
    public void testDeleteDashboard() throws Exception {

        final DashboardRecord genDashboard = dashboardGenerator.generate();

        // create first dashboard
        final DashboardRecord dashboard0 = service.createOrUpdateDashboard(genDashboard);

        // change the name of the new dashboard
        genDashboard.setName(genDashboard.getName() + " NEW");

        // and create the second dashboard
        final DashboardRecord dashboard1 = service.createOrUpdateDashboard(genDashboard);

        service.deleteDashboard(dashboard1.getId());

        final List<DashboardRecord> allDashboards = service.getAllDashboards();

        Assertions.assertThat(allDashboards).doesNotContain(dashboard1);
        Assertions.assertThat(allDashboards).contains(dashboard0);
    }
}
