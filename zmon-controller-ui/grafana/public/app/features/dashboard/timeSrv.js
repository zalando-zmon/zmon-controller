define([
  'angular',
  'lodash',
  'moment',
  'app/core/config',
  'app/core/utils/kbn',
  'app/core/utils/datemath'
], function (angular, _, moment, config, kbn, dateMath) {
  'use strict';

  var module = angular.module('grafana.services');

  module.service('timeSrv', function($rootScope, $timeout, $routeParams, timer) {
    var self = this;

    this.init = function(dashboard) {
      timer.cancel_all();

      this.dashboard = dashboard;
      this.time = dashboard.time;

      this._initTimeFromUrl();
      this._parseTime();

      if(this.dashboard.refresh) {
        this.setAutoRefresh(this.dashboard.refresh);
      }
    };

    this._parseTime = function() {
      // when absolute time is saved in json it is turned to a string
      if (_.isString(this.time.from) && this.time.from.indexOf('Z') >= 0) {
        this.time.from = moment(this.time.from).utc();
      }
      if (_.isString(this.time.to) && this.time.to.indexOf('Z') >= 0) {
        this.time.to = moment(this.time.to).utc();
      }
    };

    this._parseUrlParam = function(value) {
      if (value.indexOf('now') !== -1) {
        return value;
      }
      if (value.length === 8) {
        return moment.utc(value, 'YYYYMMDD');
      }
      if (value.length === 15) {
        return moment.utc(value, 'YYYYMMDDTHHmmss');
      }

      if (!isNaN(value)) {
        var epoch = parseInt(value);
        return moment(epoch);
      }

      return null;
    };

    this._initTimeFromUrl = function() {
      if ($routeParams.from) {
        this.time.from = this._parseUrlParam($routeParams.from) || this.time.from;
      }
      if ($routeParams.to) {
        this.time.to = this._parseUrlParam($routeParams.to) || this.time.to;
      }
    };

    this.setAutoRefresh = function (interval) {
      this.dashboard.refresh = interval;
      if (interval) {
        var _i = kbn.interval_to_ms(interval);
        var wait_ms = _i - (Date.now() % _i);
        $timeout(function () {
          self.start_scheduled_refresh(_i);
          self.refreshDashboard();
        }, wait_ms);
      } else {
        this.cancel_scheduled_refresh();
      }
    };

    this.refreshDashboard = function() {
      $rootScope.$broadcast('refresh');
    };

    this.start_scheduled_refresh = function (after_ms) {
      self.cancel_scheduled_refresh();
      self.refresh_timer = timer.register($timeout(function () {
        self.start_scheduled_refresh(after_ms);
        self.refreshDashboard();
      }, after_ms));
    };

    this.cancel_scheduled_refresh = function () {
      timer.cancel(this.refresh_timer);
    };

    this.setTime = function(time, enableRefresh) {
      _.extend(this.time, time);

      // disable refresh if zoom in or zoom out
      if (!enableRefresh && moment.isMoment(time.to)) {
        this.old_refresh = this.dashboard.refresh || this.old_refresh;
        this.setAutoRefresh(false);
      }
      else if (this.old_refresh && this.old_refresh !== this.dashboard.refresh) {
        this.setAutoRefresh(this.old_refresh);
        this.old_refresh = null;
      }

      $rootScope.appEvent('time-range-changed', this.time);
      $timeout(this.refreshDashboard, 0);
    };

    this.timeRangeForUrl = function() {
      var range = this.timeRange(false);
      if (_.isString(range.to) && range.to.indexOf('now')) {
        range = this.timeRange();
      }

      if (moment.isMoment(range.from)) { range.from = range.from.valueOf(); }
      if (moment.isMoment(range.to)) { range.to = range.to.valueOf(); }

      return range;
    };

    this.timeRange = function(parse) {
      // make copies if they are moment  (do not want to return out internal moment, because they are mutable!)
      var from = moment.isMoment(this.time.from) ? moment(this.time.from) : this.time.from ;
      var to = moment.isMoment(this.time.to) ? moment(this.time.to) : this.time.to ;

      if (parse !== false) {
        from = dateMath.parse(from, false);
        to = dateMath.parse(to, true);
      }

      return {from: from, to: to};
    };

  });

});
