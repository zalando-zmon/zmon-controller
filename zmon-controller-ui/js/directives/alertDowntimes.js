angular.module('zmon2App').directive('alertDowntimes', [ 'CommunicationService', 'FeedbackMessageService', 'UserInfoService', 'APP_CONST', function(CommunicationService, FeedbackMessageService, UserInfoService, APP_CONST) {
    return {
        restrict: 'E',
        templateUrl: 'templates/alertDowntimes.html',
        scope: {
            alertId: '=',
            downtimes: '='
        },
        link: function(scope, elem, attrs) {
            scope.limit = 100;
            scope.downtimesSelected = [];
            scope.userInfo = UserInfoService.get();

            scope.incLimit = function() {
                scope.limit += 35;
            }

            scope.areAllDowntimesSelected = function() {
                return scope.downtimes.length && scope.downtimesSelected.length === scope.downtimes.length;
            };

            scope.toggleAllDowntimes = function() {
                scope.downtimesSelected = scope.downtimesSelected.length ? [] : _.map(scope.downtimes, 'id');
            };

            scope.toggleSingleDeleteDowntime = function(id) {
                var i = scope.downtimesSelected.indexOf(id);
                return i === -1 ? scope.downtimesSelected.push(id) : scope.downtimesSelected.splice(i, 1);
            };

            scope.deleteDowntimes = function() {
                CommunicationService.deleteDowntime(scope.downtimesSelected).then(function(response) {
                    FeedbackMessageService.showSuccessMessage('Downtimes deleted', 3000);
                    scope.downtimesSelected = [];
                });
            };
        }
    };
}]);
