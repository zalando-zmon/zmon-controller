angular.module('zmon2App').directive('autofocus', function($timeout) {
    return {
        link: function ( scope, element, attrs ) {
            $timeout( function () { element[0].focus(); } );
        }
    };
});
