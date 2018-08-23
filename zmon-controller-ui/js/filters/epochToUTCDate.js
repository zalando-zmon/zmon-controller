angular.module('zmon2App').filter('epochToUTCDate', ['$filter', function($filter) {
    return function(epochSecs) {
        var epochStart = new Date(0); // 1 Jan 1970 00:00:00 UTC
        epochStart.setUTCSeconds(epochSecs);
        var dateStr = epochStart.toUTCString();
        return dateStr.substr(5,dateStr.length-9);
       // return $filter('date')(epochStart, 'yyyy-MM-dd HH:mm:ss');
    };
}]);
