angular.module('zmon2App').directive('globalSearch', [ '$timeout', 'CommunicationService', function($timeout, CommunicationService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/globalSearch.html',
        scope: {
            visible: '='
        },
        link: function(scope, elem, attrs) {
            scope.data = [];

            // filtery by name/title or ID only
            scope.search = function(item) {
                return (angular.lowercase(item.name || item.title).indexOf(angular.lowercase(scope.query) || '') !== -1 ||
                        angular.lowercase(String(item.id)).indexOf(angular.lowercase(scope.query) || '') !== -1);
            };

            scope.$watch('query', function(query) {
                CommunicationService.search(query).then(function(response) {
                    scope.data = response;
                });
            });

            // focus input field on open
            scope.$watch('visible', function(v) {
                if (v === true) {
                    $timeout(function() {
                        elem.find('input').focus();
                    });
                }
            });

            // initialize with as much data as possible
            CommunicationService.search('').then(function(response) {
                scope.data = response;
            });
        }
    };
}]);
