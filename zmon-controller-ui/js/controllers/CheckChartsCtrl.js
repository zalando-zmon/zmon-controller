angular.module('zmon2App').controller('CheckChartsCtrl', ['$routeParams', '$location',
    function($routeParams, $location) {
        var qs = $location.search();
        var suffix = '';
        if (qs.entity_id) {
            suffix = '-' + qs.entity_id.split(',')[0];
        }
        // redirect to our new dynamic Grafana dashboard :-)
        document.location.href = '/grafana/dashboard/db/zmon-check-' + $routeParams.checkId + suffix;
    }
]);

