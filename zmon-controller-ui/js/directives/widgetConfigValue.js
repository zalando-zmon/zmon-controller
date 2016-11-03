angular.module('zmon2App').directive('widgetConfigValue', ['$compile', '$log',
    function ($compile, $log) {
        return {
            restrict: 'E',
            scope: {
                widget: '=',
            },
            templateUrl: 'templates/widgetConfigValue.html',
            link: function(scope, elem, attrs) {
                scope.$watch('widget.options', function(options) {
                    if (!options.format) {
                        delete options.format;
                    }
                    if (!options.fontSize) {
                        delete options.fontSize;
                    }
                    if (!options.color) {
                        delete options.color;
                    }
                }, true);
            }
        };
    }
]);
