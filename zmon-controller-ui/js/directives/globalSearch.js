angular.module('zmon2App').directive('globalSearch', [ '$timeout', 'CommunicationService', 'localStorageService', 'UserInfoService', function($timeout, CommunicationService, localStorageService, UserInfoService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/globalSearch.html',
        scope: {
            visible: '='
        },
        link: function(scope, elem, attrs) {
            scope.teams = UserInfoService.get().teams;
            scope.focusIndex = 0;

            // get state from Local Storage and intiialize; Team Filter is disabled unless user belongs to teams
            scope.ls = localStorageService.get('globalSearch') || {};
            scope.filterByTeam = typeof scope.ls.filterByTeam !== 'undefined' ? scope.ls.filterByTeam : !!scope.teams.length;
            scope.visible = scope.ls.visible;
            scope.query = scope.ls.query || '';

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
                scope.ls.query = scope.query;
                if (scope.query) {
                    fetchData();
                }
            });

            scope.toggleTeamFilter = function() {
                scope.filterByTeam = !scope.filterByTeam;
                scope.ls.filterByTeam = scope.filterByTeam;
                fetchData();
            };

            // focus input field on open
            scope.$watch('visible', function(v) {
                scope.ls.visible = v;
                if (v === true) {
                    $timeout(function() {
                        elem.find('input').focus();
                    });
                }
            });

            scope.$watch('ls', function() {
                localStorageService.set('globalSearch', scope.ls);
            }, true);
        }
    };
}]);
