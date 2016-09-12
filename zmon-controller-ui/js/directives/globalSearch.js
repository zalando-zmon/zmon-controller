angular.module('zmon2App').directive('globalSearch', [ '$timeout', 'CommunicationService', 'UserInfoService', function($timeout, CommunicationService, UserInfoService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/globalSearch.html',
        scope: {
            visible: '='
        },
        link: function(scope, elem, attrs) {
            scope.data = [];
            scope.teams = UserInfoService.get().teams;
            scope.filterByTeam = !!scope.teams.length;      // init as enabled only if user has teams

            scope.$watch('[query, filterByTeam]', function() {
                if (scope.query) {
                    CommunicationService.search(scope.query, scope.teams).then(function(response) {
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
