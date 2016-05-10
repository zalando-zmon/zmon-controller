///<reference path="../../headers/common.d.ts" />
System.register(['angular', 'lodash'], function(exports_1) {
    var angular_1, lodash_1;
    var pluginInfoCache, AppPageCtrl;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            }],
        execute: function() {
            pluginInfoCache = {};
            AppPageCtrl = (function () {
                /** @ngInject */
                function AppPageCtrl(backendSrv, $routeParams, $rootScope) {
                    this.backendSrv = backendSrv;
                    this.$routeParams = $routeParams;
                    this.$rootScope = $rootScope;
                    this.pluginId = $routeParams.pluginId;
                    if (pluginInfoCache[this.pluginId]) {
                        this.initPage(pluginInfoCache[this.pluginId]);
                    }
                    else {
                        this.loadPluginInfo();
                    }
                }
                AppPageCtrl.prototype.initPage = function (app) {
                    this.appModel = app;
                    this.page = lodash_1.default.findWhere(app.includes, { slug: this.$routeParams.slug });
                    this.appLogoUrl = app.info.logos.small;
                    pluginInfoCache[this.pluginId] = app;
                    if (!this.page) {
                        this.$rootScope.appEvent('alert-error', ['App Page Not Found', '']);
                    }
                };
                AppPageCtrl.prototype.loadPluginInfo = function () {
                    var _this = this;
                    this.backendSrv.get("/api/plugins/" + this.pluginId + "/settings").then(function (app) {
                        _this.initPage(app);
                    });
                };
                return AppPageCtrl;
            })();
            exports_1("AppPageCtrl", AppPageCtrl);
            angular_1.default.module('grafana.controllers').controller('AppPageCtrl', AppPageCtrl);
        }
    }
});
//# sourceMappingURL=plugin_page_ctrl.js.map