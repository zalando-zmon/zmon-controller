angular.module('zmon2App').factory('FormatService', [ function() {
    var service = {};

    service.formatNumber = function(option, value) {

        if (!_.isNumber(value)) {
            return value;
        }

        if (option === undefined) {
            return value.toFixed(0)/1
        }

        // extract format spec if in {:} enclosing
        var match = option.match(/\{\:(\.[0-9]f)\}/);

        if (match && match.length) {
            option = match[1];
        }

        return d3.format(option)(value);
    }

    return service;
}]);
