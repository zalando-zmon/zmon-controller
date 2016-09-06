angular.module('zmon2App').directive('globalSearch', [ '$timeout', 'CommunicationService', function($timeout, CommunicationService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/globalSearch.html',
        scope: {
            visible: '='
        },
        link: function(scope, elem, attrs) {
            scope.data = [];

            CommunicationService.getAllDashboards().then(function(dashboards) {
                _.each(dashboards, function(dashboard) { dashboard.type = 'dashboard'; });
                scope.data = scope.data.concat(dashboards);
            });

            CommunicationService.getAlertDefinitions().then(function(alerts) {
                _.each(alerts, function(alert) { alert.type = 'alert'; });
                scope.data = scope.data.concat(alerts);
            });

            CommunicationService.getCheckDefinitions().then(function(checks) {
                _.each(checks, function(check) { check.type = 'check'; });
                scope.data = scope.data.concat(checks);
            });

            CommunicationService.getGrafanaDashboards().then(function(grafs) {
                _.each(grafs, function(graf) { graf.type = 'grafana'; });
                scope.data = scope.data.concat(grafs);
            });

            // filtery by name/title or ID only
            scope.search = function(item) {
                return (angular.lowercase(item.name || item.title).indexOf(angular.lowercase(scope.query) || '') !== -1 ||
                        angular.lowercase(String(item.id)).indexOf(angular.lowercase(scope.query) || '') !== -1);
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
