///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', '../../../features/panel/panel_ctrl'], function(exports_1) {
    var __extends = (this && this.__extends) || function (d, b) {
        for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
    var lodash_1, panel_ctrl_1;
    var PluginListCtrl;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (panel_ctrl_1_1) {
                panel_ctrl_1 = panel_ctrl_1_1;
            }],
        execute: function() {
            PluginListCtrl = (function (_super) {
                __extends(PluginListCtrl, _super);
                /** @ngInject */
                function PluginListCtrl($scope, $injector, backendSrv, $location) {
                    _super.call(this, $scope, $injector);
                    this.backendSrv = backendSrv;
                    this.$location = $location;
                    // Set and populate defaults
                    this.panelDefaults = {};
                    lodash_1.default.defaults(this.panel, this.panelDefaults);
                    this.events.on('init-edit-mode', this.onInitEditMode.bind(this));
                    this.pluginList = [];
                    this.viewModel = [
                        { header: "Installed Apps", list: [], type: 'app' },
                        { header: "Installed Panels", list: [], type: 'panel' },
                        { header: "Installed Datasources", list: [], type: 'datasource' },
                    ];
                    this.update();
                }
                PluginListCtrl.prototype.onInitEditMode = function () {
                    this.editorTabIndex = 1;
                    this.addEditorTab('Options', 'public/app/plugins/panel/pluginlist/editor.html');
                };
                PluginListCtrl.prototype.gotoPlugin = function (plugin, evt) {
                    if (evt) {
                        evt.stopPropagation();
                    }
                    this.$location.url("plugins/" + plugin.id + "/edit");
                };
                PluginListCtrl.prototype.updateAvailable = function (plugin, $event) {
                    $event.stopPropagation();
                    $event.preventDefault();
                    var modalScope = this.$scope.$new(true);
                    modalScope.plugin = plugin;
                    this.publishAppEvent('show-modal', {
                        src: 'public/app/features/plugins/partials/update_instructions.html',
                        scope: modalScope
                    });
                };
                PluginListCtrl.prototype.update = function () {
                    var _this = this;
                    this.backendSrv.get('api/plugins', { embedded: 0, core: 0 }).then(function (plugins) {
                        _this.pluginList = plugins;
                        _this.viewModel[0].list = lodash_1.default.filter(plugins, { type: 'app' });
                        _this.viewModel[1].list = lodash_1.default.filter(plugins, { type: 'panel' });
                        _this.viewModel[2].list = lodash_1.default.filter(plugins, { type: 'datasource' });
                        for (var _i = 0, _a = _this.pluginList; _i < _a.length; _i++) {
                            var plugin = _a[_i];
                            if (plugin.hasUpdate) {
                                plugin.state = 'has-update';
                            }
                            else if (!plugin.enabled) {
                                plugin.state = 'not-enabled';
                            }
                        }
                    });
                };
                PluginListCtrl.templateUrl = 'module.html';
                return PluginListCtrl;
            })(panel_ctrl_1.PanelCtrl);
            exports_1("PluginListCtrl", PluginListCtrl);
            exports_1("PanelCtrl", PluginListCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map