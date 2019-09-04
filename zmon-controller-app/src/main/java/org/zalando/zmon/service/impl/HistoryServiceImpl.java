package org.zalando.zmon.service.impl;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import org.zalando.zmon.domain.*;
import org.zalando.zmon.event.Event;
import org.zalando.zmon.event.ZMonEventType;
import org.zalando.zmon.persistence.AlertDefinitionSProcService;
import org.zalando.zmon.persistence.CheckDefinitionSProcService;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.EventLogService;
import org.zalando.zmon.service.HistoryService;
import org.zalando.zmon.util.HistoryUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class HistoryServiceImpl implements HistoryService {

    private static final int DEFAULT_HISTORY_LIMIT = 50;
    private static final Comparator<Activity> ACTIVITY_TIME_COMPARATOR = Comparator.comparing(Activity::getTime);
    private static final List<String> UNIFIED_DIFF_IGNORED_FIELDS = Arrays.asList("last_modified", "last_modified_by");
    private static final List<String> UNIFIED_DIFF_CODE_FIELDS = Arrays.asList("command", "condition");

    private final CheckDefinitionSProcService checkDefinitionSProc;

    private final AlertDefinitionSProcService alertDefinitionSProc;

    private final EventLogService eventLog;

    private final DefaultZMonPermissionService authorityService;

    public HistoryServiceImpl(final CheckDefinitionSProcService checkDefinitionSProc,
                              final AlertDefinitionSProcService alertDefinitionSProc,
                              final EventLogService eventLog,
                              final DefaultZMonPermissionService authorityService) {
        this.eventLog = eventLog;
        this.checkDefinitionSProc = checkDefinitionSProc;
        this.alertDefinitionSProc = alertDefinitionSProc;
        this.authorityService = authorityService;
    }

    @Override
    public List<Activity> getHistory(final int alertDefinitionId, final Integer limit, final Long from, final Long to) {
        final Integer realLimit = resolveLimit(limit, from, to);

        final Long fromMillis = from == null ? null : from * 1000;
        final Long toMillis = to == null ? null : to * 1000;

        final List<AlertDefinition> definitions = alertDefinitionSProc.getAlertDefinitions(null,
                ImmutableList.of(alertDefinitionId));

        if (!definitions.isEmpty()) {
            List<Event> eventsByAlertId = eventLog.getAlertEvents(alertDefinitionId,
                    realLimit, fromMillis, toMillis);
            List<Event> eventsByCheckId = eventLog.getCheckEvents(definitions.get(0).getCheckDefinitionId(),
                    realLimit, fromMillis, toMillis);

            return mergeEvents(realLimit, eventsByCheckId, eventsByAlertId);
        }

        return ImmutableList.of();
    }

    private List<Activity> mergeEvents(final Integer limit, final List<Event> eventsByCheckId,
                                       final List<Event> eventsByAlertId) {

        final int size = (eventsByCheckId == null ? 0 : eventsByCheckId.size())
                + (eventsByAlertId == null ? 0 : eventsByAlertId.size());

        if (size > 0) {
            final List<Activity> activities = new ArrayList<>(size);
            if (eventsByCheckId != null && !eventsByCheckId.isEmpty()) {
                for (final Event event : eventsByCheckId) {
                    activities.add(createActivity(event));
                }
            }

            if (eventsByAlertId != null && !eventsByAlertId.isEmpty()) {
                for (final Event event : eventsByAlertId) {
                    activities.add(createActivity(event));
                }
            }

            final List<Activity> sorted = Ordering.from(ACTIVITY_TIME_COMPARATOR).immutableSortedCopy(activities);
            return limit == null ? sorted : sorted.subList(0, limit);
        }

        return ImmutableList.of();
    }

    private Activity createActivity(final Event event) {
        final Activity activity = new Activity();
        activity.setTime(dateToSeconds(event.getTime()));
        activity.setTypeId(event.getTypeId());
        activity.setTypeName(event.getTypeName());
        activity.setAttributes(event.getAttributes());
        return activity;
    }

    @Override
    public List<ActivityDiff> getCheckDefinitionHistory(final int checkDefinitionId, final Integer limit,
                                                        final Long from, final Long to) {
        return getCheckDefinitionHistory(checkDefinitionId, limit, from, to, null);
    }

    @Override
    public List<ActivityDiff> getCheckDefinitionHistory(final int checkDefinitionId, final Integer limit,
                                                        final Long from, final Long to, final HistoryAction action) {
        final List<HistoryEntry> databaseHistory = checkDefinitionSProc.getCheckDefinitionHistory(checkDefinitionId,
                resolveLimit(limit, from, to), secondsToDate(from), secondsToDate(to), action);

        final List<ActivityDiff> history = new LinkedList<>();
        for (final HistoryEntry entry : databaseHistory) {
            history.add(createActivityDiff(entry, resolveCheckDefinitionEventType(entry.getAction())));
        }

        return history;
    }

    @Override
    public boolean restoreCheckDefinition(int checkDefinitionHistoryId) {
        return checkDefinitionSProc.restoreCheckDefinition(checkDefinitionHistoryId, authorityService.getUserName(),
                new ArrayList<>(authorityService.getTeams()), authorityService.hasAdminAuthority());
    }

    @Override
    public List<ActivityDiff> getAlertDefinitionHistory(final int alertDefinitionId, final Integer limit,
                                                        final Long from, final Long to) {
        final List<HistoryEntry> databaseHistory = alertDefinitionSProc.getAlertDefinitionHistory(alertDefinitionId,
                resolveLimit(limit, from, to), secondsToDate(from), secondsToDate(to));

        final List<ActivityDiff> history = new LinkedList<>();
        for (final HistoryEntry entry : databaseHistory) {
            history.add(createActivityDiff(entry, resolveAlertDefinitionEventType(entry.getAction())));
        }

        return history;
    }

    private Integer resolveLimit(final Integer limit, final Long from, final Long to) {
        return limit != null ? limit : from == null && to == null ? DEFAULT_HISTORY_LIMIT : null;
    }

    private Date secondsToDate(final Long time) {
        return time == null ? null : new Date(time * 1000);
    }

    private long dateToSeconds(final Date date) {
        return date.getTime() / 1000;
    }

    private List<String> rowDataToList(Map<String, String> rowData) {
        return rowData.entrySet().stream()
                .flatMap(item -> {
                    String key = item.getKey().substring(item.getKey().indexOf('_') + 1);
                    if (UNIFIED_DIFF_IGNORED_FIELDS.contains(key)) {
                        return Stream.empty();
                    }
                    String value = item.getValue();

                    if (UNIFIED_DIFF_CODE_FIELDS.contains(key)) {
                        return Stream.concat(
                            Stream.of(String.format("%s: ", key)),
                            Arrays.stream(value.split("\n")).map(line -> "  " + line)
                        );
                    }
                    else {
                        return Stream.of(String.format("%s: %s", key, value));
                    }
                })
                .collect(Collectors.toList());
    }

    private String generateUnifiedDiff(HistoryAction action, Map<String, String> rowData, Map<String, String> changedFields) {
        List<String> rowDataLines;
        List<String> currentRowDataLines;

        if (action.equals(HistoryAction.INSERT)) {
            rowDataLines = Collections.emptyList();
            currentRowDataLines = rowDataToList(rowData);
        }
        else {
            Map<String, String> currentRowData = new HashMap<>(rowData);
            currentRowData.putAll(changedFields);

            rowDataLines = rowDataToList(rowData);
            currentRowDataLines = rowDataToList(currentRowData);
        }

        String unifiedDiff;
        try {
            unifiedDiff = String.join("\n", UnifiedDiffUtils.generateUnifiedDiff("definition",
                    "definition",
                    rowDataLines,
                    DiffUtils.diff(rowDataLines, currentRowDataLines),
                    50
            ));
        }
        catch(DiffException e) {
            unifiedDiff = "";
        }

        return !unifiedDiff.isEmpty() ? unifiedDiff : "--- definition\n+++ definition";
    }

    private ActivityDiff createActivityDiff(final HistoryEntry entry, final ZMonEventType eventType) {
        return fillActivityDiff(new ActivityDiff(), entry, eventType);
    }

    private ActivityDiff fillActivityDiff(final ActivityDiff activity, final HistoryEntry entry,
                                          final ZMonEventType eventType) {
        activity.setHistoryId(entry.getId());
        activity.setTime(dateToSeconds(entry.getTimestamp()));
        activity.setTypeId(eventType.getId());
        activity.setTypeName(eventType.getName());
        activity.setAttributes(entry.getRowData());
        activity.setRecordId(entry.getRecordId());
        activity.setAction(entry.getAction());
        activity.setChangedAttributes(entry.getChangedFields());
        activity.setUnifiedDiff(generateUnifiedDiff(entry.getAction(), entry.getRowData(), entry.getChangedFields()));
        activity.setLastModifiedBy(HistoryUtils.resolveModifiedBy(entry.getRowData(), entry.getChangedFields()));

        return activity;
    }

    private ZMonEventType resolveCheckDefinitionEventType(final HistoryAction action) {

        switch (action) {

            case INSERT:
                return ZMonEventType.CHECK_DEFINITION_CREATED;

            case UPDATE:
                return ZMonEventType.CHECK_DEFINITION_UPDATED;

            default:
                throw new IllegalArgumentException("Action not supported: " + action);
        }
    }

    private ZMonEventType resolveAlertDefinitionEventType(final HistoryAction action) {

        switch (action) {

            case INSERT:
                return ZMonEventType.ALERT_DEFINITION_CREATED;

            case UPDATE:
                return ZMonEventType.ALERT_DEFINITION_UPDATED;

            default:
                throw new IllegalArgumentException("Action not supported: " + action);
        }
    }
}
