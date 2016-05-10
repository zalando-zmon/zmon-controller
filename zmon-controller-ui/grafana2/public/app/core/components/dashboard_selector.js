///<reference path="../../headers/common.d.ts" />
System.register(['app/core/core_module'], function(exports_1) {
    var core_module_1;
    var template, DashboardSelectorCtrl;
    function dashboardSelector() {
        return {
            restrict: 'E',
            controller: DashboardSelectorCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
            template: template,
            scope: {
                model: '='
            }
        };
    }
    exports_1("dashboardSelector", dashboardSelector);
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            template = "\n<select class=\"gf-form-input\" ng-model=\"ctrl.model\" ng-options=\"f.value as f.text for f in ctrl.options\"></select>\n<info-popover mode=\"right-absolute\">\n  Not finding dashboard you want? Star it first, then it should appear in this select box.\n</info-popover>\n";
            DashboardSelectorCtrl = (function () {
                /** @ngInject */
                function DashboardSelectorCtrl(backendSrv) {
                    this.backendSrv = backendSrv;
                }
                DashboardSelectorCtrl.prototype.$onInit = function () {
                    var _this = this;
                    this.options = [{ value: 0, text: 'Default' }];
                    return this.backendSrv.search({ starred: true }).then(function (res) {
                        res.forEach(function (dash) {
                            _this.options.push({ value: dash.id, text: dash.title });
                        });
                    });
                };
                return DashboardSelectorCtrl;
            })();
            exports_1("DashboardSelectorCtrl", DashboardSelectorCtrl);
            core_module_1.default.directive('dashboardSelector', dashboardSelector);
        }
    }
});
//# sourceMappingURL=dashboard_selector.js.map