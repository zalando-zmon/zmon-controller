define([
  'angular',
  'lodash',
  'app/plugins/sdk',
  'app/core/utils/datemath',
  'app/core/utils/kbn',
  './query_ctrl'
],
function (angular, _, sdk, dateMath, kbn) {
  'use strict';

  var self;

  function AlertStateDatasource(instanceSettings, $q, backendSrv, templateSrv) {
    this.type = instanceSettings.type;
    this.url = instanceSettings.url;
    this.name = instanceSettings.name;
    this.supportMetrics = true;
    this.q = $q;
    this.backendSrv = backendSrv;
    this.templateSrv = templateSrv;

    self = this;
  }

  // Called once per panel (graph)
  AlertStateDatasource.prototype.query = function(options) {
    var start = options.range.from;
    var end = options.range.to;

    var queries = _.compact(_.map(options.targets, _.partial(convertTargetToQuery, options)));
    var plotParams = _.compact(_.map(options.targets, function(target) {
      var alias = self.templateSrv.replace(target.alias);
      if (typeof target.alias === 'undefined' || target.alias === "") {
        alias = "zmon.alert." + target.alert;
      }

      if (!target.hide) {
        return { alias: alias, upValue: target.upValue, downValue: target.downValue,
        entities: target.entities};
      }
      else {
        return null;
      }
    }));
    plotParams = plotParams[0];

    var handleAlertStateQueryResponseAlias = _.partial(handleAlertStateQueryResponse, plotParams, start, end);

    // No valid targets, return the empty result to save a round trip.
    if (_.isEmpty(queries)) {
      var d = this.q.defer();
      d.resolve({ data: [] });
      return d.promise;
    }

    return this.performTimeSeriesQuery(queries, start, end)
      .then(handleAlertStateQueryResponseAlias, handleQueryError);
  };

  AlertStateDatasource.prototype.performTimeSeriesQuery = function(queries, start, end) {
    var options = {
      method: 'GET',
      url: '/rest/alertHistory?alert_definition_id=' + queries[0].alertId + '&from=' + start.unix() + '&to=' + end.unix(),
    };

    return this.backendSrv.datasourceRequest(options);
  };

  /////////////////////////////////////////////////////////////////////////
  /// Formatting methods
  ////////////////////////////////////////////////////////////////////////

  function handleQueryError(results) {
    if (results.data.errors && !_.isEmpty(results.data.errors)) {
      var errors = {
        message: results.data.errors[0]
      };
      return self.q.reject(errors);
    }
    else {
      return self.q.reject(results);
    }
  }

  function handleAlertStateQueryResponse(plotParams, start, end, results) {
    var output = [];
    var series = {};
    var upValue = plotParams.upValue || 1;
    var downValue = plotParams.downValue || 0;
    var data = _.sortBy(results.data, function(event) { return event.time; });
    _.each(data, function(event, i) {
      if (event.type_name == 'ALERT_ENTITY_STARTED' || event.type_name == 'ALERT_ENTITY_ENDED') {
        var entityId = event.attributes.entity;

        if (!plotParams.entities || _.contains(plotParams.entities, entityId)) {

        if (typeof series[entityId] === 'undefined') {
            series[entityId] = [];
        }

        if (event.type_name == 'ALERT_ENTITY_STARTED') {
            series[entityId].push([downValue, event.time * 1000]);
            series[entityId].push([upValue, (event.time * 1000) + 1]);
        } else {
            series[entityId].push([upValue, event.time * 1000]);
            series[entityId].push([downValue, (event.time * 1000) + 1]);
        }
        }
      }
    });

    _.each(series, function(datapoints, entityId) {
      datapoints.unshift([datapoints[0][0], start.unix() * 1000]);
      datapoints.push([_.last(datapoints)[0], end.unix() * 1000]);
      var label = plotParams.alias + ' ( entity=' + entityId + ' )';
      output.push({ target: label, datapoints: datapoints});
    });

    return { data: _.flatten(output) };
  }

  function convertTargetToQuery(options, target) {
    if (!target.alert || target.hide) {
      return null;
    }

    var query = {
      alertId: target.alert
    };

    return query;
  }

  return AlertStateDatasource;
});
