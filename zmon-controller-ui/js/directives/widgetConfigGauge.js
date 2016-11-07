angular.module('zmon2App').directive('widgetConfigGauge', ['$compile', '$log',
    function ($compile, $log) {
        return {
            restrict: 'E',
            scope: {
                widget: '='
            },
            templateUrl: 'templates/widgetConfigGauge.html',
            link: function(scope, elem, attrs) {
                scope.$watch('widget.options', function(options) {
                    if (!options.format) {
                        delete options.format;
                    }
                    if (!options.max) {
                        delete options.max;
                    }
                    if (!options.min) {
                        delete options.min;
                    }

                }, true);
            }
        };
    }
]);

