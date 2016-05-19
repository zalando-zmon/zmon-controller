///<reference path="../../../headers/common.d.ts" />
System.register(['./datasource', 'app/plugins/sdk'], function(exports_1) {
    var __extends = (this && this.__extends) || function (d, b) {
        for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
    var datasource_1, sdk_1;
    var GrafanaQueryCtrl;
    return {
        setters:[
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            },
            function (sdk_1_1) {
                sdk_1 = sdk_1_1;
            }],
        execute: function() {
            GrafanaQueryCtrl = (function (_super) {
                __extends(GrafanaQueryCtrl, _super);
                function GrafanaQueryCtrl() {
                    _super.apply(this, arguments);
                }
                GrafanaQueryCtrl.templateUrl = 'partials/query.editor.html';
                return GrafanaQueryCtrl;
            })(sdk_1.QueryCtrl);
            exports_1("Datasource", datasource_1.GrafanaStreamDS);
            exports_1("QueryCtrl", GrafanaQueryCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map