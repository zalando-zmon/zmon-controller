angular.module('zmon2App').directive('globalSearch', [ '$timeout', 'CommunicationService', 'UserInfoService', function($timeout, CommunicationService, UserInfoService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/globalSearch.html',
        scope: {
            visible: '='
        },
        link: function(scope, elem, attrs) {
            scope.teams = UserInfoService.get().teams;
            scope.filterByTeam = !!scope.teams.length;      // init as enabled only if user has teams
            scope.focusIndex = 0;

            scope.$on('keydown', function(msg, obj) {

                var focusedElement = $('.global-search ul li')[scope.focusIndex];

                if (obj.code === 13 && focusedElement) {
                    return $('.global-search ul li.highlight a').click();
                }

                if (obj.code === 38 && scope.focusIndex > 0) {
                    scope.focusIndex--;
                } else if (obj.code === 40 && scope.focusIndex < scope.data.length - 1) {
                    scope.focusIndex++;
                }

                focusedElement = $('.global-search ul li')[scope.focusIndex];

                if (focusedElement) {
                    focusedElement.scrollIntoView();
                }

                scope.$apply();
            });

            scope.$watch('[query, filterByTeam]', function() {
                if (scope.query) {
                    CommunicationService.search(scope.query, scope.teams).then(function(response) {
                        scope.data = [];
                        _.map(response, function(arr, key) {
                            _.each(arr, function(a) {
                                a.type = key;
                            });
                            scope.data = scope.data.concat(arr);
                        }, []);
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
