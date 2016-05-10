///<reference path="../../headers/common.d.ts" />
System.register(['../../core/core_module'], function(exports_1) {
    var core_module_1;
    var DataSourcesCtrl;
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            DataSourcesCtrl = (function () {
                /** @ngInject */
                function DataSourcesCtrl($scope, $location, $http, backendSrv, datasourceSrv) {
                    var _this = this;
                    this.$scope = $scope;
                    this.$location = $location;
                    this.$http = $http;
                    this.backendSrv = backendSrv;
                    this.datasourceSrv = datasourceSrv;
                    backendSrv.get('/api/datasources')
                        .then(function (result) {
                        _this.datasources = result;
                    });
                }
                DataSourcesCtrl.prototype.removeDataSourceConfirmed = function (ds) {
                    var _this = this;
                    this.backendSrv.delete('/api/datasources/' + ds.id)
                        .then(function () {
                        _this.$scope.appEvent('alert-success', ['Datasource deleted', '']);
                    }, function () {
                        _this.$scope.appEvent('alert-error', ['Unable to delete datasource', '']);
                    }).then(function () {
                        _this.backendSrv.get('/api/datasources')
                            .then(function (result) {
                            _this.datasources = result;
                        });
                        _this.backendSrv.get('/api/frontend/settings')
                            .then(function (settings) {
                            _this.datasourceSrv.init(settings.datasources);
                        });
                    });
                };
                DataSourcesCtrl.prototype.removeDataSource = function (ds) {
                    var _this = this;
                    this.$scope.appEvent('confirm-modal', {
                        title: 'Delete',
                        text: 'Are you sure you want to delete datasource ' + ds.name + '?',
                        yesText: "Delete",
                        icon: "fa-trash",
                        onConfirm: function () {
                            _this.removeDataSourceConfirmed(ds);
                        }
                    });
                };
                return DataSourcesCtrl;
            })();
            exports_1("DataSourcesCtrl", DataSourcesCtrl);
            core_module_1.default.controller('DataSourcesCtrl', DataSourcesCtrl);
        }
    }
});
//# sourceMappingURL=ds_list_ctrl.js.map