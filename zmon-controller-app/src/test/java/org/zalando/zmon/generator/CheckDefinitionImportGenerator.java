package org.zalando.zmon.generator;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.DefinitionStatus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CheckDefinitionImportGenerator implements RandomDataGenerator<CheckDefinitionImport> {
    private CheckDefinitionImport generate(String name, String sourceUrl) {

        final CheckDefinitionImport c = new CheckDefinitionImport();
        c.setName(name);
        c.setDescription("Generated check definition description");
        c.setTechnicalDetails("Generated technical details");
        c.setPotentialAnalysis("Generated potential analysis");
        c.setPotentialImpact("Generated potential impact");
        c.setPotentialSolution("Generated potential solution");
        c.setOwningTeam("Platform/Software");
        c.setEntities(ImmutableList.<Map<String, String>>of(ImmutableMap.of("type", "zomcat")));
        c.setInterval(60L);
        c.setCommand("zomcat().health()");
        c.setStatus(DefinitionStatus.ACTIVE);
        c.setSourceUrl(sourceUrl);
        c.setLastModifiedBy("pribeiro");

        return c;
    }

    public CheckDefinitionImport generate() {
        return generate("Generated check definition name",
                "https://scm.example.org/scm/platform/zmon2-software-checks.git/zmon-checks/zomcat-health.yaml");
    }

    public CheckDefinitionImport generateRandom() {
        return generate(RandomStringUtils.random(16), RandomStringUtils.random(16));
    }
}
