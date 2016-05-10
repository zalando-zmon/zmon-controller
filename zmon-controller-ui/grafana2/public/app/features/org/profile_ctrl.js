///<reference path="../../headers/common.d.ts" />
System.register(['app/core/config', 'app/core/core'], function(exports_1) {
    var config_1, core_1;
    var ProfileCtrl;
    return {
        setters:[
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            ProfileCtrl = (function () {
                /** @ngInject **/
                function ProfileCtrl(backendSrv, contextSrv, $location) {
                    this.backendSrv = backendSrv;
                    this.contextSrv = contextSrv;
                    this.$location = $location;
                    this.getUser();
                    this.getUserOrgs();
                }
                ProfileCtrl.prototype.getUser = function () {
                    var _this = this;
                    this.backendSrv.get('/api/user').then(function (user) {
                        _this.user = user;
                        _this.user.theme = user.theme || 'dark';
                    });
                };
                ProfileCtrl.prototype.getUserOrgs = function () {
                    var _this = this;
                    this.backendSrv.get('/api/user/orgs').then(function (orgs) {
                        _this.orgs = orgs;
                    });
                };
                ProfileCtrl.prototype.setUsingOrg = function (org) {
                    this.backendSrv.post('/api/user/using/' + org.orgId).then(function () {
                        window.location.href = config_1.default.appSubUrl + '/profile';
                    });
                };
                ProfileCtrl.prototype.update = function () {
                    var _this = this;
                    if (!this.userForm.$valid) {
                        return;
                    }
                    this.backendSrv.put('/api/user/', this.user).then(function () {
                        _this.contextSrv.user.name = _this.user.name || _this.user.login;
                        if (_this.old_theme !== _this.user.theme) {
                            window.location.href = config_1.default.appSubUrl + _this.$location.path();
                        }
                    });
                };
                return ProfileCtrl;
            })();
            exports_1("ProfileCtrl", ProfileCtrl);
            core_1.coreModule.controller('ProfileCtrl', ProfileCtrl);
        }
    }
});
//# sourceMappingURL=profile_ctrl.js.map