package org.zalando.zmon.checkruntime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.controller.domain.CheckRuntimeConfigDto;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.DefinitionRuntime;
import org.zalando.zmon.persistence.CheckDefinitionImportResult;
import org.zalando.zmon.persistence.CheckDefinitionSProcService;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = {"server.ssl.enabled=false", "zmon.checkruntime.enabled=true", "zmon.checkruntime.migrationGuideUrl=http://example.com"}
)
@Transactional
public class CheckRuntimeEnabledIT extends BaseCheckRuntimeIT {
    @Autowired
    private CheckDefinitionSProcService sProcService;

    @Test
    public void checkDefinitionIsCreatedWithDefaultRuntimeAccordingToConfig() {
        CheckDefinitionImport checkDefinitionImport = checkImportGenerator.generate();

        CheckDefinitionImportResult result = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS);

        assertThat(result.isPermissionDenied(), is(false));
        assertThat(result.getEntity().getRuntime(), is(DefinitionRuntime.PYTHON_3));
    }

    @Test
    public void checkRuntimeConfigIsExposedInPrivateApiAccordingToConfig() throws Exception {
        CheckRuntimeConfigDto checkRuntimeConfig = getCheckRuntimeConfigWithPrivateApi();

        assertThat(checkRuntimeConfig.isEnabled(), is(true));
        assertThat(checkRuntimeConfig.getDefaultRuntime(), is(DefinitionRuntime.PYTHON_3));
        assertThat(checkRuntimeConfig.getMigrationGuideUrl(), is("http://example.com"));
    }

    @Test
    public void switchingRuntimeBackToPython2IsNotAllowedForNewChecks() {
        CheckDefinitionImport checkDefinitionImport = checkImportGenerator.generateRandom();
        Integer checkDefinitionId = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS).getEntity().getId();

        checkDefinitionImport.setId(checkDefinitionId);
        checkDefinitionImport.setRuntime(DefinitionRuntime.PYTHON_2);
        CheckDefinitionImportResult result = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS);

        assertThat(result.isPermissionDenied(), is(true));
    }

    @Test
    public void switchingRuntimeToPython3AndBackToPython2IsAllowedForOldChecks() {
        CheckDefinitionImportResult result;
        CheckDefinitionImport checkDefinitionImport = checkImportGenerator.generateRandom();
        checkDefinitionImport.setRuntime(DefinitionRuntime.PYTHON_2);
        // Create a check definition as if it was before changing runtime settings,
        // that is - checkruntime.enabled=false, checkruntime.defaultRuntime=PYTHON_2
        Integer checkDefinitionId = sProcService.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS, false, false, DefinitionRuntime.PYTHON_2)
                .getEntity().getId();
        checkDefinitionImport.setId(checkDefinitionId);

        // START - Switch its runtime to Python 3
        checkDefinitionImport.setRuntime(DefinitionRuntime.PYTHON_3);
        result = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS);

        assertThat(result.isPermissionDenied(), is(false));
        assertThat(getCheckDefinitionRuntimeWithSProc(checkDefinitionId), is(DefinitionRuntime.PYTHON_3));
        // END

        // START - And then switch it back to Python 2
        checkDefinitionImport.setRuntime(DefinitionRuntime.PYTHON_2);
        result = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS);

        assertThat(result.isPermissionDenied(), is(false));
        assertThat(getCheckDefinitionRuntimeWithSProc(checkDefinitionId), is(DefinitionRuntime.PYTHON_2));
        // END
    }

    @Test
    public void runtimePropertyIsExposedThroughPublicApi() throws Exception {
        CheckDefinitionImport checkDefinitionImport = checkImportGenerator.generateRandom();
        CheckDefinitionImportResult result = service.createOrUpdateCheckDefinition(checkDefinitionImport, USER_NAME, USER_TEAMS);

        Map<String, Object> checkDefinitionMap = getCheckDefinitionFromPublicApiAsMap(result.getEntity().getId());

        assertThat(checkDefinitionMap.get("runtime"), is("PYTHON_3"));
    }
}
