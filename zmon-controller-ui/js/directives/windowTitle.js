angular.module('zmon2App').directive('windowTitle', [ '$rootScope',
    function($rootScope) {
        return {
            restrict: 'E',
            scope: {
                title: '@title',
            },
            link: function ( scope, element, attrs ) {

                $rootScope.$on('$routeChangeSuccess', function() {
                    document.title = 'ZMON'
                });

                scope.$watch('title', function (title) {
                    if (title) {
                        document.title = title;
                    }
                });

            }
        };
    }
]);
