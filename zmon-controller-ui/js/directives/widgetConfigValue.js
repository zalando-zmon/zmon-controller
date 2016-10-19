angular.module('zmon2App').directive('widgetConfigValue', ['$compile', '$log',
    function ($compile, $log) {
        return {
            restrict: 'E',
            scope: {
                widget: '=',
            },
            templateUrl: 'templates/widgetConfigValue.html',
            link: function (scope, element, attrs, controller) {
                var options = scope.widget.options || {};
            }
        };
    }
]);

