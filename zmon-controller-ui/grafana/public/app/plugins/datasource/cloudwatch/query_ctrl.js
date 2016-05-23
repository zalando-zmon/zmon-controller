///<reference path="../../../headers/common.d.ts" />
System.register(['./query_parameter_ctrl', 'app/plugins/sdk'], function(exports_1) {
    var __extends = (this && this.__extends) || function (d, b) {
        for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
    var sdk_1;
    var CloudWatchQueryCtrl;
    return {
        setters:[
            function (_1) {},
            function (sdk_1_1) {
                sdk_1 = sdk_1_1;
            }],
        execute: function() {
            CloudWatchQueryCtrl = (function (_super) {
                __extends(CloudWatchQueryCtrl, _super);
                /** @ngInject **/
                function CloudWatchQueryCtrl($scope, $injector) {
                    _super.call(this, $scope, $injector);
                    this.aliasSyntax = '{{metric}} {{stat}} {{namespace}} {{region}} {{<dimension name>}}';
                }
                CloudWatchQueryCtrl.templateUrl = 'partials/query.editor.html';
                return CloudWatchQueryCtrl;
            })(sdk_1.QueryCtrl);
            exports_1("CloudWatchQueryCtrl", CloudWatchQueryCtrl);
        }
    }
});
//# sourceMappingURL=query_ctrl.js.map