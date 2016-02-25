package de.zalando.zmon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.zalando.zmon.exception.ZMonException;
import de.zalando.zmon.persistence.GrafanaDashboardSprocService;
import de.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(value = "/rest/grafana2")
public class Grafana2Controller extends AbstractZMonController {

    private static final Logger LOG = LoggerFactory.getLogger(Grafana2Controller.class);

    @Autowired
    GrafanaDashboardSprocService grafanaService;

    @Autowired
    DefaultZMonPermissionService authService;

    @Autowired
    ObjectMapper mapper;

    // home dashboard
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/dashboards/home", method = RequestMethod.GET)
    public JsonNode getDashboard2() throws ZMonException {
        ObjectNode node = mapper.createObjectNode();
        ObjectNode dashboard = mapper.createObjectNode();
        dashboard.put("editable", true);
        dashboard.put("hideControls", true);
        dashboard.put("id", 36);
        dashboard.put("oritinalTitle", "DashboardTest");
        dashboard.put("schemaVersion", 7);
        dashboard.put("sharedCrosshair", false);
        dashboard.put("timezone", "browser");
        dashboard.put("title", "Grafana Zmon");
        dashboard.put("version", 26);

        ObjectNode annotations = mapper.createObjectNode();
        ArrayNode list = mapper.createArrayNode();

        annotations.put("list", list);
        annotations.put("enable", false);

        ArrayNode rows = mapper.createArrayNode();
        dashboard.put("rows", rows);

        ArrayNode tags = mapper.createArrayNode();
        tags.add("startpage");
        tags.add("home");
        annotations.put("tags", tags);

        ObjectNode templating = mapper.createObjectNode();
        templating.put("enable", false);
        templating.put("list", list);
        annotations.put("templating", templating);

        ObjectNode time = mapper.createObjectNode();
        time.put("from", "now-2h");
        time.put("to", "now");
        annotations.put("time", time);

        dashboard.put("annotations", annotations);

        ObjectNode meta = mapper.createObjectNode();
        meta.put("canEdit", true);
        meta.put("canSave", false);
        meta.put("canStar", false);
        meta.put("created", "0001-01-01T00:00:00Z");
        meta.put("expires", "0001-01-01T00:00:00Z");
        meta.put("updated", "0001-01-01T00:00:00Z");
        meta.put("isHome", true);
        meta.put("slug", "");

        node.put("dashboard", dashboard);
        node.put("meta", meta);
        return node;
    }

    // search for dashboards, returns list of all available dashboards
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/search", method = RequestMethod.GET)
    public JsonNode searchDashboards(@RequestParam(value="query", required = false) String query, @RequestParam(value="tag", required = false) List<String> tags, @RequestParam(value="starred", defaultValue="false") boolean starred) throws IOException, ZMonException {
        if (null == query) {
            query = "";
        }

        String starredBy = null;
        if(starred) {
            starredBy = authService.getUserName();
        }

        String jsonTags = null;
        if(tags!=null && tags.size()>0) {
            jsonTags = mapper.writeValueAsString(tags);
        }
        LOG.info("Grafana2 search: query=\"{}\" starred={} by={} tags={}", query, starred, starredBy, jsonTags);

        List<GrafanaDashboardSprocService.GrafanaDashboard> results = grafanaService.getGrafanaDashboards(query, jsonTags, starredBy, authService.getUserName());
        ArrayNode resultsNode = mapper.createArrayNode();

        for (GrafanaDashboardSprocService.GrafanaDashboard d : results ) {
            LOG.info("Adding dashboard: {}", d);
            ObjectNode dashboard = resultsNode.addObject();
            dashboard.put("uri", "db/"+d.id);
            dashboard.put("id", d.id);
            dashboard.put("type", "db-dash");
            dashboard.put("title", d.title);

            if(d.tags != null && !"".equals(d.tags) && !"[]".equals(d.tags)) {
                JsonNode tagsField = mapper.readTree(d.tags);
                dashboard.set("tags", tagsField);
            }
            else {
                dashboard.putArray("tags");
                if(d.grafanaVersion.equals("v1")) {
                    ArrayNode tagNode = ((ArrayNode)dashboard.get("tags"));
                    tagNode.add("v1");
                }
            }

            dashboard.put("isStarred", d.starred);
        }

        return resultsNode;
    }

    public static String getUnit(String unit) {
        if(unit.equals("minutes")) return "m";
        if(unit.equals("hours")) return "h";
        return "m";
    }

    public static void migrateSampling(ObjectNode target) {
        if(target.has("sampling")) {
            ObjectNode sampling = (ObjectNode)target.get("sampling");
            String unit = sampling.get("unit").textValue();
            String value = sampling.get("value").textValue();

            target.put("sampling", value.replace("'","") + getUnit(unit));

            ArrayNode h = target.putArray("horizontalAggregators");
            String aggregator = target.get("aggregator").textValue();
            
            ObjectNode entry = h.addObject();
            entry.put("name", aggregator);
            entry.put("sampling_rate", value.replace("'", "") + getUnit(unit));
        }
    }

    public static void migrateTarget(ObjectNode target) {// convert groups
        if(target.get("groups")!=null && target.get("groups").size()>0) {
            target.put("currentGroupByType", "tag");
            ArrayNode groupTags = (ArrayNode)target.get("groups");
            ArrayNode groupV2 = target.putArray("groupByTags");
            for(int l = 0; l < groupTags.size(); ++l) {
                groupV2.add(groupTags.get(l).textValue());
            }
        }
        else {
            target.remove("groups");
        }

        ArrayNode oldTags = (ArrayNode)target.get("tags");
        ObjectNode newTags = target.putObject("tags");

        // convert tag filter
        if(oldTags!=null && oldTags.size()>0) {
            for(int t = 0; t < oldTags.size(); ++t) {
                ObjectNode tf = (ObjectNode)oldTags.get(t);
                String tk = tf.get("key").textValue();
                String tv = tf.get("value").textValue();
                if(newTags.has(tk)) {
                    ArrayNode vs = (ArrayNode)newTags.get(tk);
                    vs.add(tv);
                }
                else {
                    ArrayNode vs = newTags.putArray(tk);
                    vs.add(tv);
                }
            }
        }
    }

    public static void migrateV1(ObjectNode dashboard) {
        LOG.info("Migrating dashboard to v2 properties");
        ArrayNode rows = (ArrayNode) dashboard.get("rows");
        if (null!=rows) {
            for (int i = 0; i < rows.size(); ++i) {
                JsonNode row = rows.get(i);
                if (null!=row && row.has("panels")) {
                    ArrayNode panels = (ArrayNode) row.get("panels");
                    if (null == panels) continue;

                    for (int j = 0; j < panels.size(); ++j) {
                        if(null==panels.get(j)) continue;
                        ObjectNode panel = (ObjectNode) panels.get(j);
                        panel.putNull("datasource");
                        ArrayNode targets = (ArrayNode)panel.get("targets");

                        for(int k = 0; null!=targets && k < targets.size(); ++k) {
                            ObjectNode target = (ObjectNode)targets.get(k);
                            migrateTarget(target);
                            migrateSampling(target);
                        }
                    }
                }
            }
        }
    }

    // requests a dashboard
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/dashboards/db/{id}", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getDashboard(@PathVariable(value="id") String id) throws ZMonException, IOException {
        if(null == id || "".equals(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        id = id.toLowerCase();

        List<GrafanaDashboardSprocService.GrafanaDashboard> dashboards = grafanaService.getGrafanaDashboard(id, authService.getUserName());
        if(dashboards.size()==0) {
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

        ObjectNode model =  (ObjectNode) mapper.readTree(dashboard.dashboard);
        model.put("id", id);

        if(dashboard.grafanaVersion.equals("v1")) {
            migrateV1(model);
        }

        result.set("dashboard", model);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/api/dashboards/db/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<JsonNode> deleteG2Dashboard(@PathVariable(value="id") String id) {
        grafanaService.deleteGrafanaDashboard(id, authService.getUserName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // saves a dashboard
    @ResponseBody
    @RequestMapping(value = "/api/dashboards/db", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<JsonNode> saveDashboard(@RequestBody(required = true) final JsonNode grafanaData) throws IOException {

        String title = grafanaData.get("dashboard").get("title").textValue();
        if(title==null || "".equals(title)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String dashboard = mapper.writeValueAsString(grafanaData.get("dashboard"));

        String id = grafanaData.get("dashboard").get("id").textValue();
        if (null == id || "".equals(id)) {
            id = title.replace(" ", "-").replace("'","").toLowerCase();
        }
        else {
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
        k.put("orgId", "2");
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
        arr.add(k);

        ObjectNode e = mapper.createObjectNode();
        e.put("id", 2);
        e.put("orgId", "2");
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
        return arr;
    }

    @RequestMapping(value="/api/user/orgs", method = RequestMethod.GET)
    @ResponseBody
    public Collection<String> getUserOrgs() {
        return authService.getTeams();
    }

    @RequestMapping(value="/api/user", method = RequestMethod.GET)
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

    @RequestMapping(value="/api/dashboards/tags")
    @ResponseBody
    public JsonNode getGrafana2Tags() {
        ArrayNode result = mapper.createArrayNode();
        List<GrafanaDashboardSprocService.GrafanaTag> tags = grafanaService.getTagsWithCount();
        for(GrafanaDashboardSprocService.GrafanaTag t : tags) {
            ObjectNode node = result.addObject();
            node.put("term", t.tag);
            node.put("count", t.count);
        }
        return result;
    }

    @RequestMapping(value="/api/user/stars/dashboard/{id}", method=RequestMethod.POST)
    @ResponseBody
    public JsonNode starDashboard(@PathVariable String id) {
        grafanaService.starGrafanaDashboard(id, authService.getUserName());
        return mapper.createObjectNode();
    }

    @RequestMapping(value="/api/user/stars/dashboard/{id}", method=RequestMethod.DELETE)
    @ResponseBody
    public JsonNode unstarDashboard(@PathVariable String id) {
        grafanaService.unstarGrafanaDashboard(id, authService.getUserName());
        return mapper.createObjectNode();
    }
}
