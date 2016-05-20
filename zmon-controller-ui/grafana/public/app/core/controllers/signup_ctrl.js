///<reference path="../../headers/common.d.ts" />
System.register(['app/core/config', '../core_module'], function(exports_1) {
    var config_1, core_module_1;
    var SignUpCtrl;
    return {
        setters:[
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            SignUpCtrl = (function () {
                /** @ngInject */
                function SignUpCtrl($scope, $location, contextSrv, backendSrv) {
                    this.$scope = $scope;
                    this.$location = $location;
                    this.contextSrv = contextSrv;
                    this.backendSrv = backendSrv;
                    contextSrv.sidemenu = false;
                    $scope.ctrl = this;
                    $scope.formModel = {};
                    var params = $location.search();
                    $scope.formModel.orgName = params.email;
                    $scope.formModel.email = params.email;
                    $scope.formModel.username = params.email;
                    $scope.formModel.code = params.code;
                    $scope.verifyEmailEnabled = false;
                    $scope.autoAssignOrg = false;
                    backendSrv.get('/api/user/signup/options').then(function (options) {
                        $scope.verifyEmailEnabled = options.verifyEmailEnabled;
                        $scope.autoAssignOrg = options.autoAssignOrg;
                    });
                }
                SignUpCtrl.prototype.submit = function () {
                    if (!this.$scope.signUpForm.$valid) {
                        return;
                    }
                    this.backendSrv.post('/api/user/signup/step2', this.$scope.formModel).then(function (rsp) {
                        if (rsp.code === 'redirect-to-select-org') {
                            window.location.href = config_1.default.appSubUrl + '/profile/select-org?signup=1';
                        }
                        else {
                            window.location.href = config_1.default.appSubUrl + '/';
                        }
                    });
                };
                ;
                return SignUpCtrl;
            })();
            exports_1("SignUpCtrl", SignUpCtrl);
            core_module_1.default.controller('SignUpCtrl', SignUpCtrl);
        }
    }
});
//# sourceMappingURL=signup_ctrl.js.map