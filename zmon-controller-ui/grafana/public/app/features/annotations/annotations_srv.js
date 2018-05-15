define([
  'angular',
  'lodash',
  './editor_ctrl',
], function (angular, _) {
  'use strict';

  var module = angular.module('grafana.services');

  module.service('annotationsSrv', function($rootScope, $q, datasourceSrv, alertSrv, timeSrv) {
    var promiseCached;
    var list = [];
    var self = this;

    this.init = function() {
      $rootScope.onAppEvent('refresh', this.clearCache, $rootScope);
      $rootScope.onAppEvent('dashboard-initialized', this.clearCache, $rootScope);
    };

    this.clearCache = function() {
      promiseCached = null;
      list = [];
    };

    this.getAnnotations = function(dashboard) {
      if (dashboard.annotations.list.length === 0) {
        return $q.when(null);
      }

      if (promiseCached) {
        return promiseCached;
      }

      self.dashboard = dashboard;
      var annotations = _.where(dashboard.annotations.list, {enable: true});

      var range = timeSrv.timeRange();
      var rangeRaw = timeSrv.timeRange(false);

      var promises  = _.map(annotations, function(annotation) {
        if (annotation.snapshotData) {
          self.receiveAnnotationResults(annotation.snapshotData);
          return;
        }
        return datasourceSrv.get(annotation.datasource).then(function(datasource) {
          var query = {range: range, rangeRaw: rangeRaw, annotation: annotation};
          return datasource.annotationQuery(query)
            .then(self.receiveAnnotationResults)
            .then(function(results) {
              if (dashboard.snapshot) {
                annotation.snapshotData = angular.copy(results);
              }
            })
            .then(null, errorHandler);
        }, this);
      });

      promiseCached = $q.all(promises).then(function() {
        return list;
      }).catch(function(err) {
        $rootScope.appEvent('alert-error', ['Annotations failed', (err.message || err)]);
      });

      return promiseCached;
    };

    this.receiveAnnotationResults = function(results) {
      for (var i = 0; i < results.length; i++) {
        self.addAnnotation(results[i]);
      }

      return results;
    };

    this.addAnnotation = function(options) {
      list.push({
        annotation: options.annotation,
        min: options.time,
        max: options.time,
        eventType: options.annotation.name,
        title: options.title,
        tags: options.tags,
        text: options.text,
        score: 1
      });
    };

    function errorHandler(err) {
      console.log('Annotation error: ', err);
      var message = err.message || "Annotation query failed";
      alertSrv.set('Annotations error', message,'error');
    }

    // Now init
    this.init();
  });

});
