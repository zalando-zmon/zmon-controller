package org.zalando.zmon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Created by jmussler on 25.02.16.
 */
public class GrafanaMigrationTest {

    @Test
    public void testMigration() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode target = (ObjectNode) mapper.readTree((new ClassPathResource("/old_grafana_target.json").getInputStream()));
        GrafanaController.migrateTarget(target);
        assert (target.get("tags") instanceof ObjectNode);
    }

    @Test
    public void testMigrationRow() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode dashboard = (ObjectNode) mapper.readTree((new ClassPathResource("/old_grafana_row.json").getInputStream()));
        GrafanaController.migrateV1(dashboard);
        assert (dashboard.get("rows").get(0).get("panels").get(0).get("targets").get(0).get("groupByTags") instanceof ArrayNode);
    }
}
