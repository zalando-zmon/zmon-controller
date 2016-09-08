angular.module('zmon2App').directive('globalSearch', [ '$timeout', 'CommunicationService', function($timeout, CommunicationService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/globalSearch.html',
        scope: {
            visible: '='
        },
        link: function(scope, elem, attrs) {
            scope.data = [];
            scope.filterByTeam = false;

            scope.$watch('query', function(query) {
                if (query) {
                    CommunicationService.search(query).then(function(response) {
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
