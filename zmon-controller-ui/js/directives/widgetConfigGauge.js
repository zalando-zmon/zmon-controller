angular.module('zmon2App').directive('widgetConfigGauge', ['$compile', '$log',
    function ($compile, $log) {
        return {
            restrict: 'E',
            scope: {
                widget: '=',
                alertStyleTypes: '='
            },
            templateUrl: 'templates/widgetConfigGauge.html',
            link: function(scope, elem, attr) {
                scope.alertStyles = [];
                _.each(scope.widget.alertStyles, function(value, type) {
                    scope.alertStyles.push({
                        type: type,
                        value: value
                    });
                });

                scope.$watch('alertStyles', function(alertStyles) {
                    scope.widget.alertStyles = {};
                    _.each(alertStyles, function(a) {
                        if (a.type && a.value && a.value.length) {
                            scope.widget.alertStyles[a.type] = [];
                            _.each(a.value, function(n) {
                                scope.widget.alertStyles[a.type].push(parseInt(n))
                            });
                        }
                    });
                }, true);
            }
        };
    }
]);

