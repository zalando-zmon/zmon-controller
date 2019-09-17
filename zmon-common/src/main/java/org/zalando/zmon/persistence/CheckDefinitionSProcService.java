package org.zalando.zmon.persistence;

import java.util.Date;
import java.util.List;

import org.zalando.zmon.domain.*;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

@SProcService
public interface CheckDefinitionSProcService {

    @SProcCall
    List<CheckDefinition> getCheckDefinitions(@SProcParam DefinitionStatus status,
            @SProcParam List<Integer> checkDefinitionIds);

    @SProcCall
    List<CheckDefinition> getCheckDefinitionsByOwningTeam(@SProcParam DefinitionStatus status,
            @SProcParam List<String> owningTeams);

    @SProcCall
    CheckDefinitions getAllCheckDefinitions(@SProcParam DefinitionStatus status);

    @SProcCall
    CheckDefinitions getCheckDefinitionsDiff(@SProcParam Long lastSnapshotId);

    @SProcCall
    CheckDefinitionImportResult createOrUpdateCheckDefinition(@SProcParam CheckDefinitionImport checkDefinition,
                                                              @SProcParam String userName,
                                                              @SProcParam List<String> teams,
                                                              @SProcParam boolean isAdmin,
                                                              @SProcParam boolean isRuntimeEnabled,
                                                              @SProcParam DefinitionRuntime defaultRuntime);

    @SProcCall
    CheckDefinition deleteCheckDefinition(@SProcParam String userName, @SProcParam String name,
            @SProcParam String owningTeam);

    @SProcCall
    List<CheckDefinition> deleteDetachedCheckDefinitions();

    @SProcCall
    List<HistoryEntry> getCheckDefinitionHistory(@SProcParam int checkDefinitionId, @SProcParam int limit,
            @SProcParam Date from, @SProcParam Date to, @SProcParam HistoryAction action);

    @SProcCall
    List<Integer> deleteUnusedCheckDefinition(@SProcParam int id);

    @SProcCall
    Date getCheckLastModifiedMax();
}
