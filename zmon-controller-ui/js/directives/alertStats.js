angular.module('zmon2App').directive(   'alertStats', ['$q', 'CommunicationService', function($q, CommunicationService) {
  return {
    restrict: 'E',
    templateUrl: 'templates/alertStats.html',
    scope: {
      alertId: '@',
    },
    link: function (scope) {
      scope.message = null;
      scope.loaded = false;
      scope.title = null;
      scope.messageStyle = {background: 'linear-gradient(144deg, #dbdbdb, #4c4c4c)'};
      const infoTitle = 'Notifications marked as "not an incident" in Jira on average over the last 2 weeks';

      const setFalsePositiveInfoMessage = (alertStats) => {
        scope.message = alertStats.message;
        scope.title = infoTitle;
        scope.messageStyle = {background: 'lightblue'};
      };

      const setDafaultMessage = () => {
        scope.message = "No data available regarding this alert's false positives.";
        scope.messageStyle = {background: 'lightgrey'};
      };

      const stopLoading = () => {
        scope.loaded = true;
      };

      CommunicationService.getAlertStats(scope.alertId)
        .then(setFalsePositiveInfoMessage)
        .catch(setDafaultMessage)
        .then(stopLoading)
    }
  }
}]);
