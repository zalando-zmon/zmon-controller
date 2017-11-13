///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', 'app/core/core_module', 'app/core/app_events'], function(exports_1) {
    var lodash_1, core_module_1, app_events_1;
    var DashImportListCtrl;
    function dashboardImportList() {
        return {
            restrict: 'E',
            templateUrl: 'public/app/features/plugins/import_list/import_list.html',
            controller: DashImportListCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
            scope: {
                plugin: "=",
                datasource: "="
            }
        };
    }
    exports_1("dashboardImportList", dashboardImportList);
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            },
            function (app_events_1_1) {
                app_events_1 = app_events_1_1;
            }],
        execute: function() {
            DashImportListCtrl = (function () {
                /** @ngInject */
                function DashImportListCtrl($scope, $http, backendSrv, $rootScope) {
                    var _this = this;
                    this.$http = $http;
                    this.backendSrv = backendSrv;
                    this.$rootScope = $rootScope;
                    this.dashboards = [];
                    backendSrv.get("/api/plugins/" + this.plugin.id + "/dashboards").then(function (dashboards) {
                        _this.dashboards = dashboards;
                    });
                    app_events_1.default.on('dashboard-list-import-all', this.importAll.bind(this), $scope);
                }
                DashImportListCtrl.prototype.importAll = function (payload) {
                    return this.importNext(0).then(function () {
                        payload.resolve("All dashboards imported");
                    }).catch(function (err) {
                        payload.reject(err);
                    });
                };
                DashImportListCtrl.prototype.importNext = function (index) {
                    var _this = this;
                    return this.import(this.dashboards[index], true).then(function () {
                        if (index + 1 < _this.dashboards.length) {
                            return new Promise(function (resolve) {
                                setTimeout(function () {
                                    _this.importNext(index + 1).then(function () {
                                        resolve();
                                    });
                                }, 500);
                            });
                        }
                    });
                };
                DashImportListCtrl.prototype.import = function (dash, overwrite) {
                    var _this = this;
                    var installCmd = {
                        pluginId: this.plugin.id,
                        path: dash.path,
                        overwrite: overwrite,
                        inputs: []
                    };
                    if (this.datasource) {
                        installCmd.inputs.push({
                            name: '*',
                            type: 'datasource',
                            pluginId: this.datasource.type,
                            value: this.datasource.name
                        });
                    }
                    return this.backendSrv.post("/api/dashboards/import", installCmd).then(function (res) {
                        _this.$rootScope.appEvent('alert-success', ['Dashboard Imported', dash.title]);
                        lodash_1.default.extend(dash, res);
                    });
                };
                DashImportListCtrl.prototype.remove = function (dash) {
                    var _this = this;
                    this.backendSrv.delete('/api/dashboards/' + dash.importedUri).then(function () {
                        _this.$rootScope.appEvent('alert-success', ['Dashboard Deleted', dash.title]);
                        dash.imported = false;
                    });
                };
                return DashImportListCtrl;
            })();
            exports_1("DashImportListCtrl", DashImportListCtrl);
            core_module_1.default.directive('dashboardImportList', dashboardImportList);
        }
    }
});
//# sourceMappingURL=import_list.js.map