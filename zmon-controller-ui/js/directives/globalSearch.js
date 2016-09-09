angular.module('zmon2App').directive('globalSearch', [ '$timeout', 'CommunicationService', 'UserInfoService', function($timeout, CommunicationService, UserInfoService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/globalSearch.html',
        scope: {
            visible: '='
        },
        link: function(scope, elem, attrs) {
            scope.data = [];
            scope.filterByTeam = true;

            scope.$watch('query', function(query) {
                if (query) {
                    var team = scope.filterByTeam ? UserInfoService.get().teams.split(',')[0] : null;
                    CommunicationService.search(query, team).then(function(response) {
                        scope.data = response;
                    });
                }
            });

            // focus input field on open
            scope.$watch('visible', function(v) {
                if (v === true) {
                    $timeout(function() {
                        elem.find('input').focus();
                    });
                }
            });
        }
    };
}]);
