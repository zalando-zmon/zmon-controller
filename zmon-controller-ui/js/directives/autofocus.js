angular.module('zmon2App').directive('autofocus', function($timeout) {
    return {
        link: function ( scope, element, attrs ) {

            //avoid focusing other elements if search pane is open
            if ($('#search').is(":visible")) {
                return;
            }

            $timeout( function () { element[0].focus(); } );
        }
    };
});
