///<reference path="../../headers/common.d.ts" />
System.register(['app/core/core_module'], function(exports_1) {
    var core_module_1;
    var DashListCtrl;
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            DashListCtrl = (function () {
                /** @ngInject */
                function DashListCtrl() {
                }
                return DashListCtrl;
            })();
            exports_1("DashListCtrl", DashListCtrl);
            core_module_1.default.controller('DashListCtrl', DashListCtrl);
        }
    }
});
//# sourceMappingURL=dash_list_ctrl.js.map