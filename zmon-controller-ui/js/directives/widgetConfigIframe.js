angular.module('zmon2App').directive('widgetConfigIframe', ['$compile', '$log',
    function ($compile, $log) {
        return {
            restrict: 'E',
            scope: {
                widget: '='
            },
            templateUrl: 'templates/widgetConfigIframe.html'
        };
    }
]);
