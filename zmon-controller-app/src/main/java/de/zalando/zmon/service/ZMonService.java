package de.zalando.zmon.service;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;

import de.zalando.zmon.domain.CheckDefinition;
import de.zalando.zmon.domain.CheckDefinitionImport;
import de.zalando.zmon.domain.CheckDefinitions;
import de.zalando.zmon.domain.CheckDefinitionsDiff;
import de.zalando.zmon.domain.CheckResults;
import de.zalando.zmon.domain.DefinitionStatus;
import de.zalando.zmon.domain.ExecutionStatus;
import de.zalando.zmon.rest.domain.CheckChartResult;

// TODO split into multiple services
public interface ZMonService {

    ExecutionStatus getStatus();

    List<String> getAllTeams();

    List<CheckDefinition> getCheckDefinitionsById(final int id);

    CheckDefinitions getCheckDefinitions(@Nullable DefinitionStatus status);

    List<CheckDefinition> getCheckDefinitions(@Nullable DefinitionStatus status, List<Integer> checkDefinitionIds);

    List<CheckDefinition> getCheckDefinitions(@Nullable DefinitionStatus status, Set<String> teams);

    CheckDefinitionsDiff getCheckDefinitionsDiff(Long snapshotId);

    List<CheckResults> getCheckResults(int checkId, String entity, int limit);

    List<CheckResults> getCheckAlertResults(int alertId, int limit);

    CheckDefinition createOrUpdateCheckDefinition(CheckDefinitionImport checkDefinition);

    void deleteCheckDefinition(String userName, String name, String owningTeam);

    void deleteDetachedCheckDefinitions();

    JsonNode getEntityProperties();

    CheckChartResult getChartResults(int checkId, String entity, int limit);

    CheckChartResult getFilteredLastResults(String checkId, String filter, int limit);
}
