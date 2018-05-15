///<reference path="../../headers/common.d.ts" />
System.register(['angular', 'lodash', 'app/core/config', 'app/core/core'], function(exports_1) {
    var angular_1, lodash_1, config_1, core_1;
    var datasourceTypes, defaults, datasourceCreated, DataSourceEditCtrl;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            datasourceTypes = [];
            defaults = {
                name: '',
                type: 'graphite',
                url: '',
                access: 'proxy',
                jsonData: {}
            };
            datasourceCreated = false;
            DataSourceEditCtrl = (function () {
                /** @ngInject */
                function DataSourceEditCtrl($scope, $q, backendSrv, $routeParams, $location, datasourceSrv) {
                    var _this = this;
                    this.$scope = $scope;
                    this.$q = $q;
                    this.backendSrv = backendSrv;
                    this.$routeParams = $routeParams;
                    this.$location = $location;
                    this.datasourceSrv = datasourceSrv;
                    this.isNew = true;
                    this.datasources = [];
                    this.tabIndex = 0;
                    this.loadDatasourceTypes().then(function () {
                        if (_this.$routeParams.id) {
                            _this.getDatasourceById(_this.$routeParams.id);
                        }
                        else {
                            _this.current = angular_1.default.copy(defaults);
                            _this.typeChanged();
                        }
                    });
                }
                DataSourceEditCtrl.prototype.loadDatasourceTypes = function () {
                    var _this = this;
                    if (datasourceTypes.length > 0) {
                        this.types = datasourceTypes;
                        return this.$q.when(null);
                    }
                    return this.backendSrv.get('/api/plugins', { enabled: 1, type: 'datasource' }).then(function (plugins) {
                        datasourceTypes = plugins;
                        _this.types = plugins;
                    });
                };
                DataSourceEditCtrl.prototype.getDatasourceById = function (id) {
                    var _this = this;
                    this.backendSrv.get('/api/datasources/' + id).then(function (ds) {
                        _this.isNew = false;
                        _this.current = ds;
                        if (datasourceCreated) {
                            datasourceCreated = false;
                            _this.testDatasource();
                        }
                        return _this.typeChanged();
                    });
                };
                DataSourceEditCtrl.prototype.typeChanged = function () {
                    var _this = this;
                    this.hasDashboards = false;
                    return this.backendSrv.get('/api/plugins/' + this.current.type + '/settings').then(function (pluginInfo) {
                        _this.datasourceMeta = pluginInfo;
                        _this.hasDashboards = lodash_1.default.findWhere(pluginInfo.includes, { type: 'dashboard' });
                    });
                };
                DataSourceEditCtrl.prototype.updateFrontendSettings = function () {
                    var _this = this;
                    return this.backendSrv.get('/api/frontend/settings').then(function (settings) {
                        config_1.default.datasources = settings.datasources;
                        config_1.default.defaultDatasource = settings.defaultDatasource;
                        _this.datasourceSrv.init();
                    });
                };
                DataSourceEditCtrl.prototype.testDatasource = function () {
                    var _this = this;
                    this.testing = { done: false };
                    this.datasourceSrv.get(this.current.name).then(function (datasource) {
                        if (!datasource.testDatasource) {
                            delete _this.testing;
                            return;
                        }
                        return datasource.testDatasource().then(function (result) {
                            _this.testing.message = result.message;
                            _this.testing.status = result.status;
                            _this.testing.title = result.title;
                        }).catch(function (err) {
                            if (err.statusText) {
                                _this.testing.message = err.statusText;
                                _this.testing.title = "HTTP Error";
                            }
                            else {
                                _this.testing.message = err.message;
                                _this.testing.title = "Unknown error";
                            }
                        });
                    }).finally(function () {
                        if (_this.testing) {
                            _this.testing.done = true;
                        }
                    });
                };
                DataSourceEditCtrl.prototype.saveChanges = function (test) {
                    var _this = this;
                    if (!this.editForm.$valid) {
                        return;
                    }
                    if (this.current.id) {
                        return this.backendSrv.put('/api/datasources/' + this.current.id, this.current).then(function () {
                            _this.updateFrontendSettings().then(function () {
                                _this.testDatasource();
                            });
                        });
                    }
                    else {
                        return this.backendSrv.post('/api/datasources', this.current).then(function (result) {
                            _this.updateFrontendSettings();
                            datasourceCreated = true;
                            _this.$location.path('datasources/edit/' + result.id);
                        });
                    }
                };
                ;
                DataSourceEditCtrl.prototype.confirmDelete = function () {
                    var _this = this;
                    this.backendSrv.delete('/api/datasources/' + this.current.id).then(function () {
                        _this.$location.path('datasources');
                    });
                };
                DataSourceEditCtrl.prototype.delete = function (s) {
                    var _this = this;
                    core_1.appEvents.emit('confirm-modal', {
                        title: 'Delete',
                        text: 'Are you sure you want to delete this datasource?',
                        yesText: "Delete",
                        icon: "fa-trash",
                        onConfirm: function () {
                            _this.confirmDelete();
                        }
                    });
                };
                return DataSourceEditCtrl;
            })();
            exports_1("DataSourceEditCtrl", DataSourceEditCtrl);
            core_1.coreModule.controller('DataSourceEditCtrl', DataSourceEditCtrl);
            core_1.coreModule.directive('datasourceHttpSettings', function () {
                return {
                    scope: {
                        current: "=",
                        suggestUrl: "@",
                    },
                    templateUrl: 'public/app/features/plugins/partials/ds_http_settings.html',
                    link: {
                        pre: function ($scope, elem, attrs) {
                            $scope.getSuggestUrls = function () {
                                return [$scope.suggestUrl];
                            };
                        }
                    }
                };
            });
        }
    }
});
//# sourceMappingURL=ds_edit_ctrl.js.map