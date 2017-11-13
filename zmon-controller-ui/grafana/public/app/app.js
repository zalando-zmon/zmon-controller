///<reference path="headers/common.d.ts" />
System.register(['bootstrap', 'vendor/filesaver', 'lodash-src', 'angular-strap', 'angular-route', 'angular-sanitize', 'angular-dragdrop', 'angular-bindonce', 'angular-ui', 'jquery', 'angular', 'app/core/config', 'lodash', './core/core'], function(exports_1) {
    var jquery_1, angular_1, config_1, lodash_1, core_1;
    var GrafanaApp;
    return {
        setters:[
            function (_1) {},
            function (_2) {},
            function (_3) {},
            function (_4) {},
            function (_5) {},
            function (_6) {},
            function (_7) {},
            function (_8) {},
            function (_9) {},
            function (jquery_1_1) {
                jquery_1 = jquery_1_1;
            },
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            GrafanaApp = (function () {
                function GrafanaApp() {
                    this.preBootModules = [];
                    this.registerFunctions = {};
                    this.ngModuleDependencies = [];
                }
                GrafanaApp.prototype.useModule = function (module) {
                    if (this.preBootModules) {
                        this.preBootModules.push(module);
                    }
                    else {
                        lodash_1.default.extend(module, this.registerFunctions);
                    }
                    this.ngModuleDependencies.push(module.name);
                    return module;
                };
                GrafanaApp.prototype.init = function () {
                    var _this = this;
                    var app = angular_1.default.module('grafana', []);
                    app.constant('grafanaVersion', "@grafanaVersion@");
                    app.config(function ($locationProvider, $controllerProvider, $compileProvider, $filterProvider, $httpProvider, $provide) {
                        if (config_1.default.buildInfo.env !== 'development') {
                            $compileProvider.debugInfoEnabled(false);
                        }
                        $httpProvider.useApplyAsync(true);
                        _this.registerFunctions.controller = $controllerProvider.register;
                        _this.registerFunctions.directive = $compileProvider.directive;
                        _this.registerFunctions.factory = $provide.factory;
                        _this.registerFunctions.service = $provide.service;
                        _this.registerFunctions.filter = $filterProvider.register;
                        $provide.decorator("$http", ["$delegate", "$templateCache", function ($delegate, $templateCache) {
                                var get = $delegate.get;
                                $delegate.get = function (url, config) {
                                    if (url.match(/\.html$/)) {
                                        // some template's already exist in the cache
                                        if (!$templateCache.get(url)) {
                                            url += "?v=" + new Date().getTime();
                                        }
                                    }
                                    return get(url, config);
                                };
                                return $delegate;
                            }]);
                    });
                    this.ngModuleDependencies = [
                        'grafana.core',
                        'ngRoute',
                        'ngSanitize',
                        '$strap.directives',
                        'ang-drag-drop',
                        'grafana',
                        'pasvaz.bindonce',
                        'ui.bootstrap',
                        'ui.bootstrap.tpls',
                    ];
                    var module_types = ['controllers', 'directives', 'factories', 'services', 'filters', 'routes'];
                    lodash_1.default.each(module_types, function (type) {
                        var moduleName = 'grafana.' + type;
                        _this.useModule(angular_1.default.module(moduleName, []));
                    });
                    // makes it possible to add dynamic stuff
                    this.useModule(core_1.coreModule);
                    var preBootRequires = [System.import('app/features/all')];
                    Promise.all(preBootRequires).then(function () {
                        // disable tool tip animation
                        jquery_1.default.fn.tooltip.defaults.animation = false;
                        // bootstrap the app
                        angular_1.default.bootstrap(document, _this.ngModuleDependencies).invoke(function () {
                            lodash_1.default.each(_this.preBootModules, function (module) {
                                lodash_1.default.extend(module, _this.registerFunctions);
                            });
                            _this.preBootModules = null;
                        });
                    }).catch(function (err) {
                        console.log('Application boot failed:', err);
                    });
                };
                return GrafanaApp;
            })();
            exports_1("GrafanaApp", GrafanaApp);
            exports_1("default",new GrafanaApp());
        }
    }
});
//# sourceMappingURL=app.js.map