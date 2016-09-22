angular.module('zmon2App').directive('keyTrap', function() {
    return function(scope, elem) {
        elem.bind('keydown', function(event) {
            scope.$broadcast('keydown', { code: event.keyCode });
        });
    };
});
