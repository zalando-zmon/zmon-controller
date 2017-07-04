package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.zalando.zmon.config.EventLogProperties;
import org.zalando.zmon.event.Event;
import org.zalando.zmon.event.EventlogEvent;
import org.zalando.zmon.service.EventLogService;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventLogServiceImpl implements EventLogService {
    private final static Logger LOG = LoggerFactory.getLogger(HistoryServiceImpl.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private final EventLogProperties eventLogProperties;
    private final RestOperations restOperations;

    @Autowired
    private EventLogServiceImpl(final EventLogProperties eventLogProperties, final RestOperations restOperations) {
        this.eventLogProperties = eventLogProperties;
        this.restOperations = restOperations;
    }

    @Override
    public List<Event> getAlertEvents(final int alertDefinitionId,
                                      @Nullable final Integer limit,
                                      @Nullable final Long fromMillis,
                                      @Nullable final Long toMillis) {
        final Optional<URI> baseQuery = getBaseQueryUri(limit, fromMillis, toMillis);
        if (!baseQuery.isPresent()) {
            return ImmutableList.of();
        }

        final String typesFilter = eventLogProperties.getAlertHistoryEventsFilter().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        final URIBuilder queryBuilder = new URIBuilder(baseQuery.get())
                .addParameter("types", typesFilter)
                .addParameter("key", "alertId")
                .addParameter("value", String.valueOf(alertDefinitionId));
        try {
            final URI query = queryBuilder.build();
            final EventlogEvent[] events = restOperations.getForObject(query, EventlogEvent[].class);

            return Arrays.stream(events).
                    map(this::convert).
                    collect(Collectors.toList());
        } catch (URISyntaxException e) {
            LOG.error("Failed to query for alert events", e);
        }

        return ImmutableList.of();
    }

    @Override
    public List<Event> getCheckEvents(final int checkDefinitionId,
                                      @Nullable final Integer limit,
                                      @Nullable final Long fromMillis,
                                      @Nullable final Long toMillis) {
        final Optional<URI> baseQuery = getBaseQueryUri(limit, fromMillis, toMillis);
        if (!baseQuery.isPresent()) {
            return ImmutableList.of();
        }

        final String typesFilter = eventLogProperties.getCheckHistoryEventsFilter().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        final URIBuilder queryBuilder = new URIBuilder(baseQuery.get())
                .addParameter("types", typesFilter)
                .addParameter("key", "checkId")
                .addParameter("value", String.valueOf(checkDefinitionId));
        try {
            final URI query = queryBuilder.build();

            return Arrays.asList(restOperations.getForObject(query, Event[].class));
        } catch (URISyntaxException e) {
            LOG.error("Failed to query for check events", e);
        }
        return ImmutableList.of();
    }


    private Optional<URI> getBaseQueryUri(final Integer limit, final Long fromMillis, final Long toMillis) {
        final URIBuilder baseQueryBuilder;
        try {
            baseQueryBuilder = new URIBuilder(eventLogProperties.getUrl().toString());
        } catch (URISyntaxException e) {
            LOG.error("Invalid event log URI", e);
            return Optional.empty();
        }

        if (limit != null) {
            baseQueryBuilder.addParameter("limit", limit.toString());
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

    private Event convert(EventlogEvent in) {
        final String typeName = in.getTypeName();
        final String flowId = in.getFlowId();

        final Event e = new Event();
        e.setTypeName(typeName);
        e.setTime(in.getTime());
        e.setFlowId(flowId);
        e.setTypeId(in.getTypeId());

        for (Map.Entry<String, JsonNode> ie : in.getAttributes().entrySet()) {
            try {
                e.setAttribute(ie.getKey(), mapper.writeValueAsString(ie.getValue()));
            } catch (JsonProcessingException ex) {
                LOG.error("Failed to convert event {} of type {}", flowId, typeName, ex);
            }
        }

        return e;
    }

    private static class AlertEventsResponse extends LinkedList<EventlogEvent> {
    }

    private static class CheckEventsResponse extends LinkedList<Event> {
    }
}
