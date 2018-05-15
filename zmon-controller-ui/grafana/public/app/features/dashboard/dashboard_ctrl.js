///<reference path="../../headers/common.d.ts" />
System.register(['app/core/config', 'angular', 'app/core/core_module'], function(exports_1) {
    var config_1, angular_1, core_module_1;
    var DashboardCtrl;
    return {
        setters:[
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            DashboardCtrl = (function () {
                /** @ngInject */
                function DashboardCtrl($scope, $rootScope, dashboardKeybindings, timeSrv, templateValuesSrv, dashboardSrv, unsavedChangesSrv, dynamicDashboardSrv, dashboardViewStateSrv, contextSrv, $timeout) {
                    this.$scope = $scope;
                    this.$rootScope = $rootScope;
                    $scope.editor = { index: 0 };
                    $scope.panels = config_1.default.panels;
                    var resizeEventTimeout;
                    $scope.setupDashboard = function (data) {
                        var dashboard = dashboardSrv.create(data.dashboard, data.meta);
                        dashboardSrv.setCurrent(dashboard);
                        // init services
                        timeSrv.init(dashboard);
                        // template values service needs to initialize completely before
                        // the rest of the dashboard can load
                        templateValuesSrv.init(dashboard).finally(function () {
                            dynamicDashboardSrv.init(dashboard);
                            unsavedChangesSrv.init(dashboard, $scope);
                            $scope.dashboard = dashboard;
                            $scope.dashboardMeta = dashboard.meta;
                            $scope.dashboardViewState = dashboardViewStateSrv.create($scope);
                            dashboardKeybindings.shortcuts($scope);
                            $scope.updateSubmenuVisibility();
                            $scope.setWindowTitleAndTheme();
                            $scope.appEvent("dashboard-initialized", $scope.dashboard);
                        }).catch(function (err) {
                            if (err.data && err.data.message) {
                                err.message = err.data.message;
                            }
                            $scope.appEvent("alert-error", ['Dashboard init failed', 'Template variables could not be initialized: ' + err.message]);
                        });
                    };
                    $scope.templateVariableUpdated = function () {
                        dynamicDashboardSrv.update($scope.dashboard);
                    };
                    $scope.updateSubmenuVisibility = function () {
                        $scope.submenuEnabled = $scope.dashboard.isSubmenuFeaturesEnabled();
                    };
                    $scope.setWindowTitleAndTheme = function () {
                        window.document.title = config_1.default.window_title_prefix + $scope.dashboard.title;
                    };
                    $scope.broadcastRefresh = function () {
                        $rootScope.$broadcast('refresh');
                    };
                    $scope.addRow = function (dash, row) {
                        dash.rows.push(row);
                    };
                    $scope.addRowDefault = function () {
                        $scope.resetRow();
                        $scope.row.title = 'New row';
                        $scope.addRow($scope.dashboard, $scope.row);
                    };
                    $scope.resetRow = function () {
                        $scope.row = {
                            title: '',
                            height: '250px',
                            editable: true,
                        };
                    };
                    $scope.showJsonEditor = function (evt, options) {
                        var editScope = $rootScope.$new();
                        editScope.object = options.object;
                        editScope.updateHandler = options.updateHandler;
                        $scope.appEvent('show-dash-editor', { src: 'public/app/partials/edit_json.html', scope: editScope });
                    };
                    $scope.onDrop = function (panelId, row, dropTarget) {
                        var info = $scope.dashboard.getPanelInfoById(panelId);
                        if (dropTarget) {
                            var dropInfo = $scope.dashboard.getPanelInfoById(dropTarget.id);
                            dropInfo.row.panels[dropInfo.index] = info.panel;
                            info.row.panels[info.index] = dropTarget;
                            var dragSpan = info.panel.span;
                            info.panel.span = dropTarget.span;
                            dropTarget.span = dragSpan;
                        }
                        else {
                            info.row.panels.splice(info.index, 1);
                            info.panel.span = 12 - $scope.dashboard.rowSpan(row);
                            row.panels.push(info.panel);
                        }
                        $rootScope.$broadcast('render');
                    };
                    $scope.registerWindowResizeEvent = function () {
                        angular_1.default.element(window).bind('resize', function () {
                            $timeout.cancel(resizeEventTimeout);
                            resizeEventTimeout = $timeout(function () { $scope.$broadcast('render'); }, 200);
                        });
                        $scope.$on('$destroy', function () {
                            angular_1.default.element(window).unbind('resize');
                        });
                    };
                    $scope.timezoneChanged = function () {
                        $rootScope.$broadcast("refresh");
                    };
                }
                DashboardCtrl.prototype.init = function (dashboard) {
                    this.$scope.resetRow();
                    this.$scope.registerWindowResizeEvent();
                    this.$scope.onAppEvent('show-json-editor', this.$scope.showJsonEditor);
                    this.$scope.onAppEvent('template-variable-value-updated', this.$scope.templateVariableUpdated);
                    this.$scope.setupDashboard(dashboard);
                };
                return DashboardCtrl;
            })();
            exports_1("DashboardCtrl", DashboardCtrl);
            core_module_1.default.controller('DashboardCtrl', DashboardCtrl);
        }
    }
});
//# sourceMappingURL=dashboard_ctrl.js.map