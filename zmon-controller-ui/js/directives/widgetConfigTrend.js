angular.module('zmon2App').directive('widgetConfigTrend', ['$compile', '$log',
    function ($compile, $log) {
        return {
            restrict: 'E',
            scope: {
                widget: '=',
            },
            templateUrl: 'templates/widgetConfigTrend.html',
            link: function (scope, element, attrs, controller) {
                var options = scope.widget.options || {};
            }
        };
    }
]);
