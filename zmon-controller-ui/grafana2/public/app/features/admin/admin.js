System.register(['./adminListUsersCtrl', './adminListOrgsCtrl', './adminEditOrgCtrl', './adminEditUserCtrl', 'app/core/core_module'], function(exports_1) {
    var core_module_1;
    var AdminSettingsCtrl, AdminHomeCtrl, AdminStatsCtrl;
    return {
        setters:[
            function (_1) {},
            function (_2) {},
            function (_3) {},
            function (_4) {},
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            AdminSettingsCtrl = (function () {
                /** @ngInject **/
                function AdminSettingsCtrl($scope, backendSrv) {
                    backendSrv.get('/api/admin/settings').then(function (settings) {
                        $scope.settings = settings;
                    });
                }
                return AdminSettingsCtrl;
            })();
            AdminHomeCtrl = (function () {
                /** @ngInject **/
                function AdminHomeCtrl() {
                }
                return AdminHomeCtrl;
            })();
            AdminStatsCtrl = (function () {
                /** @ngInject */
                function AdminStatsCtrl(backendSrv) {
                    var _this = this;
                    backendSrv.get('/api/admin/stats').then(function (stats) {
                        _this.stats = stats;
                    });
                }
                return AdminStatsCtrl;
            })();
            exports_1("AdminStatsCtrl", AdminStatsCtrl);
            core_module_1.default.controller('AdminSettingsCtrl', AdminSettingsCtrl);
            core_module_1.default.controller('AdminHomeCtrl', AdminHomeCtrl);
            core_module_1.default.controller('AdminStatsCtrl', AdminStatsCtrl);
        }
    }
});
//# sourceMappingURL=admin.js.map