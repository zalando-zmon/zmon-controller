angular.module('zmon2App').filter('awsName', function() {
    /*
     * Shortens AWS Entity names. Does nothing for non aws entity names.
     * i.e: "i-09ec2da3[aws:786011980701:eu-west-1]" => "i-09ec2da3[701]"
    */
    return function(value, length) {
        var r = /([^\[]+)\[aws:([0-9]+)(:[^\]]+)?]/;
        if (r.test(value)) {
            var m = value.match(r);
            var id = m[2];
            value = m[1] + '[' + id.slice(-3) + ']';
        }
        return value;
    };
});
