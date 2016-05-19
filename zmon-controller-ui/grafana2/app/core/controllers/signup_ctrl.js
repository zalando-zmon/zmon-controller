///<reference path="../../headers/common.d.ts" />
define(["require", "exports", 'app/core/config', '../core_module'], function (require, exports, config, coreModule) {
    var SignUpCtrl = (function () {
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
                    window.location.href = config.appSubUrl + '/profile/select-org?signup=1';
                }
                else {
                    window.location.href = config.appSubUrl + '/';
                }
            });
        };
        ;
        return SignUpCtrl;
    })();
    exports.SignUpCtrl = SignUpCtrl;
    coreModule.controller('SignUpCtrl', SignUpCtrl);
});
//# sourceMappingURL=signup_ctrl.js.map