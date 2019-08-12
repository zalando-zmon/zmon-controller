package org.zalando.zmon.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.zmon.api.domain.*;
import org.zalando.zmon.domain.*;
import org.zalando.zmon.persistence.CheckDefinitionImportResult;

import javax.annotation.Nullable;
import java.util.*;

// TODO split into multiple services
public interface ZMonService {

    ExecutionStatus getStatus();

    List<String> getAllTeams();

    Optional<CheckDefinition> getCheckDefinitionById(final int id);

    CheckDefinitions getCheckDefinitions(@Nullable DefinitionStatus status);

    List<CheckDefinition> getCheckDefinitions(@Nullable DefinitionStatus status, List<Integer> checkDefinitionIds);

    List<CheckDefinition> getCheckDefinitions(@Nullable DefinitionStatus status, Set<String> teams);

    CheckDefinitionsDiff getCheckDefinitionsDiff(Long snapshotId);

    List<CheckResults> getCheckResults(int checkId, String entity, int limit);

    List<CheckResults> getCheckResultsWithoutEntities(int checkId, String entity, int limit);

    List<CheckResults> getCheckAlertResults(int alertId, int limit);

    CheckDefinitionImportResult createOrUpdateCheckDefinition(CheckDefinitionImport checkDefinition, String userName, List<String> teams);

    CheckDefinitionImportResult createOrUpdateCheckDefinition(CheckDefinitionImport checkDefinition, String userName, List<String> teams, boolean isAdmin);

    void deleteCheckDefinition(String userName, String name, String owningTeam);

    void deleteDetachedCheckDefinitions();

    List<Integer> deleteUnusedCheckDef(int id);

    JsonNode getEntityProperties();

    CheckChartResult getChartResults(int checkId, String entity, int limit);

    CheckChartResult getFilteredLastResults(String checkId, String filter, int limit);

    JsonNode getAlertCoverage(JsonNode filter);

    EntityFilterResponse getEntitiesMatchingFilters(EntityFilterRequest request);

    Date getMaxCheckDefinitionLastModified();

    List<AlertResult> getAlertResults(JsonNode filter);
}
