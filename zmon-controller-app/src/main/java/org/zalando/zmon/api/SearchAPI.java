package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.zmon.config.VisualizationProperties;
import org.zalando.zmon.persistence.QuickSearchResultItem;
import org.zalando.zmon.persistence.QuickSearchSprocService;
import org.zalando.zmon.service.VisualizationService;
import org.zalando.zmon.service.impl.Grafana;

import java.util.*;

/**
 * Created by jmussler on 08.09.16.
 */

@Controller
@RequestMapping("/api/v1/quick-search")
public class SearchAPI {

    @Autowired
    QuickSearchSprocService searchService;

    @Autowired
    private VisualizationService visualizationService;

    @Autowired
    private VisualizationProperties visualizationProperties;

    private final Logger log = LoggerFactory.getLogger(Grafana.class);

    public static class QuickSearchResult extends HashMap<String, List<QuickSearchResultItem>> {
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<QuickSearchResult> search(
            @RequestParam(name = "query", defaultValue = "") final String search,
            @RequestParam(required = false, name = "teams") final String teams,
            @RequestParam(name = "limit", defaultValue = "25") int limit,
            @RequestHeader("Authorization") final String authHeader) {
        QuickSearchResult result = new QuickSearchResult();

        // null has special meaning in sproc -> "do not filter by team"
        List<String> teamList = null;
        if (null != teams && !"".equals(teams) && !"null".equals(teams)) {
            teamList = Arrays.asList(teams.split(","));
        }

        List<QuickSearchResultItem> alerts = searchService.quickSearchAlerts(search, teamList, limit);
        result.put("alerts", alerts);

        List<QuickSearchResultItem> checks = searchService.quickSearchChecks(search, teamList, limit);
        result.put("checks", checks);

        List<QuickSearchResultItem> dashboards = searchService.quickSearchDashboards(search, teamList, limit);
        result.put("dashboards", dashboards);

        result.put("grafana_dashboards", searchVisualizationDashboards(search, teams, limit, authHeader != null ? authHeader.split(" ")[1] : ""));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private List<QuickSearchResultItem> searchVisualizationDashboards(String search, String teams, int limit, String token) {
        List<QuickSearchResultItem> dashboards = new ArrayList<>();

        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("query", search);
        teams = teams != null ? teams : "";
        searchParams.put("tag", teams);
        searchParams.put("limit", String.valueOf(limit));

        ResponseEntity<JsonNode> responseEntity = visualizationService.searchDashboards(searchParams, token);

        if (responseEntity.getStatusCodeValue() == 200 && responseEntity.getBody().isArray()) {
            for (final JsonNode dashboardNode : responseEntity.getBody()) {
                QuickSearchResultItem i = new QuickSearchResultItem();
                i.setId(dashboardNode.hasNonNull("uid") ? dashboardNode.get("uid").textValue() : "");
                i.setTitle(dashboardNode.hasNonNull("title") ? dashboardNode.get("title").textValue() : "");
                i.setUrl(dashboardNode.hasNonNull("url") ?
                        visualizationProperties.getUrl() + dashboardNode.get("url").textValue() : "");
                dashboards.add(i);
            }
        } else {
            log.error("Failed to search dashboard in visualization provider. Status: {}", responseEntity.getStatusCodeValue());
        }
        return dashboards;
    }
}
