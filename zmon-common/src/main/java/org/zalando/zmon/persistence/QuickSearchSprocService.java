package org.zalando.zmon.persistence;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

import java.util.List;

/**
 * Created by jmussler on 08.09.16.
 */

@SProcService
public interface QuickSearchSprocService {

    @SProcCall
    public List<QuickSearchResultItem> quickSearchAlerts(@SProcParam  String search, @SProcParam List<String> teams, @SProcParam int limit);

    @SProcCall
    public List<QuickSearchResultItem> quickSearchChecks(@SProcParam  String search, @SProcParam List<String> teams, @SProcParam int limit);

    @SProcCall
    public List<QuickSearchResultItem> quickSearchGrafanaDashboards(@SProcParam  String search, @SProcParam List<String> teams, @SProcParam int limit);

    @SProcCall
    public List<QuickSearchResultItem> quickSearchDashboards(@SProcParam  String search, @SProcParam List<String> teams, @SProcParam int limit);
}
