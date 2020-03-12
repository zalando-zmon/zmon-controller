package org.zalando.zmon.persistence;

import java.util.Date;
import java.util.List;

import org.zalando.zmon.domain.*;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

@SProcService
public interface AlertDefinitionSProcService {

    @SProcCall
    AlertDefinitions getActiveAlertDefinitionsDiff();

    @SProcCall
    AlertDefinitions getAlertDefinitionsDiff(@SProcParam Long lastSnapshotId);

    @SProcCall
    List<AlertDefinition> getAllAlertDefinitions();

    @SProcCall
    List<AlertDefinition> getAlertDefinitions(@SProcParam DefinitionStatus status, @SProcParam List<Integer> alertIds);

    @SProcCall
    List<AlertDefinition> getAlertDefinitionsByTeam(@SProcParam DefinitionStatus status,
            @SProcParam List<String> teams);

    @SProcCall
    List<AlertDefinition> getAlertDefinitionsByTeamAndTag(@SProcParam DefinitionStatus status,
            @SProcParam List<String> teams, @SProcParam List<String> tags);

    @SProcCall
    AlertDefinitionOperationResult createOrUpdateAlertDefinitionTree(@SProcParam AlertDefinition alertDefinition);

    @SProcCall
    AlertDefinitionOperationResult deleteAlertDefinition(@SProcParam int alertDefinitionId);

    @SProcCall
    AlertCommentOperationResult addAlertCommentRecord(@SProcParam AlertCommentRecord comment);

    @SProcCall
    AlertCommentRecord getAlertCommentRecordById(@SProcParam int id);

    @SProcCall
    List<AlertCommentRecord> getAlertCommentRecords(@SProcParam int alertDefinitionId, @SProcParam int limit,
                                              @SProcParam int offset);

    @SProcCall
    AlertCommentRecord deleteAlertCommentRecord(@SProcParam int commentId);

    @SProcCall
    List<HistoryEntry> getAlertDefinitionHistory(@SProcParam int alertDefinitionId, @SProcParam int limit,
            @SProcParam Date from, @SProcParam Date to);

    @SProcCall
    List<Integer> getAlertIdsByCheckId(@SProcParam int alertDefinitionId);

    @SProcCall
    List<Integer> getAlertIdsByStatus(@SProcParam DefinitionStatus status);

    @SProcCall
    AlertDefinition getAlertDefinitionNode(@SProcParam int alertDefinitionId);

    @SProcCall
    List<AlertDefinition> getAlertDefinitionChildren(@SProcParam int alertDefinitionId);

    @SProcCall
    List<String> getAllTags();

    @SProcCall
    Date getAlertLastModifiedMax();
}
