angular.module('zmon2App').directive('globalSearch', [ '$timeout', 'CommunicationService', 'UserInfoService', function($timeout, CommunicationService, UserInfoService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/globalSearch.html',
        scope: {
            visible: '='
        },
        link: function(scope, elem, attrs) {
            scope.teams = UserInfoService.get().teams;
            scope.teams = 'STUPS,Eagle Eye,Platform';
            scope.filterByTeam = !!scope.teams.length;      // init as enabled only if user has teams
            scope.focusIndex = 0;

            var fetchData = function() {
                var teams = scope.filterByTeam ? scope.teams : null;
                CommunicationService.search(scope.query, teams).then(function(response) {
                    scope.data = [];
                    _.map(response, function(arr, key) {
                        _.each(arr, function(a) {
                            a.type = key;
                        });
                        scope.data = scope.data.concat(arr);
                    }, []);
                });
            };

            scope.$on('keydown', function(msg, obj) {

                if (obj.code !== 13 && obj.code !== 38 && obj.code !== 40) {
                    return;
                }

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

            scope.$watch('query', function() {
                if (scope.query) {
                    fetchData();
                }
            });

            scope.toggleTeamFilter = function() {
                scope.filterByTeam = !scope.filterByTeam;
                fetchData();
            };

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
