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
        return {
            alias: alias,
            upValue: target.upValue,
            downValue: target.downValue,
            entities: target.entities
        };
      }
      else {
        return null;
      }
    }));

    // No valid targets, return the empty result to save a round trip.
    if (_.isEmpty(queries)) {
      var d = this.q.defer();
      d.resolve({ data: [] });
      return d.promise;
    }

    // NOTE: this datasource plugin only supports ONE query!
    // use the "Mixed" datasource to combine multiple queries

    plotParams = plotParams[0];
    var alertId = queries[0].alertId;

    var entitiesReq = {
      method: 'GET',
      // NOTE: this might return a potential big check result to the UI :-(
      url: '/rest/checkAlertResults?alert_id=' + alertId + '&limit=1'
    };
    var currentAlertState = {};
    var that = this;
    return this.backendSrv.datasourceRequest(entitiesReq).then(function(entitiesResult) {
        // initial query finds all matching entities
        _.each(entitiesResult.data, function(cr) {
            if (_.contains(cr.active_alert_ids, alertId)) {
                // this makes sure that we draw a line for this entity
                currentAlertState[cr.entity] = false;
            }
        });
        var alertDetailsReq = {
            method: 'GET',
            url: '/rest/alertDetails?alert_id=' + alertId
        };
        return that.backendSrv.datasourceRequest(alertDetailsReq);
    }).then(function(alertDetailsResult) {
        // second query finds all entities in alert state
        _.each(alertDetailsResult.data.entities, function(entity) {
            currentAlertState[entity.entity] = true;
        });

        var handleAlertStateQueryResponseAlias = _.partial(handleAlertStateQueryResponse, plotParams, start, end, currentAlertState);

        return that.performTimeSeriesQuery(queries[0], start, end)
            .then(handleAlertStateQueryResponseAlias, handleQueryError);
    });
  };

  AlertStateDatasource.prototype.performTimeSeriesQuery = function(query, start, end) {
    var options = {
        method: 'GET',
        url: '/rest/alertHistory?alert_definition_id=' + query.alertId + '&from=' + start.unix() + '&to=' + end.unix(),
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

  function handleAlertStateQueryResponse(plotParams, start, end, currentAlertState, results) {
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

    // add datapoints for all entities where no events were returned
    _.each(currentAlertState, function(val, entityId) {
      if (!series[entityId] && (!plotParams.entities || _.contains(plotParams.entities, entityId))) {
          series[entityId] = [[val ? upValue : downValue, end.unix() * 1000]];
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
      alertId: parseInt(target.alert)
    };
    if (!query.alertId) {
        return null;
    }

    return query;
  }

  return AlertStateDatasource;
});
