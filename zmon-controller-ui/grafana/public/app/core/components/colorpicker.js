///<reference path="../../headers/common.d.ts" />
System.register(['app/core/core_module'], function(exports_1) {
    var core_module_1;
    var template, ColorPickerCtrl;
    function colorPicker() {
        return {
            restrict: 'E',
            controller: ColorPickerCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
            template: template,
        };
    }
    exports_1("colorPicker", colorPicker);
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            template = "\n<div class=\"graph-legend-popover\">\n  <div ng-show=\"ctrl.series\" class=\"p-b-1\">\n    <label>Y Axis:</label>\n    <button ng-click=\"ctrl.toggleAxis(yaxis);\" class=\"btn btn-small\"\n            ng-class=\"{'btn-success': ctrl.series.yaxis === 1,\n                       'btn-inverse': ctrl.series.yaxis === 2}\">\n      Left\n    </button>\n    <button ng-click=\"ctrl.toggleAxis(yaxis);\"\n      class=\"btn btn-small\"\n      ng-class=\"{'btn-success': ctrl.series.yaxis === 2,\n                 'btn-inverse': ctrl.series.yaxis === 1}\">\n      Right\n    </button>\n  </div>\n\n  <p class=\"m-b-0\">\n   <i ng-repeat=\"color in ctrl.colors\" class=\"pointer fa fa-circle\"\n    ng-style=\"{color:color}\"\n    ng-click=\"ctrl.colorSelected(color);\">&nbsp;</i>\n  </p>\n</div>\n";
            ColorPickerCtrl = (function () {
                /** @ngInject */
                function ColorPickerCtrl($scope, $rootScope) {
                    this.$scope = $scope;
                    this.$rootScope = $rootScope;
                    this.colors = $rootScope.colors;
                    this.autoClose = $scope.autoClose;
                    this.series = $scope.series;
                }
                ColorPickerCtrl.prototype.toggleAxis = function (yaxis) {
                    this.$scope.toggleAxis();
                    if (this.$scope.autoClose) {
                        this.$scope.dismiss();
                    }
                };
                ColorPickerCtrl.prototype.colorSelected = function (color) {
                    this.$scope.colorSelected(color);
                    if (this.$scope.autoClose) {
                        this.$scope.dismiss();
                    }
                };
                return ColorPickerCtrl;
            })();
            exports_1("ColorPickerCtrl", ColorPickerCtrl);
            core_module_1.default.directive('gfColorPicker', colorPicker);
        }
    }
});
//# sourceMappingURL=colorpicker.js.map