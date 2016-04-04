package org.zalando.zmon.adapter;

import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.zalando.zmon.adapter.JsonAdapter;
import org.zalando.zmon.domain.Parameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;

public class JsonAdapterTest {

    private ObjectMapper objectMapper;

    private final JsonAdapter jsonAdapter = new JsonAdapter();

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testUnmarshalNull() throws Exception {
        MatcherAssert.assertThat(jsonAdapter.unmarshal(null), Matchers.nullValue());
    }

    @Test
    public void testUnmarshallJson() throws Exception {
        final JsonNode param0 = objectMapper.readTree("{\"value\":0,\"comment\":\"desc 0\",\"type\":\"int\"}");
        final JsonNode param1 = objectMapper.readTree("{\"value\":1,\"comment\":\"desc 1\",\"type\":\"float\"}");

        final ObjectNode node = objectMapper.createObjectNode();
        node.set("param0", param0);
        node.set("param1", param1);

        MatcherAssert.assertThat(jsonAdapter.unmarshal(node.toString()),
                Matchers.<Map<String, Parameter>> is(ImmutableMap.of("param0", new Parameter(0, "desc 0", "int"),
                        "param1", new Parameter(1, "desc 1", "float"))));
    }

    @Test
    public void testMarshallNull() throws Exception {
        MatcherAssert.assertThat(jsonAdapter.marshal(null), Matchers.nullValue());
    }

    @Test
    public void testMarshallJson() throws Exception {

        final ImmutableMap<String, Parameter> input = ImmutableMap.of("param0", new Parameter(0, "desc 0", "int"),
                "param1", new Parameter(1, "desc 1", "float"));

        final ObjectNode node = objectMapper.createObjectNode();
        node.set("param0", objectMapper.readTree("{\"value\":0,\"comment\":\"desc 0\",\"type\":\"int\"}"));
        node.set("param1", objectMapper.readTree("{\"value\":1,\"comment\":\"desc 1\",\"type\":\"float\"}"));

        MatcherAssert.assertThat(jsonAdapter.marshal(input), Matchers.is(node.toString()));
    }

}
