define([
  './datasource',
  './query_ctrl'
],
function(AlertStateDatasource, AlertStateQueryCtrl) {
  'use strict';

  var AlertStateConfigCtrl = function() {}
  AlertStateConfigCtrl.templateUrl = "partials/config.html";

  var AlertStateQueryOptionsCtrl = function() {}
  AlertStateQueryOptionsCtrl.templateUrl = "partials/query.options.html";

  return {
    'Datasource': AlertStateDatasource,
    'QueryCtrl': AlertStateQueryCtrl,
    'ConfigCtrl': AlertStateConfigCtrl,
    'QueryOptionsCtrl': AlertStateQueryOptionsCtrl
  };
});