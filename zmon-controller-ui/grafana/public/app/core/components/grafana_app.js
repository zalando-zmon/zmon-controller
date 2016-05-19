///<reference path="../../headers/common.d.ts" />
System.register(['app/core/config', 'app/core/store', 'lodash', 'angular', 'jquery', 'app/core/core_module'], function(exports_1) {
    var config_1, store_1, lodash_1, angular_1, jquery_1, core_module_1;
    var GrafanaCtrl;
    /** @ngInject */
    function grafanaAppDirective(playlistSrv, contextSrv) {
        return {
            restrict: 'E',
            controller: GrafanaCtrl,
            link: function (scope, elem) {
                var ignoreSideMenuHide;
                var body = jquery_1.default('body');
                // handle sidemenu open state
                scope.$watch('contextSrv.sidemenu', function (newVal) {
                    if (newVal !== undefined) {
                        body.toggleClass('sidemenu-open', scope.contextSrv.sidemenu);
                        if (!newVal) {
                            contextSrv.setPinnedState(false);
                        }
                    }
                    if (contextSrv.sidemenu) {
                        ignoreSideMenuHide = true;
                        setTimeout(function () {
                            ignoreSideMenuHide = false;
                        }, 300);
                    }
                });
                scope.$watch('contextSrv.pinned', function (newVal) {
                    if (newVal !== undefined) {
                        body.toggleClass('sidemenu-pinned', newVal);
                    }
                });
                // tooltip removal fix
                // manage page classes
                var pageClass;
                scope.$on("$routeChangeSuccess", function (evt, data) {
                    if (pageClass) {
                        body.removeClass(pageClass);
                    }
                    pageClass = data.$$route.pageClass;
                    if (pageClass) {
                        body.addClass(pageClass);
                    }
                    jquery_1.default("#tooltip, .tooltip").remove();
                });
                // handle document clicks that should hide things
                body.click(function (evt) {
                    var target = jquery_1.default(evt.target);
                    if (target.parents().length === 0) {
                        return;
                    }
                    if (target.parents('.dash-playlist-actions').length === 0) {
                        playlistSrv.stop();
                    }
                    // hide search
                    if (body.find('.search-container').length > 0) {
                        if (target.parents('.search-container').length === 0) {
                            scope.$apply(function () {
                                scope.appEvent('hide-dash-search');
                            });
                        }
                    }
                    // hide sidemenu
                    if (!ignoreSideMenuHide && !contextSrv.pinned && body.find('.sidemenu').length > 0) {
                        if (target.parents('.sidemenu').length === 0) {
                            scope.$apply(function () {
                                scope.contextSrv.toggleSideMenu();
                            });
                        }
                    }
                    // hide popovers
                    var popover = elem.find('.popover');
                    if (popover.length > 0 && target.parents('.graph-legend').length === 0) {
                        popover.hide();
                    }
                });
            }
        };
    }
    exports_1("grafanaAppDirective", grafanaAppDirective);
    return {
        setters:[
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (store_1_1) {
                store_1 = store_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (jquery_1_1) {
                jquery_1 = jquery_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            GrafanaCtrl = (function () {
                /** @ngInject */
                function GrafanaCtrl($scope, alertSrv, utilSrv, $rootScope, $controller, contextSrv) {
                    $scope.init = function () {
                        $scope.contextSrv = contextSrv;
                        $scope._ = lodash_1.default;
                        $rootScope.profilingEnabled = store_1.default.getBool('profilingEnabled');
                        $rootScope.performance = { loadStart: new Date().getTime() };
                        $rootScope.appSubUrl = config_1.default.appSubUrl;
                        if ($rootScope.profilingEnabled) {
                            $scope.initProfiling();
                        }
                        alertSrv.init();
                        utilSrv.init();
                        $scope.dashAlerts = alertSrv;
                    };
                    $scope.initDashboard = function (dashboardData, viewScope) {
                        $controller('DashboardCtrl', { $scope: viewScope }).init(dashboardData);
                    };
                    $rootScope.onAppEvent = function (name, callback, localScope) {
                        var unbind = $rootScope.$on(name, callback);
                        var callerScope = this;
                        if (callerScope.$id === 1 && !localScope) {
                            console.log('warning rootScope onAppEvent called without localscope');
                        }
                        if (localScope) {
                            callerScope = localScope;
                        }
                        callerScope.$on('$destroy', unbind);
                    };
                    $rootScope.appEvent = function (name, payload) {
                        $rootScope.$emit(name, payload);
                    };
                    $rootScope.colors = [
                        "#7EB26D", "#EAB839", "#6ED0E0", "#EF843C", "#E24D42", "#1F78C1", "#BA43A9", "#705DA0",
                        "#508642", "#CCA300", "#447EBC", "#C15C17", "#890F02", "#0A437C", "#6D1F62", "#584477",
                        "#B7DBAB", "#F4D598", "#70DBED", "#F9BA8F", "#F29191", "#82B5D8", "#E5A8E2", "#AEA2E0",
                        "#629E51", "#E5AC0E", "#64B0C8", "#E0752D", "#BF1B00", "#0A50A1", "#962D82", "#614D93",
                        "#9AC48A", "#F2C96D", "#65C5DB", "#F9934E", "#EA6460", "#5195CE", "#D683CE", "#806EB7",
                        "#3F6833", "#967302", "#2F575E", "#99440A", "#58140C", "#052B51", "#511749", "#3F2B5B",
                        "#E0F9D7", "#FCEACA", "#CFFAFF", "#F9E2D2", "#FCE2DE", "#BADFF4", "#F9D9F9", "#DEDAF7"
                    ];
                    $scope.getTotalWatcherCount = function () {
                        var count = 0;
                        var scopes = 0;
                        var root = jquery_1.default(document.getElementsByTagName('body'));
                        var f = function (element) {
                            if (element.data().hasOwnProperty('$scope')) {
                                scopes++;
                                angular_1.default.forEach(element.data().$scope.$$watchers, function () {
                                    count++;
                                });
                            }
                            angular_1.default.forEach(element.children(), function (childElement) {
                                f(jquery_1.default(childElement));
                            });
                        };
                        f(root);
                        $rootScope.performance.scopeCount = scopes;
                        return count;
                    };
                    $scope.initProfiling = function () {
                        var count = 0;
                        $scope.$watch(function digestCounter() {
                            count++;
                        }, function () {
                            // something
                        });
                        $rootScope.performance.panels = [];
                        $scope.$on('refresh', function () {
                            if ($rootScope.performance.panels.length > 0) {
                                var totalRender = 0;
                                var totalQuery = 0;
                                lodash_1.default.each($rootScope.performance.panels, function (panelTiming) {
                                    totalRender += panelTiming.render;
                                    totalQuery += panelTiming.query;
                                });
                                console.log('total query: ' + totalQuery);
                                console.log('total render: ' + totalRender);
                                console.log('avg render: ' + totalRender / $rootScope.performance.panels.length);
                            }
                            $rootScope.performance.panels = [];
                        });
                        $scope.onAppEvent('dashboard-loaded', function () {
                            count = 0;
                            setTimeout(function () {
                                console.log("Dashboard::Performance Total Digests: " + count);
                                console.log("Dashboard::Performance Total Watchers: " + $scope.getTotalWatcherCount());
                                console.log("Dashboard::Performance Total ScopeCount: " + $rootScope.performance.scopeCount);
                                var timeTaken = $rootScope.performance.allPanelsInitialized - $rootScope.performance.dashboardLoadStart;
                                console.log("Dashboard::Performance - All panels initialized in " + timeTaken + " ms");
                                // measure digest performance
                                var rootDigestStart = window.performance.now();
                                for (var i = 0; i < 30; i++) {
                                    $rootScope.$apply();
                                }
                                console.log("Dashboard::Performance Root Digest " + ((window.performance.now() - rootDigestStart) / 30));
                            }, 3000);
                        });
                    };
                    $scope.init();
                }
                return GrafanaCtrl;
            })();
            exports_1("GrafanaCtrl", GrafanaCtrl);
            core_module_1.default.directive('grafanaApp', grafanaAppDirective);
        }
    }
});
//# sourceMappingURL=grafana_app.js.map