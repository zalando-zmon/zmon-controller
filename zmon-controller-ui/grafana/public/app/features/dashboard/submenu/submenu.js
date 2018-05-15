///<reference path="../../../headers/common.d.ts" />
System.register(['angular'], function(exports_1) {
    var angular_1;
    var SubmenuCtrl;
    function submenuDirective() {
        return {
            restrict: 'E',
            templateUrl: 'public/app/features/dashboard/submenu/submenu.html',
            controller: SubmenuCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
            scope: {
                dashboard: "=",
            }
        };
    }
    exports_1("submenuDirective", submenuDirective);
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            }],
        execute: function() {
            SubmenuCtrl = (function () {
                /** @ngInject */
                function SubmenuCtrl($rootScope, templateValuesSrv, templateSrv, $location) {
                    this.$rootScope = $rootScope;
                    this.templateValuesSrv = templateValuesSrv;
                    this.templateSrv = templateSrv;
                    this.$location = $location;
                    this.annotations = this.dashboard.templating.list;
                    this.variables = this.dashboard.templating.list;
                }
                SubmenuCtrl.prototype.disableAnnotation = function (annotation) {
                    annotation.enable = !annotation.enable;
                    this.$rootScope.$broadcast('refresh');
                };
                SubmenuCtrl.prototype.getValuesForTag = function (variable, tagKey) {
                    return this.templateValuesSrv.getValuesForTag(variable, tagKey);
                };
                SubmenuCtrl.prototype.variableUpdated = function (variable) {
                    var _this = this;
                    this.templateValuesSrv.variableUpdated(variable).then(function () {
                        _this.$rootScope.$emit('template-variable-value-updated');
                        _this.$rootScope.$broadcast('refresh');
                    });
                };
                return SubmenuCtrl;
            })();
            exports_1("SubmenuCtrl", SubmenuCtrl);
            angular_1.default.module('grafana.directives').directive('dashboardSubmenu', submenuDirective);
        }
    }
});
//# sourceMappingURL=submenu.js.map