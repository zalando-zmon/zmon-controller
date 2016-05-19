///<reference path="../../headers/common.d.ts" />
System.register(['angular'], function(exports_1) {
    var angular_1;
    var PluginListCtrl;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            }],
        execute: function() {
            PluginListCtrl = (function () {
                /** @ngInject */
                function PluginListCtrl(backendSrv, $location) {
                    var _this = this;
                    this.backendSrv = backendSrv;
                    this.tabIndex = 0;
                    var pluginType = $location.search().type || 'panel';
                    switch (pluginType) {
                        case "datasource": {
                            this.tabIndex = 1;
                            break;
                        }
                        case "app": {
                            this.tabIndex = 2;
                            break;
                        }
                        case "panel":
                        default:
                            this.tabIndex = 0;
                    }
                    this.backendSrv.get('api/plugins', { embedded: 0, type: pluginType }).then(function (plugins) {
                        _this.plugins = plugins;
                    });
                }
                return PluginListCtrl;
            })();
            exports_1("PluginListCtrl", PluginListCtrl);
            angular_1.default.module('grafana.controllers').controller('PluginListCtrl', PluginListCtrl);
        }
    }
});
//# sourceMappingURL=plugin_list_ctrl.js.map