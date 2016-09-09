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
                    var teams = scope.filterByTeam ? UserInfoService.get().teams : null;
                    CommunicationService.search(query, teams).then(function(response) {
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
