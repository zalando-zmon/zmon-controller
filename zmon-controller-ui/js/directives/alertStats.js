angular.module('zmon2App').directive('alertStats', ['$q', 'CommunicationService', function($q, CommunicationService) {
  return {
    restrict: 'E',
    templateUrl: 'templates/alertStats.html',
    scope: {
      alertId: '@',
    },
    link: function (scope, elem, attrs) {
      scope.message = null;
      scope.loaded = false;
      scope.messageStyle = {background: 'linear-gradient(144deg, #dbdbdb, #4c4c4c)'};

      var setFalsePositiveInfoMessage = function(alertStats) {
        scope.message = alertStats.message;
        scope.messageStyle = {background: 'lightblue'};
      };

      var setDafaultMessage = function() {
        scope.message = "No data available regarding this alert's false positives.";
        scope.messageStyle = {background: 'lightgrey'};
        stopLoading();
      };
      var stopLoading = function() {
        scope.loaded = true;
      };

      CommunicationService.getAlertStats(scope.alertId)
        .then(setFalsePositiveInfoMessage)
        .then(stopLoading)
        .catch(setDafaultMessage)
    }
  }
}]);
