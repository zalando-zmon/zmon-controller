package org.zalando.zmon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.config.ControllerProperties;
import org.zalando.zmon.domain.CheckResults;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.persistence.GrafanaDashboardSprocService;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.ZMonService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/rest/grafana")
public class GrafanaController extends AbstractZMonController {

    private static final Logger LOG = LoggerFactory.getLogger(GrafanaController.class);

    ZMonService zMonService;

    GrafanaDashboardSprocService grafanaService;

    DefaultZMonPermissionService authService;

    ObjectMapper mapper;

    ControllerProperties controllerProperties;

    @Autowired
    public GrafanaController(
            ZMonService zMonService,
            GrafanaDashboardSprocService grafanaService,
            DefaultZMonPermissionService authService,
            ObjectMapper mapper,
            ControllerProperties controllerProperties
    ) {
        this.zMonService = zMonService;
        this.grafanaService = grafanaService;
        this.authService = authService;
        this.mapper = mapper;
        this.controllerProperties = controllerProperties;
    }

    // home dashboard
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/dashboards/home", method = RequestMethod.GET)
    public JsonNode getDashboard2() throws IOException {
        return mapper.readTree(GrafanaController.class.getResourceAsStream("/grafana/home.json"));
    }

    // search for dashboards, returns list of all available dashboards
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/search", method = RequestMethod.GET)
    public JsonNode searchDashboards(@RequestParam(value = "query", required = false) String query, @RequestParam(value = "tag", required = false) List<String> tags, @RequestParam(value = "starred", defaultValue = "false") boolean starred) throws IOException, ZMonException {
        if (null == query) {
            query = "";
        }

        String starredBy = null;
        if (starred) {
            starredBy = authService.getUserName();
        }

        String jsonTags = null;
        if (tags != null && tags.size() > 0) {
            jsonTags = mapper.writeValueAsString(tags);
        }

        List<GrafanaDashboardSprocService.GrafanaDashboard> results = grafanaService.getGrafanaDashboards(query, jsonTags, starredBy, authService.getUserName());
        ArrayNode resultsNode = mapper.createArrayNode();

        for (GrafanaDashboardSprocService.GrafanaDashboard d : results) {
            ObjectNode dashboard = resultsNode.addObject();
            dashboard.put("uri", "db/" + d.id);
            dashboard.put("id", d.id);
            dashboard.put("type", "db-dash");
            dashboard.put("title", d.title);

            if (d.tags != null && !"".equals(d.tags) && !"[]".equals(d.tags)) {
                JsonNode tagsField = mapper.readTree(d.tags);
                dashboard.set("tags", tagsField);
            } else {
                dashboard.putArray("tags");
                if (d.grafanaVersion.equals("v1")) {
                    ArrayNode tagNode = ((ArrayNode) dashboard.get("tags"));
                    tagNode.add("v1");
                }
            }

            dashboard.put("isStarred", d.starred);
        }

        return resultsNode;
    }

    public static String getUnit(String unit) {
        if (unit.equals("minutes")) return "m";
        if (unit.equals("hours")) return "h";
        if (unit.equals("seconds")) return "s";
        return "m";
    }

    public static void migratePanel(ObjectNode panel) {
        panel.putNull("datasource");
        ArrayNode overrides = (ArrayNode) panel.get("seriesOverrides");
        if (null != overrides && overrides.size() > 0) {
            for (int i = 0; i < overrides.size(); ++i) {
                ObjectNode o = (ObjectNode) overrides.get(i);
                String id = o.get("alias").textValue();
                o.put("alias", id.replace("{", " ( ").replace("}", " ) "));
            }
        }
    }

    public static void migrateSampling(ObjectNode target) {
        if (target.has("sampling")) {
            ObjectNode sampling = (ObjectNode) target.get("sampling");
            String unit = sampling.get("unit").textValue();
            String value = sampling.get("value").textValue();

            target.put("sampling", value.replace("'", "") + getUnit(unit));

            ArrayNode h = target.putArray("horizontalAggregators");
            String aggregator = target.get("aggregator").textValue();

            ObjectNode entry = h.addObject();
            entry.put("name", aggregator);
            entry.put("sampling_rate", value.replace("'", "") + getUnit(unit));
        }
    }

    public static void migrateTarget(ObjectNode target) {// convert groups
        if (target.get("groups") != null && target.get("groups").size() > 0) {
            target.put("currentGroupByType", "tag");
            ArrayNode groupTags = (ArrayNode) target.get("groups");
            ArrayNode groupV2 = target.putArray("groupByTags");
            for (int l = 0; l < groupTags.size(); ++l) {
                groupV2.add(groupTags.get(l).textValue());
            }
        } else {
            target.remove("groups");
        }

        if (!target.has("downsampling")) {
            target.put("downsampling", "(NONE)");
        }

        if (null != target.get("tags") && target.get("tags") instanceof ArrayNode) {
            ArrayNode oldTags = (ArrayNode) target.get("tags");
            ObjectNode newTags = target.putObject("tags");

            // convert tag filter
            if (oldTags != null && oldTags.size() > 0) {
                for (int t = 0; t < oldTags.size(); ++t) {
                    ObjectNode tf = (ObjectNode) oldTags.get(t);
                    if (null == tf || !tf.has("key") || !tf.has("value")) {
                        continue;
                    }

                    String tk = tf.get("key").textValue();
                    String tv = tf.get("value").textValue();

                    if (newTags.has(tk)) {
                        ArrayNode vs = (ArrayNode) newTags.get(tk);
                        vs.add(tv);
                    } else {
                        ArrayNode vs = newTags.putArray(tk);
                        vs.add(tv);
                    }
                }
            }
        }
    }

    public static void migrateV1(ObjectNode dashboard) {
        LOG.info("Migrating dashboard to v2 properties");
        ArrayNode rows = (ArrayNode) dashboard.get("rows");
        if (null != rows) {
            for (int i = 0; i < rows.size(); ++i) {
                JsonNode row = rows.get(i);
                if (null != row && row.has("panels")) {
                    ArrayNode panels = (ArrayNode) row.get("panels");
                    if (null == panels) continue;

                    for (int j = 0; j < panels.size(); ++j) {
                        if (null == panels.get(j)) continue;
                        ObjectNode panel = (ObjectNode) panels.get(j);
                        migratePanel(panel);
                        ArrayNode targets = (ArrayNode) panel.get("targets");

                        for (int k = 0; null != targets && k < targets.size(); ++k) {
                            ObjectNode target = (ObjectNode) targets.get(k);
                            migrateTarget(target);
                            migrateSampling(target);
                        }
                    }
                }
            }
        }
    }

    public ResponseEntity<JsonNode> serveDynamicDashboard(String id) throws IOException {
        // zmon-check-123-inst
        String[] parts = id.split("-", 4);
        int checkId = Integer.valueOf(parts[2]);

        final Optional<String> entityId = parts.length > 3 ? Optional.of(parts[3]) : Optional.empty();

        List<CheckResults> checkResults = zMonService.getCheckResults(checkId, null, 1);
        String entityIds = checkResults.stream().map(CheckResults::getEntity).sorted()
                // replace chars for KairosDB
                .map(entity -> entity.replace("[", "_").replace("]", "_").replace(":", "_").replace("@", "_"))
                .collect(Collectors.joining(","));

        JsonNode node = mapper.readTree(GrafanaController.class.getResourceAsStream("/grafana/dynamic-dashboard.json"));
        ((ObjectNode) node.get("dashboard").get("rows").get(0).get("panels").get(0).get("targets").get(0)).put("metric", "zmon.check." + checkId);

        ((ObjectNode) node.get("dashboard").get("templating").get("list").get(0)).put("query", entityIds);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    // requests a dashboard
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/dashboards/db/{id}", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getDashboard(@PathVariable(value = "id") String id) throws ZMonException, IOException {
        if (null == id || "".equals(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id.startsWith("zmon-check-")) {
            return serveDynamicDashboard(id);
        }

        id = id.toLowerCase();

        List<GrafanaDashboardSprocService.GrafanaDashboard> dashboards = grafanaService.getGrafanaDashboard(id, authService.getUserName());
        if (dashboards.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        GrafanaDashboardSprocService.GrafanaDashboard dashboard = dashboards.get(0);

        ObjectNode result = mapper.createObjectNode();

        ObjectNode meta = result.putObject("meta");
        meta.put("type", "db");
        meta.put("canEdit", true);
        meta.put("canSave", true);
        meta.put("canStar", true);
        meta.put("created", "0001-01-01T00:00:00Z");
        meta.put("expires", "2999-01-01T00:00:00Z");
        meta.put("updated", "0001-01-01T00:00:00Z");
        meta.put("isHome", false);
        meta.put("slug", id);
        meta.put("isStarred", dashboard.starred);

        ObjectNode model = (ObjectNode) mapper.readTree(dashboard.dashboard);
        model.put("id", id);

        if (model.has("refresh") && model.get("refresh") != null) {
            String refresh = model.get("refresh").textValue();
            if (refresh != null && refresh.endsWith("s")) {
                int interval = Integer.parseInt(refresh.replace("s", ""));
                if (interval < controllerProperties.getGrafanaMinInterval()) {
                    model.put("refresh", controllerProperties.getGrafanaMinInterval() + "s");
                }
            }
        }

        if (dashboard.grafanaVersion.equals("v1")) {
            migrateV1(model);
        }

        result.set("dashboard", model);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/dashboards/db/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<JsonNode> deleteG2Dashboard(@PathVariable(value = "id") String id) {
        grafanaService.deleteGrafanaDashboard(id, authService.getUserName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // saves a dashboard
    @ResponseBody
    @RequestMapping(value = "/api/dashboards/db", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<JsonNode> saveDashboard(@RequestBody(required = true) final JsonNode grafanaData) throws IOException {
        /*
        if(authService.hasOnetimeTokenPrivilege()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
         */
        String title = grafanaData.get("dashboard").get("title").textValue();
        if (title == null || "".equals(title)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String dashboard = mapper.writeValueAsString(grafanaData.get("dashboard"));

        String id = grafanaData.get("dashboard").get("id").textValue();
        if (null == id || "".equals(id)) {
            id = title.replace(" ", "-").replace("'", "").toLowerCase();
        } else {
            id = id.toLowerCase();
        }

        LOG.info("Saving Grafana 2 dashboard title: \"{}\" id: {}", title, id);
        grafanaService.createOrUpdateGrafanaDashboard(id, title, dashboard, authService.getUserName(), "v2");

        ObjectNode result = mapper.createObjectNode();
        result.put("slug", id);
        result.put("status", "success");
        result.put("version", 0);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // returns a list of all available datasources
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/datasources", method = RequestMethod.GET)
    public JsonNode g2getDatasources() throws ZMonException {
        ArrayNode arr = mapper.createArrayNode();
        ObjectNode k = mapper.createObjectNode();
        k.put("id", 1);
        k.put("orgId", "1");
        k.put("name", "kairos");
        k.put("access", "direct");
        k.put("url", "/rest/kairosDBPost");
        k.put("password", "");
        k.put("user", "");
        k.put("database", "");
        k.put("basicAuth", false);
        k.put("basicAuthUser", "");
        k.put("basicAuthPassword", "");
        k.put("isDefault", true);
        k.put("type", "grafana-kairosdb-datasource");
        k.put("typeLogoUrl", "public/plugins/grafana-kairosdb-datasource/img/kairosdb_logo_small.png");
        arr.add(k);

        /*
        ObjectNode e = mapper.createObjectNode();
        e.put("id", 2);
        e.put("orgId", "1");
        e.put("name", "elasticsearch");
        e.put("access", "direct");
        e.put("url", "/rest/grafana/dashboard/_search");
        e.put("password", "");
        e.put("user", "");
        e.put("database", "");
        e.put("basicAuth", false);
        e.put("basicAuthUser", "");
        e.put("basicAuthPassword", "");
        e.put("isDefault", true);
        arr.add(e);
        */
        return arr;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/datasources/{id}", method = RequestMethod.GET)
    public JsonNode g2getDatasource(@PathVariable(value = "id") String id) throws ZMonException {
        ObjectNode k = mapper.createObjectNode();
        k.put("id", 1);
        k.put("orgId", "1");
        k.put("name", "kairos");
        k.put("access", "direct");
        k.put("url", "/rest/kairosDBPost");
        k.put("password", "");
        k.put("user", "");
        k.put("database", "");
        k.put("basicAuth", false);
        k.put("basicAuthUser", "");
        k.put("basicAuthPassword", "");
        k.put("isDefault", true);
        k.put("type", "grafana-kairosdb-datasource");
        k.put("typeLogoUrl", "public/plugins/grafana-kairosdb-datasource/img/kairosdb_logo_small.png");
        return k;
    }

    // returns a list of all available plugins
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/plugins", method = RequestMethod.GET)
    public JsonNode g2getPlugins() throws ZMonException {
        ArrayNode arr = mapper.createArrayNode();

        ObjectNode k = mapper.createObjectNode();
        k.put("defaultNavUrl", "plugins/grafana-kairosdb-datasource/edit");
        k.put("enabled", true);
        k.put("hasUpdate", false);
        k.put("id", "grafana-kairosdb-datasource");
        k.put("latestVersion", "1.0.1");
        k.put("name", "kairos");
        k.put("pinned", false);
        k.put("type", "datasource");
        arr.add(k);

        ObjectNode j = mapper.createObjectNode();
        j.put("defaultNavUrl", "/plugins/grafana-piechart-panel/edit");
        j.put("enabled", true);
        j.put("hasUpdate", false);
        j.put("id", "grafana-piechart-panel");
        j.put("latestVersion", "1.1.1");
        j.put("name", "Pie Chart");
        j.put("pinned", false);
        j.put("type", "panel");
        arr.add(j);

        return arr;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/plugins/grafana-kairosdb-datasource/settings", method = RequestMethod.GET)
    public JsonNode g2getPluginsKairosSettings() throws ZMonException {
        ObjectNode k = mapper.createObjectNode();
        k.put("baseUrl", "public/plugins/grafana-kairosdb-datasource");
        k.put("defaultNavUrl", "");
        k.put("enabled", true);
        k.put("hasUpdate", false);
        k.put("id", "grafana-kairosdb-datasource");
        k.put("module", "plugins/grafana-kairosdb-datasource/module");
        k.put("name", "kairos");
        k.put("pinned", false);
        k.put("type", "datasource");

        ObjectNode d = mapper.createObjectNode();
        ArrayNode emptyArr = mapper.createArrayNode();
        d.put("grafanaVersion", "3.x.x");
        d.put("plugins", emptyArr);
        k.put("dependencies", d);

        return k;
    }


    @RequestMapping(value = "/api/user/orgs", method = RequestMethod.GET)
    @ResponseBody
    public Collection<String> getUserOrgs() {
        return authService.getTeams();
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getCurrentUser() {
        ObjectNode node = mapper.createObjectNode();
        node.put("name", authService.getUserName());
        node.put("login", authService.getUserName());
        node.put("email", "");
        node.put("isGrafanaAdmin", false);
        node.put("isSignedIn", true);
        return node;
    }

    @RequestMapping(value = "/api/dashboards/tags")
    @ResponseBody
    public JsonNode getGrafanaTags() {
        ArrayNode result = mapper.createArrayNode();
        List<GrafanaDashboardSprocService.GrafanaTag> tags = grafanaService.getTagsWithCount();
        for (GrafanaDashboardSprocService.GrafanaTag t : tags) {
            ObjectNode node = result.addObject();
            node.put("term", t.tag);
            node.put("count", t.count);
        }
        return result;
    }

    @RequestMapping(value = "/api/user/stars/dashboard/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonNode starDashboard(@PathVariable String id) {
        grafanaService.starGrafanaDashboard(id, authService.getUserName());
        return mapper.createObjectNode();
    }

    @RequestMapping(value = "/api/user/stars/dashboard/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonNode unstarDashboard(@PathVariable String id) {
        grafanaService.unstarGrafanaDashboard(id, authService.getUserName());
        return mapper.createObjectNode();
    }
}
