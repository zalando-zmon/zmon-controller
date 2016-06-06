angular.module('zmon2App').directive('autofocus', function($timeout) {
    return {
        link: function ( scope, element, attrs ) {
            scope.$watch( attrs.ngFocus, function ( val ) {
                    $timeout( function () { element[0].focus(); } );
            }, true);
        }
    };
});
