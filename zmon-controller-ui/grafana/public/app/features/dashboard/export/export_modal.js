///<reference path="../../../headers/common.d.ts" />
System.register(['angular', 'app/core/core_module', './exporter'], function(exports_1) {
    var angular_1, core_module_1, exporter_1;
    var DashExportCtrl;
    function dashExportDirective() {
        return {
            restrict: 'E',
            templateUrl: 'public/app/features/dashboard/export/export_modal.html',
            controller: DashExportCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
        };
    }
    exports_1("dashExportDirective", dashExportDirective);
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            },
            function (exporter_1_1) {
                exporter_1 = exporter_1_1;
            }],
        execute: function() {
            DashExportCtrl = (function () {
                /** @ngInject */
                function DashExportCtrl(backendSrv, dashboardSrv, datasourceSrv, $scope) {
                    var _this = this;
                    this.backendSrv = backendSrv;
                    this.exporter = new exporter_1.DashboardExporter(datasourceSrv);
                    var current = dashboardSrv.getCurrent().getSaveModelClone();
                    this.exporter.makeExportable(current).then(function (dash) {
                        $scope.$apply(function () {
                            _this.dash = dash;
                        });
                    });
                }
                DashExportCtrl.prototype.save = function () {
                    var blob = new Blob([angular_1.default.toJson(this.dash, true)], { type: "application/json;charset=utf-8" });
                    var wnd = window;
                    wnd.saveAs(blob, this.dash.title + '-' + new Date().getTime() + '.json');
                };
                DashExportCtrl.prototype.saveJson = function () {
                    var html = angular_1.default.toJson(this.dash, true);
                    var uri = "data:application/json," + encodeURIComponent(html);
                    var newWindow = window.open(uri);
                };
                return DashExportCtrl;
            })();
            exports_1("DashExportCtrl", DashExportCtrl);
            core_module_1.default.directive('dashExportModal', dashExportDirective);
        }
    }
});
//# sourceMappingURL=export_modal.js.map