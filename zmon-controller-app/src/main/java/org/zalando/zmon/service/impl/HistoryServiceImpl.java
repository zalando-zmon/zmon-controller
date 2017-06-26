package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;
import org.zalando.zmon.config.EventLogProperties;
import org.zalando.zmon.domain.Activity;
import org.zalando.zmon.domain.ActivityDiff;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.HistoryAction;
import org.zalando.zmon.domain.HistoryEntry;
import org.zalando.zmon.domain.HistoryType;
import org.zalando.zmon.event.Event;
import org.zalando.zmon.event.EventlogEvent;
import org.zalando.zmon.event.ZMonEventType;
import org.zalando.zmon.persistence.AlertDefinitionSProcService;
import org.zalando.zmon.persistence.CheckDefinitionSProcService;
import org.zalando.zmon.service.HistoryService;
import org.zalando.zmon.util.HistoryUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class HistoryServiceImpl implements HistoryService {

    private static final int DEFAULT_HISTORY_LIMIT = 50;
    private final static Logger LOG = LoggerFactory.getLogger(HistoryServiceImpl.class);
    private static final Comparator<Activity> ACTIVITY_TIME_COMPARATOR = Comparator.comparing(Activity::getTime);

    private final RestOperations restOperations;

    private final CheckDefinitionSProcService checkDefinitionSProc;

    private final AlertDefinitionSProcService alertDefinitionSProc;

    private final EventLogProperties eventLogProperties;

    private ObjectMapper mapper = new ObjectMapper();

    public HistoryServiceImpl(final RestOperations restOperations,
                              final CheckDefinitionSProcService checkDefinitionSProc,
                              final AlertDefinitionSProcService alertDefinitionSProc,
                              final EventLogProperties eventLogProperties) {
        this.restOperations = restOperations;
        this.checkDefinitionSProc = checkDefinitionSProc;
        this.alertDefinitionSProc = alertDefinitionSProc;
        this.eventLogProperties = eventLogProperties;
    }

    @Override
    public List<Activity> getHistory(final int alertDefinitionId, final Integer limit, final Long from, final Long to) {
        final Integer realLimit = resolveLimit(limit, from, to);

        final Long fromMillis = from == null ? null : from * 1000;
        final Long toMillis = to == null ? null : to * 1000;

        final List<AlertDefinition> definitions = alertDefinitionSProc.getAlertDefinitions(null,
                ImmutableList.of(alertDefinitionId));

        if (!definitions.isEmpty()) {
            final Optional<URI> baseQuery = getBaseQueryUri(limit, realLimit, fromMillis, toMillis);
            if (baseQuery.isPresent()) {
                final URI uri = baseQuery.get();
                List<Event> eventsByAlertId = getAlertEvents(uri, alertDefinitionId);
                List<Event> eventsByCheckId = getCheckEvents(uri, definitions.get(0).getCheckDefinitionId());
                return mergeEvents(realLimit, eventsByCheckId, eventsByAlertId);
            }
        }

        return ImmutableList.of();
    }

    private Optional<URI> getBaseQueryUri(final Integer limit, final Integer realLimit, final Long fromMillis, final Long toMillis) {
        final URIBuilder baseQueryBuilder;
        try {
            baseQueryBuilder = new URIBuilder(eventLogProperties.getUrl().toString());
        } catch (URISyntaxException e) {
            LOG.error("Invalid event log URI", e);
            return Optional.empty();
        }

        if (limit != null) {
            baseQueryBuilder.addParameter("limit", realLimit.toString());
        }
        if (fromMillis != null) {
            baseQueryBuilder.addParameter("from", fromMillis.toString());
        }
        if (toMillis != null) {
            baseQueryBuilder.addParameter("to", toMillis.toString());
        }

        try {
            return Optional.of(baseQueryBuilder.build());
        } catch (URISyntaxException e) {
            LOG.error("Failed to build base query", e);
        }
        return Optional.empty();
    }

    private List<Event> getAlertEvents(final URI baseQuery, final int alertDefinitionId) {
        final String typesFilter = eventLogProperties.getAlertHistoryEventsFilter().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        final URIBuilder alertsQueryBuilder = new URIBuilder(baseQuery)
            .addParameter("types", typesFilter)
            .addParameter("key", "alertId")
            .addParameter("value", String.valueOf(alertDefinitionId));
        try {
            final URI query = alertsQueryBuilder.build();
            final AlertEventsResponse tempEvents = restOperations.getForObject(query, AlertEventsResponse.class);
            return tempEvents.stream().map(this::convert).collect(Collectors.toList());
        } catch (URISyntaxException e) {
            LOG.error("Failed to create query URI", e);
        }
        return ImmutableList.of();
    }

    private static class AlertEventsResponse extends LinkedList<EventlogEvent> {}

    private List<Event> getCheckEvents(final URI baseQuery, final int checkDefinitionId) {
        final String typesFilter = eventLogProperties.getCheckHistoryEventsFilter().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        final URIBuilder alertsQueryBuilder = new URIBuilder(baseQuery)
            .addParameter("types", typesFilter)
            .addParameter("key", "checkId")
            .addParameter("value", String.valueOf(checkDefinitionId));
        try {
            final URI query = alertsQueryBuilder.build();
            return restOperations.getForObject(query, CheckEventsResponse.class);
        } catch (URISyntaxException e) {
            LOG.error("Failed to create query URI", e);
        }
        return ImmutableList.of();
    }

    private static class CheckEventsResponse extends LinkedList<Event> {}

    private Event convert(EventlogEvent in) {
        final String typeName = in.getTypeName();
        final String flowId = in.getFlowId();

        final Event e = new Event();
        e.setTypeName(typeName);
        e.setTime(in.getTime());
        e.setFlowId(flowId);
        e.setTypeId(in.getTypeId());

        for(Map.Entry<String, JsonNode> ie : in.getAttributes().entrySet()) {
            try {
                e.setAttribute(ie.getKey(), mapper.writeValueAsString(ie.getValue()));
            }
            catch(JsonProcessingException ex) {
                LOG.error("Failed to convert event {} of type {}", flowId, typeName, ex);
            }
        }

        return e;
    }

    private Activity createActivity(final Event event) {
        final Activity activity = new Activity();
        activity.setTime(dateToSeconds(event.getTime()));
        activity.setTypeId(event.getTypeId());
        activity.setTypeName(event.getTypeName());
        activity.setAttributes(event.getAttributes());
        return activity;
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

    @Override
    public List<ActivityDiff> getCheckDefinitionHistory(final int checkDefinitionId, final Integer limit,
                                                        final Long from, final Long to) {
        final List<HistoryEntry> databaseHistory = checkDefinitionSProc.getCheckDefinitionHistory(checkDefinitionId,
                resolveLimit(limit, from, to), secondsToDate(from), secondsToDate(to));

        final List<ActivityDiff> history = new LinkedList<>();
        for (final HistoryEntry entry : databaseHistory) {
            history.add(createActivityDiff(entry, resolveCheckDefinitionEventType(entry.getAction())));
        }

        return history;
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

    private ActivityDiff createActivityDiff(final HistoryEntry entry, final ZMonEventType eventType) {
        return fillActivityDiff(new ActivityDiff(), entry, eventType);
    }

    private ActivityDiff fillActivityDiff(final ActivityDiff activity, final HistoryEntry entry,
                                          final ZMonEventType eventType) {
        activity.setTime(dateToSeconds(entry.getTimestamp()));
        activity.setTypeId(eventType.getId());
        activity.setTypeName(eventType.getName());
        activity.setAttributes(entry.getRowData());
        activity.setRecordId(entry.getRecordId());
        activity.setAction(entry.getAction());
        activity.setChangedAttributes(entry.getChangedFields());
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

    private ZMonEventType resolveEventType(final HistoryAction action, final HistoryType historyType) {
        switch (historyType) {

            case CHECK_DEFINITION:
                return resolveCheckDefinitionEventType(action);

            case ALERT_DEFINITION:
                return resolveAlertDefinitionEventType(action);

            default:
                throw new IllegalArgumentException("History type not supported: " + action);
        }

    }
}
