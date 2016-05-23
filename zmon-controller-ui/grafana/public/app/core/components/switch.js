///<reference path="../../headers/common.d.ts" />
System.register(['app/core/core_module'], function(exports_1) {
    var core_module_1;
    var template, SwitchCtrl;
    function switchDirective() {
        return {
            restrict: 'E',
            controller: SwitchCtrl,
            controllerAs: 'ctrl',
            bindToController: true,
            scope: {
                checked: "=",
                label: "@",
                labelClass: "@",
                tooltip: "@",
                switchClass: "@",
                onChange: "&",
            },
            template: template,
        };
    }
    exports_1("switchDirective", switchDirective);
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            template = "\n<label for=\"check-{{ctrl.id}}\" class=\"gf-form-label {{ctrl.labelClass}} pointer\">\n  {{ctrl.label}}\n  <info-popover mode=\"right-normal\" ng-if=\"ctrl.tooltip\">\n    {{ctrl.tooltip}}\n  </info-popover>\n</label>\n<div class=\"gf-form-switch {{ctrl.switchClass}}\" ng-if=\"ctrl.show\">\n  <input id=\"check-{{ctrl.id}}\" type=\"checkbox\" ng-model=\"ctrl.checked\" ng-change=\"ctrl.internalOnChange()\">\n  <label for=\"check-{{ctrl.id}}\" data-on=\"Yes\" data-off=\"No\"></label>\n</div>\n";
            SwitchCtrl = (function () {
                /** @ngInject */
                function SwitchCtrl($scope, $timeout) {
                    this.$timeout = $timeout;
                    this.show = true;
                    this.id = $scope.$id;
                }
                SwitchCtrl.prototype.internalOnChange = function () {
                    var _this = this;
                    return this.$timeout(function () {
                        return _this.onChange();
                    });
                };
                return SwitchCtrl;
            })();
            exports_1("SwitchCtrl", SwitchCtrl);
            core_module_1.default.directive('gfFormSwitch', switchDirective);
        }
    }
});
//# sourceMappingURL=switch.js.map