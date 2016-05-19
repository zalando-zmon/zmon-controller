///<reference path="../../headers/common.d.ts" />
System.register(['app/core/config', 'lodash', 'app/core/core_module', 'app/core/store'], function(exports_1) {
    var config_1, lodash_1, core_module_1, store_1;
    var User, ContextSrv, contextSrv;
    return {
        setters:[
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            },
            function (store_1_1) {
                store_1 = store_1_1;
            }],
        execute: function() {
            User = (function () {
                function User() {
                    if (config_1.default.bootData.user) {
                        lodash_1.default.extend(this, config_1.default.bootData.user);
                    }
                }
                return User;
            })();
            exports_1("User", User);
            ContextSrv = (function () {
                function ContextSrv() {
                    this.pinned = store_1.default.getBool('grafana.sidemenu.pinned', false);
                    if (this.pinned) {
                        this.sidemenu = true;
                    }
                    if (!config_1.default.buildInfo) {
                        config_1.default.buildInfo = {};
                    }
                    if (!config_1.default.bootData) {
                        config_1.default.bootData = { user: {}, settings: {} };
                    }
                    this.version = config_1.default.buildInfo.version;
                    this.user = new User();
                    this.isSignedIn = this.user.isSignedIn;
                    this.isGrafanaAdmin = this.user.isGrafanaAdmin;
                    this.isEditor = this.hasRole('Editor') || this.hasRole('Admin');
                }
                ContextSrv.prototype.hasRole = function (role) {
                    return this.user.orgRole === role;
                };
                ContextSrv.prototype.setPinnedState = function (val) {
                    this.pinned = val;
                    store_1.default.set('grafana.sidemenu.pinned', val);
                };
                ContextSrv.prototype.toggleSideMenu = function () {
                    this.sidemenu = !this.sidemenu;
                    if (!this.sidemenu) {
                        this.setPinnedState(false);
                    }
                };
                return ContextSrv;
            })();
            exports_1("ContextSrv", ContextSrv);
            contextSrv = new ContextSrv();
            exports_1("contextSrv", contextSrv);
            core_module_1.default.factory('contextSrv', function () {
                return contextSrv;
            });
        }
    }
});
//# sourceMappingURL=context_srv.js.map