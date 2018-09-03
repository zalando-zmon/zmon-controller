angular.module('zmon2App').filter('UTC', ['$filter', function($filter) {
    return function(localdate) {
       
        localdate = new Date(localdate);

        var utcdate = new Date(localdate.getUTCFullYear(), localdate.getUTCMonth(), localdate.getUTCDate(),  localdate.getUTCHours(), localdate.getUTCMinutes(), localdate.getUTCSeconds());
        return utcdate;
    };
}]);
