angular.module('zmon2App').filter('timeago', ['timespanFilter', 'MainAlertService', function(timespanFilter, MainAlertService) {
        return function(epochPastTs) {
            return timespanFilter(MainAlertService.millisecondsApart(epochPastTs, MainAlertService.getLastUpdate()));
        };
}]);
