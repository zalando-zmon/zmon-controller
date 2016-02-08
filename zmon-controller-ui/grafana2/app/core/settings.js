define([
  'lodash',
],
function (_) {
  "use strict";

  return function Settings (options) {
    var defaults = {
      datasources: [
        {
          "id":1,
          "orgId":2,
          "name":"KairosDB",
          "type":"kairosdb",
          "access":"direct",
          "url":"http://localhost:8443/rest/kairosDBPost",
          "password":"",
          "user":"",
          "database":"",
          "basicAuth":false,
          "basicAuthUser":"",
          "basicAuthPassword":"",
          "isDefault":true
        }
      ],
      window_title_prefix           : 'Grafana - ',
      panels                        : {
        'graph':      { path: 'app/panels/graph',      name: 'Graph' },
        'table':      { path: 'app/panels/table',      name: 'Table' },
        'singlestat': { path: 'app/panels/singlestat', name: 'Single stat' },
        'text':       { path: 'app/panels/text',       name: 'Text' },
        'dashlist':   { path: 'app/panels/dashlist',   name: 'Dashboard list' },
      },
      new_panel_title: 'Panel Title',
      plugins: {},
      playlist_timespan: "1m",
      unsaved_changes_warning: true,
      appSubUrl: ""
    };

    return _.extend({}, defaults, options);
  };
});
