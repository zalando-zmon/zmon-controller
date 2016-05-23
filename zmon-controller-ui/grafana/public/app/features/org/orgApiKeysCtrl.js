define([
  'angular',
],
function (angular) {
  'use strict';

  var module = angular.module('grafana.controllers');

  module.controller('OrgApiKeysCtrl', function($scope, $http, backendSrv) {

    $scope.roleTypes = ['Viewer', 'Editor', 'Admin'];
    $scope.token = { role: 'Viewer' };

    $scope.init = function() {
      $scope.getTokens();
    };

    $scope.getTokens = function() {
      backendSrv.get('/api/auth/keys').then(function(tokens) {
        $scope.tokens = tokens;
      });
    };

    $scope.removeToken = function(id) {
      backendSrv.delete('/api/auth/keys/'+id).then($scope.getTokens);
    };

    $scope.addToken = function() {
      backendSrv.post('/api/auth/keys', $scope.token).then(function(result) {

        var modalScope = $scope.$new(true);
        modalScope.key = result.key;

        $scope.appEvent('show-modal', {
          src: 'public/app/features/org/partials/apikeyModal.html',
          scope: modalScope
        });

        $scope.getTokens();
      });
    };

    $scope.init();

  });
});
