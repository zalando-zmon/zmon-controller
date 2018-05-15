///<reference path="../../../headers/common.d.ts" />
System.register(['app/core/core_module', 'app/core/config', 'lodash'], function(exports_1) {
    var core_module_1, config_1, lodash_1;
    var DashImportCtrl;
    function dashImportDirective() {
        return {
            restrict: 'E',
            templateUrl: 'public/app/features/dashboard/import/dash_import.html',
            controller: DashImportCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
        };
    }
    exports_1("dashImportDirective", dashImportDirective);
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            },
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            }],
        execute: function() {
            DashImportCtrl = (function () {
                /** @ngInject */
                function DashImportCtrl(backendSrv, $location, $scope, $routeParams) {
                    this.backendSrv = backendSrv;
                    this.$location = $location;
                    this.$scope = $scope;
                    this.$routeParams = $routeParams;
                    this.step = 1;
                    this.nameExists = false;
                    // check gnetId in url
                    if ($routeParams.gnetId) {
                        this.gnetUrl = $routeParams.gnetId;
                        this.checkGnetDashboard();
                    }
                }
                DashImportCtrl.prototype.onUpload = function (dash) {
                    this.dash = dash;
                    this.dash.id = null;
                    this.step = 2;
                    this.inputs = [];
                    if (this.dash.__inputs) {
                        for (var _i = 0, _a = this.dash.__inputs; _i < _a.length; _i++) {
                            var input = _a[_i];
                            var inputModel = {
                                name: input.name,
                                label: input.label,
                                info: input.description,
                                value: input.value,
                                type: input.type,
                                pluginId: input.pluginId,
                                options: []
                            };
                            if (input.type === 'datasource') {
                                this.setDatasourceOptions(input, inputModel);
                            }
                            else if (!inputModel.info) {
                                inputModel.info = 'Specify a string constant';
                            }
                            this.inputs.push(inputModel);
                        }
                    }
                    this.inputsValid = this.inputs.length === 0;
                    this.titleChanged();
                };
                DashImportCtrl.prototype.setDatasourceOptions = function (input, inputModel) {
                    var sources = lodash_1.default.filter(config_1.default.datasources, function (val) {
                        return val.type === input.pluginId;
                    });
                    if (sources.length === 0) {
                        inputModel.info = "No data sources of type " + input.pluginName + " found";
                    }
                    else if (!inputModel.info) {
                        inputModel.info = "Select a " + input.pluginName + " data source";
                    }
                    inputModel.options = sources.map(function (val) {
                        return { text: val.name, value: val.name };
                    });
                };
                DashImportCtrl.prototype.inputValueChanged = function () {
                    this.inputsValid = true;
                    for (var _i = 0, _a = this.inputs; _i < _a.length; _i++) {
                        var input = _a[_i];
                        if (!input.value) {
                            this.inputsValid = false;
                        }
                    }
                };
                DashImportCtrl.prototype.titleChanged = function () {
                    var _this = this;
                    this.backendSrv.search({ query: this.dash.title }).then(function (res) {
                        _this.nameExists = false;
                        for (var _i = 0; _i < res.length; _i++) {
                            var hit = res[_i];
                            if (_this.dash.title === hit.title) {
                                _this.nameExists = true;
                                break;
                            }
                        }
                    });
                };
                DashImportCtrl.prototype.saveDashboard = function () {
                    var _this = this;
                    var inputs = this.inputs.map(function (input) {
                        return {
                            name: input.name,
                            type: input.type,
                            pluginId: input.pluginId,
                            value: input.value
                        };
                    });
                    return this.backendSrv.post('api/dashboards/import', {
                        dashboard: this.dash,
                        overwrite: true,
                        inputs: inputs
                    }).then(function (res) {
                        _this.$location.url('dashboard/' + res.importedUri);
                        _this.$scope.dismiss();
                    });
                };
                DashImportCtrl.prototype.loadJsonText = function () {
                    try {
                        this.parseError = '';
                        var dash = JSON.parse(this.jsonText);
                        this.onUpload(dash);
                    }
                    catch (err) {
                        console.log(err);
                        this.parseError = err.message;
                        return;
                    }
                };
                DashImportCtrl.prototype.checkGnetDashboard = function () {
                    var _this = this;
                    this.gnetError = '';
                    var match = /(^\d+$)|dashboards\/(\d+)/.exec(this.gnetUrl);
                    var dashboardId;
                    if (match && match[1]) {
                        dashboardId = match[1];
                    }
                    else if (match && match[2]) {
                        dashboardId = match[2];
                    }
                    else {
                        this.gnetError = 'Could not find dashboard';
                    }
                    return this.backendSrv.get('api/gnet/dashboards/' + dashboardId).then(function (res) {
                        _this.gnetInfo = res;
                        // store reference to grafana.net
                        res.json.gnetId = res.id;
                        _this.onUpload(res.json);
                    }).catch(function (err) {
                        err.isHandled = true;
                        _this.gnetError = err.data.message || err;
                    });
                };
                DashImportCtrl.prototype.back = function () {
                    this.gnetUrl = '';
                    this.step = 1;
                    this.gnetError = '';
                    this.gnetInfo = '';
                };
                return DashImportCtrl;
            })();
            exports_1("DashImportCtrl", DashImportCtrl);
            core_module_1.default.directive('dashImport', dashImportDirective);
        }
    }
});
//# sourceMappingURL=dash_import.js.map