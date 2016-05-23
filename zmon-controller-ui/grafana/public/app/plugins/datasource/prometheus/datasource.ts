///<reference path="../../../headers/common.d.ts" />

import angular from 'angular';
import _ from 'lodash';
import moment from 'moment';

import * as dateMath from 'app/core/utils/datemath';
import PrometheusMetricFindQuery from './metric_find_query';

var durationSplitRegexp = /(\d+)(ms|s|m|h|d|w|M|y)/;

/** @ngInject */
export function PrometheusDatasource(instanceSettings, $q, backendSrv, templateSrv) {
  this.type = 'prometheus';
  this.editorSrc = 'app/features/prometheus/partials/query.editor.html';
  this.name = instanceSettings.name;
  this.supportMetrics = true;
  this.url = instanceSettings.url;
  this.directUrl = instanceSettings.directUrl;
  this.basicAuth = instanceSettings.basicAuth;
  this.withCredentials = instanceSettings.withCredentials;
  this.lastErrors = {};

  this._request = function(method, url) {
    var options: any = {
      url: this.url + url,
      method: method
    };

    if (this.basicAuth || this.withCredentials) {
      options.withCredentials = true;
    }
    if (this.basicAuth) {
      options.headers = {
        "Authorization": this.basicAuth
      };
    }

    return backendSrv.datasourceRequest(options);
  };

  function regexEscape(value) {
    return value.replace(/[\\^$*+?.()|[\]{}]/g, '\\\\$&');
  }

  function interpolateQueryExpr(value, variable, defaultFormatFn) {
    // if no multi or include all do not regexEscape
    if (!variable.multi && !variable.includeAll) {
      return value;
    }

    if (typeof value === 'string') {
      return regexEscape(value);
    }

    var escapedValues = _.map(value, regexEscape);
    return escapedValues.join('|');
  };

  // Called once per panel (graph)
  this.query = function(options) {
    var start = getPrometheusTime(options.range.from, false);
    var end = getPrometheusTime(options.range.to, true);

    var queries = [];
    var activeTargets = [];

    options = _.clone(options);
    _.each(options.targets, _.bind(function(target) {
      if (!target.expr || target.hide) {
        return;
      }
      activeTargets.push(target);

      var query: any = {};
      query.expr = templateSrv.replace(target.expr, options.scopedVars, interpolateQueryExpr);

      var interval = target.interval || options.interval;
      var intervalFactor = target.intervalFactor || 1;
      target.step = query.step = this.calculateInterval(interval, intervalFactor);
      var range = Math.ceil(end - start);
      // Prometheus drop query if range/step > 11000
      // calibrate step if it is too big
      if (query.step !== 0 && range / query.step > 11000) {
        target.step = query.step = Math.ceil(range / 11000);
      }

      queries.push(query);
    }, this));

    // No valid targets, return the empty result to save a round trip.
    if (_.isEmpty(queries)) {
      var d = $q.defer();
      d.resolve({ data: [] });
      return d.promise;
    }

    var allQueryPromise = _.map(queries, _.bind(function(query) {
      return this.performTimeSeriesQuery(query, start, end);
    }, this));

    var self = this;
    return $q.all(allQueryPromise)
    .then(function(allResponse) {
      var result = [];

      _.each(allResponse, function(response, index) {
        if (response.status === 'error') {
          self.lastErrors.query = response.error;
          throw response.error;
        }
        delete self.lastErrors.query;

        _.each(response.data.data.result, function(metricData) {
          result.push(self.transformMetricData(metricData, activeTargets[index], start, end));
        });
      });

      return { data: result };
    });
  };

  this.performTimeSeriesQuery = function(query, start, end) {
    var url = '/api/v1/query_range?query=' + encodeURIComponent(query.expr) + '&start=' + start + '&end=' + end + '&step=' + query.step;
    return this._request('GET', url);
  };

  this.performSuggestQuery = function(query) {
    var url = '/api/v1/label/__name__/values';

    return this._request('GET', url).then(function(result) {
      return _.filter(result.data.data, function (metricName) {
        return metricName.indexOf(query) !== 1;
      });
    });
  };

  this.metricFindQuery = function(query) {
    if (!query) { return $q.when([]); }

    var interpolated;
    try {
      interpolated = templateSrv.replace(query);
    } catch (err) {
      return $q.reject(err);
    }

    var metricFindQuery = new PrometheusMetricFindQuery(this, interpolated);
    return metricFindQuery.process();
  };

  this.annotationQuery = function(options) {
    var annotation = options.annotation;
    var expr = annotation.expr || '';
    var tagKeys = annotation.tagKeys || '';
    var titleFormat = annotation.titleFormat || '';
    var textFormat = annotation.textFormat || '';

    if (!expr) { return $q.when([]); }

    var interpolated;
    try {
      interpolated = templateSrv.replace(expr, {}, interpolateQueryExpr);
    } catch (err) {
      return $q.reject(err);
    }

    var query = {
      expr: interpolated,
      step: '60s'
    };
    var start = getPrometheusTime(options.range.from, false);
    var end = getPrometheusTime(options.range.to, true);
    var self = this;
    return this.performTimeSeriesQuery(query, start, end).then(function(results) {
      var eventList = [];
      tagKeys = tagKeys.split(',');

      _.each(results.data.data.result, function(series) {
        var tags = _.chain(series.metric)
        .filter(function(v, k) {
          return _.contains(tagKeys, k);
        }).value();

        _.each(series.values, function(value) {
          if (value[1] === '1') {
            var event = {
              annotation: annotation,
              time: Math.floor(value[0]) * 1000,
              title: self.renderTemplate(titleFormat, series.metric),
              tags: tags,
              text: self.renderTemplate(textFormat, series.metric)
            };

            eventList.push(event);
          }
        });
      });

      return eventList;
    });
  };

  this.testDatasource = function() {
    return this.metricFindQuery('metrics(.*)').then(function() {
      return { status: 'success', message: 'Data source is working', title: 'Success' };
    });
  };

  this.calculateInterval = function(interval, intervalFactor) {
    var m = interval.match(durationSplitRegexp);
    var dur = moment.duration(parseInt(m[1]), m[2]);
    var sec = dur.asSeconds();
    if (sec < 1) {
      sec = 1;
    }

    return Math.ceil(sec * intervalFactor);
  };

  this.transformMetricData = function(md, options, start, end) {
    var dps = [],
      metricLabel = null;

    metricLabel = this.createMetricLabel(md.metric, options);

    var stepMs = parseInt(options.step) * 1000;
    var baseTimestamp = start * 1000;
    _.each(md.values, function(value) {
      var dp_value = parseFloat(value[1]);
      if (_.isNaN(dp_value)) {
        dp_value = null;
      }

      var timestamp = value[0] * 1000;
      for (var t = baseTimestamp; t < timestamp; t += stepMs) {
        dps.push([null, t]);
      }
      baseTimestamp = timestamp + stepMs;
      dps.push([dp_value, timestamp]);
    });

    var endTimestamp = end * 1000;
    for (var t = baseTimestamp; t <= endTimestamp; t += stepMs) {
      dps.push([null, t]);
    }

    return { target: metricLabel, datapoints: dps };
  };

  this.createMetricLabel = function(labelData, options) {
    if (_.isUndefined(options) || _.isEmpty(options.legendFormat)) {
      return this.getOriginalMetricName(labelData);
    }

    return this.renderTemplate(options.legendFormat, labelData) || '{}';
  };

  this.renderTemplate = function(format, data) {
    var originalSettings = _.templateSettings;
    _.templateSettings = {
      interpolate: /\{\{(.+?)\}\}/g
    };

    var template = _.template(templateSrv.replace(format));
    var result;
    try {
      result = template(data);
    } catch (e) {
      result = null;
    }

    _.templateSettings = originalSettings;

    return result;
  };

  this.getOriginalMetricName = function(labelData) {
    var metricName = labelData.__name__ || '';
    delete labelData.__name__;
    var labelPart = _.map(_.pairs(labelData), function(label) {
      return label[0] + '="' + label[1] + '"';
    }).join(',');
    return metricName + '{' + labelPart + '}';
  };

  function getPrometheusTime(date, roundUp): number {
    if (_.isString(date)) {
      date = dateMath.parse(date, roundUp);
    }
    return Math.ceil(date.valueOf() / 1000);
  }
}
