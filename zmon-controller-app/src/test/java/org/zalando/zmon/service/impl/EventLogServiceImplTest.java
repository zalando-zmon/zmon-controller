package org.zalando.zmon.service.impl;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;
import org.zalando.zmon.config.EventLogProperties;
import org.zalando.zmon.event.Event;
import org.zalando.zmon.event.EventlogEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventLogServiceImplTest {
    private static String EVENTLOG_URL = "http://localhost:12345";

    @Mock
    private EventLogProperties eventLogProperties;
    @Mock
    private RestOperations restOperations;

    @InjectMocks
    private EventLogServiceImpl eventLogService;

    @Before
    public void setUp() throws Exception {
        when(eventLogProperties.getAlertHistoryEventsFilter()).thenReturn(Arrays.asList(1, 2, 3));
        when(eventLogProperties.getCheckHistoryEventsFilter()).thenReturn(Arrays.asList(5, 6, 7));
        when(eventLogProperties.getUrl()).thenReturn(new URL(EVENTLOG_URL));
    }

    @Test
    public void testGetAlertEventsQueriesSpecificAlerts() throws URISyntaxException {
        final URI query = new URIBuilder(EVENTLOG_URL)
                .addParameter("limit", "5")
                .addParameter("from", "1000")
                .addParameter("to", "2000")
                .addParameter("types", "1,2,3")
                .addParameter("key", "alertId")
                .addParameter("value", "1")
                .build();

        when(restOperations.getForObject(query, EventlogEvent[].class)).thenReturn(new EventlogEvent[0]);
        List<Event> events = eventLogService.getAlertEvents(1, 5, new Long(1000), new Long(2000));
        assertEquals(events.size(), 0);
    }

    @Test
    public void testGetAlertEventsQueriesSpecificChecks() throws URISyntaxException {
        final URI query = new URIBuilder(EVENTLOG_URL)
                .addParameter("limit", "5")
                .addParameter("from", "1000")
                .addParameter("to", "2000")
                .addParameter("types", "5,6,7")
                .addParameter("key", "checkId")
                .addParameter("value", "1")
                .build();

        when(restOperations.getForObject(query, Event[].class)).thenReturn(new Event[0]);
        List<Event> events = eventLogService.getCheckEvents(1, 5, new Long(1000), new Long(2000));
        assertEquals(events.size(), 0);
    }
}
