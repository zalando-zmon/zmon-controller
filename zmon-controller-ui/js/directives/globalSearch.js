angular.module('zmon2App').directive('globalSearch', [ '$timeout', 'CommunicationService', function($timeout, CommunicationService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/globalSearch.html',
        scope: {
            visible: '='
        },
        link: function(scope, elem, attrs) {
            scope.data = [];

            scope.$watch('query', function(query, teams) {
                if (query) {
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
