define([
  'angular',
  'lodash',
  '../core_module',
  'app/core/config',
],
function (angular, _, coreModule, config) {
  'use strict';

  coreModule.service('backendSrv', function($http, alertSrv, $timeout) {
    var self = this;

    this.get = function(url, params) {
      return this.request({ method: 'GET', url: '/rest/grafana2' + url, params: params });
    };

    this.delete = function(url) {
      return this.request({ method: 'DELETE', url: '/rest/grafana2' + url });
    };

    this.post = function(url, data) {
      return this.request({ method: 'POST', url: '/rest/grafana2' + url, data: data });
    };

    this.patch = function(url, data) {
      return this.request({ method: 'PATCH', url: '/rest/grafana2' + url, data: data });
    };

    this.put = function(url, data) {
      return this.request({ method: 'PUT', url: '/rest/grafana2' + url, data: data });
    };

    this._handleError = function(err) {
      return function() {
        if (err.isHandled) {
          return;
        }

        var data = err.data || { message: 'Unexpected error' };
        if (_.isString(data)) {
          data = { message: data };
        }

        if (err.status === 422) {
          alertSrv.set("Validation failed", data.message, "warning", 4000);
          throw data;
        }

        data.severity = 'error';

        if (err.status < 500) {
          data.severity = "warning";
        }

        if (data.message) {
          alertSrv.set("Problem!", data.message, data.severity, 10000);
        }

        throw data;
      };
    };

    this.request = function(options) {
      options.retry = options.retry || 0;
      var requestIsLocal = options.url.indexOf('/') === 0;
      var firstAttempt = options.retry === 0;

      if (requestIsLocal && !options.hasSubUrl) {
        options.url = config.appSubUrl + options.url;
        options.hasSubUrl = true;
      }

      return $http(options).then(function(results) {
        if (options.method !== 'GET') {
          if (results && results.data.message) {
            alertSrv.set(results.data.message, '', 'success', 3000);
          }
        }
        return results.data;
      }, function(err) {
        // handle unauthorized
        if (err.status === 401 && firstAttempt) {
          // reload page on 401. Backend will redirect to auth dance and back.
          return location.reload();

          // No need to do this anymore since we are not using grafana2's backend
          /*
          return self.loginPing().then(function() {
            options.retry = 1;
            return self.request(options);
          });
          */
        }

        $timeout(self._handleError(err), 50);
        throw err;
      });
    };

    this.datasourceRequest = function(options) {
      options.retry = options.retry || 0;
      var requestIsLocal = options.url.indexOf('/') === 0;
      var firstAttempt = options.retry === 0;

      return $http(options).then(null, function(err) {
        // handle unauthorized for backend requests
        if (requestIsLocal && firstAttempt  && err.status === 401) {
          // reload page on 401. Backend will redirect to auth dance and back.
          return location.reload();

          // No need to do this anymore since we are not using grafana2's backend
          /*
          return self.loginPing().then(function() {
            options.retry = 1;
            return self.datasourceRequest(options);
          });
          */
        }

        // for Prometheus
        if (!err.data.message && _.isString(err.data.error)) {
          err.data.message = err.data.error;
        }

        throw err;
      });
    };

    this.loginPing = function() {
      return this.request({url: '/api/login/ping', method: 'GET', retry: 1 });
    };

    this.search = function(query) {
      return this.get('/api/search', query);
    };

    this.getDashboard = function(type, slug) {
      return this.get('/api/dashboards/' + type + '/' + slug);
    };

    this.saveDashboard = function(dash, options) {
      options = (options || {});
      return this.post('/api/dashboards/db/', {dashboard: dash, overwrite: options.overwrite === true});
    };

  });
});
