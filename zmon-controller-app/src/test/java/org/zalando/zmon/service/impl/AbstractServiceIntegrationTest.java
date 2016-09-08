package org.zalando.zmon.service.impl;

import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

/**
 * Provide some {@link Rule}s for Tests.
 * 
 * @author jbellmann
 *
 */
public abstract class AbstractServiceIntegrationTest {

    @Rule
    public SpringMethodRule methodRule = new SpringMethodRule();

    @ClassRule
    public static final SpringClassRule clazzRule = new SpringClassRule();

}
