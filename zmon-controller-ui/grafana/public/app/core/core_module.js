///<reference path="../headers/common.d.ts" />
System.register(['angular'], function(exports_1) {
    var angular_1;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            }],
        execute: function() {
            exports_1("default",angular_1.default.module('grafana.core', ['ngRoute']));
        }
    }
});
//# sourceMappingURL=core_module.js.map