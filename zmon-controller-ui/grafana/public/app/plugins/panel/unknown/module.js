///<reference path="../../../headers/common.d.ts" />
System.register(['app/plugins/sdk'], function(exports_1) {
    var __extends = (this && this.__extends) || function (d, b) {
        for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
    var sdk_1;
    var UnknownPanelCtrl;
    return {
        setters:[
            function (sdk_1_1) {
                sdk_1 = sdk_1_1;
            }],
        execute: function() {
            UnknownPanelCtrl = (function (_super) {
                __extends(UnknownPanelCtrl, _super);
                /** @ngInject */
                function UnknownPanelCtrl($scope, $injector) {
                    _super.call(this, $scope, $injector);
                }
                UnknownPanelCtrl.templateUrl = 'public/app/plugins/panel/unknown/module.html';
                return UnknownPanelCtrl;
            })(sdk_1.PanelCtrl);
            exports_1("UnknownPanelCtrl", UnknownPanelCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map