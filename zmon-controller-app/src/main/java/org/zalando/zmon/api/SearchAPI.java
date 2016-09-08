package org.zalando.zmon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.zmon.persistence.QuickSearchResultItem;
import org.zalando.zmon.persistence.QuickSearchSprocService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jmussler on 08.09.16.
 */

@Controller
@RequestMapping("/api/v1/quick-search")
public class SearchAPI {

    @Autowired
    QuickSearchSprocService searchService;

    public static class QuickSearchResult extends HashMap<String, List<QuickSearchResultItem>> {
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<QuickSearchResult> search(@RequestParam(name = "query", defaultValue = "") String search, @RequestParam(required = false, name = "team") List<String> teams, @RequestParam(name = "limit", defaultValue = "25") int limit) {
        QuickSearchResult result = new QuickSearchResult();

        List<QuickSearchResultItem> alerts = searchService.quickSearchAlerts(search, teams, limit);
        result.put("alerts", alerts);

        List<QuickSearchResultItem> checks = searchService.quickSearchChecks(search, teams, limit);
        result.put("checks", checks);

        List<QuickSearchResultItem> dashboards = searchService.quickSearchDashboards(search, teams, limit);
        result.put("dashboards", dashboards);

        List<QuickSearchResultItem> grafanaDashboards = searchService.quickSearchGrafanaDashboards(search, teams, limit);
        result.put("grafana-dashboards", grafanaDashboards);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
