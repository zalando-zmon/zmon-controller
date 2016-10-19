angular.module('zmon2App').directive('widgetConfigGauge', ['$compile', '$log',
    function ($compile, $log) {
        return {
            restrict: 'E',
            scope: {
                widget: '=',
            },
            templateUrl: 'templates/widgetConfigGauge.html',
            link: function (scope, element, attrs, controller) {
                var options = scope.widget.options || {};
            }
        };
    }
]);

