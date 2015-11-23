angular.module('zmon2App').filter('tooltipCapture', function() {
    return function(value) {
        if (typeof value === 'object') {
            var args = [];
            _.each(value, function(v, k) {
                args.push('<li>' + k + ' = ' + v + '</li>');
            });
            value = '<ul class="capture-tooltip">' + args.join('') + '</ul>';
        }
        return value;
    };
});
