package org.zalando.zmon.service;

import java.util.*;

import javax.annotation.Nullable;

import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.CheckDefinitions;
import org.zalando.zmon.domain.CheckDefinitionsDiff;
import org.zalando.zmon.domain.CheckResults;
import org.zalando.zmon.domain.DefinitionStatus;
import org.zalando.zmon.domain.ExecutionStatus;
import org.zalando.zmon.api.domain.CheckChartResult;

import com.fasterxml.jackson.databind.JsonNode;

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

    List<CheckResults> getCheckAlertResults(int alertId, int limit);

    CheckDefinition createOrUpdateCheckDefinition(CheckDefinitionImport checkDefinition);

    void deleteCheckDefinition(String userName, String name, String owningTeam);

    void deleteDetachedCheckDefinitions();

    List<Integer> deleteUnusedCheckDef(int id, Collection<String> teams);

    JsonNode getEntityProperties();

    CheckChartResult getChartResults(int checkId, String entity, int limit);

    CheckChartResult getFilteredLastResults(String checkId, String filter, int limit);

    JsonNode getAlertCoverage(JsonNode filter);

    Date getMaxCheckDefinitionLastModified();
}
