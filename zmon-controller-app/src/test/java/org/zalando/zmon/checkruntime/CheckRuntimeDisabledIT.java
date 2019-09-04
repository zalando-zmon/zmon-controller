package org.zalando.zmon.checkruntime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.controller.domain.CheckRuntimeConfigDto;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.DefinitionRuntime;
import org.zalando.zmon.persistence.CheckDefinitionImportResult;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = {"server.ssl.enabled=false", "zmon.checkruntime.enabled=false"}
)
@Transactional
public class CheckRuntimeDisabledIT extends BaseCheckRuntimeIT {
    @Test
    public void checkDefinitionIsCreatedWithDefaultRuntimeAccordingToConfig() {
        CheckDefinitionImport checkDefinitionImport = checkImportGenerator.generateRandom();

        CheckDefinitionImportResult result = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS);

        assertThat(result.isPermissionDenied(), is(false));
        assertThat(result.getEntity().getRuntime(), is(DefinitionRuntime.PYTHON_2));
    }

    @Test
    public void switchingRuntimeIsNotAllowed() {
        CheckDefinitionImport checkDefinitionImport = checkImportGenerator.generateRandom();
        CheckDefinition checkDefinition = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS).getEntity();

        checkDefinitionImport.setId(checkDefinition.getId());
        checkDefinitionImport.setRuntime(DefinitionRuntime.PYTHON_3);
        CheckDefinitionImportResult result = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS);

        assertThat(result.isPermissionDenied(), is(true));
    }

    @Test
    public void checkRuntimeConfigIsExposedInPrivateApiAccordingToConfig() throws Exception {
        CheckRuntimeConfigDto checkRuntimeConfig = getCheckRuntimeConfigWithPrivateApi();

        assertThat(checkRuntimeConfig.isEnabled(), is(false));
        assertThat(checkRuntimeConfig.getDefaultRuntime(), is(DefinitionRuntime.PYTHON_2));
        assertThat(checkRuntimeConfig.getMigrationGuideUrl(), is(""));
    }

    @Test
    public void runtimePropertyIsNotExposedThroughPublicApi() throws Exception {
        CheckDefinitionImport checkDefinitionImport = checkImportGenerator.generateRandom();
        CheckDefinitionImportResult result = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS);

        Map<String, Object> checkDefinition = getCheckDefinitionFromPublicApiAsMap(result.getEntity().getId());

        assertThat(checkDefinition.containsKey("runtime"), is(false));
    }
}
