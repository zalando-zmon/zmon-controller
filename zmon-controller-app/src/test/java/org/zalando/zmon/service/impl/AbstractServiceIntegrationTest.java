package org.zalando.zmon.service.impl;

import static org.zalando.stups.junit.postgres.MavenProjectLayout.projectBaseDir;

import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.zalando.stups.junit.postgres.PostgreSqlRule;

/**
 * Provide some {@link Rule}s for Tests.
 * 
 * @author jbellmann
 *
 */
public abstract class AbstractServiceIntegrationTest {

    // @formatter:off
    /*
    @ClassRule
    public static PostgreSqlRule postgresqlRule = new PostgreSqlRule.Builder()
            .withDbName("local_zmon_db")
            .addScriptLocation(projectBaseDir(DashboardServiceImplIT.class) + "/../b_database")
            .addScriptLocation(projectBaseDir(DashboardServiceImplIT.class) + "/../database")
            .build();
    */
    // @formatter:on

    @Rule
    public SpringMethodRule methodRule = new SpringMethodRule();

    @ClassRule
    public static final SpringClassRule clazzRule = new SpringClassRule();

}
