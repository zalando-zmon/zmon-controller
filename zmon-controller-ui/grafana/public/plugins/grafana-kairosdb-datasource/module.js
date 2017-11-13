System.register(['./datasource', './query_ctrl'], function(exports_1) {
    var datasource_1, query_ctrl_1;
    var KairosDBConfigCtrl, KairosDBQueryOptionsCtrl;
    return {
        setters:[
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            },
            function (query_ctrl_1_1) {
                query_ctrl_1 = query_ctrl_1_1;
            }],
        execute: function() {
            KairosDBConfigCtrl = (function () {
                function KairosDBConfigCtrl() {
                }
                KairosDBConfigCtrl.templateUrl = "partials/config.html";
                return KairosDBConfigCtrl;
            })();
            KairosDBQueryOptionsCtrl = (function () {
                function KairosDBQueryOptionsCtrl() {
                }
                KairosDBQueryOptionsCtrl.templateUrl = "partials/query.options.html";
                return KairosDBQueryOptionsCtrl;
            })();
            exports_1("Datasource", datasource_1.KairosDBDatasource);
            exports_1("QueryCtrl", query_ctrl_1.KairosDBQueryCtrl);
            exports_1("ConfigCtrl", KairosDBConfigCtrl);
            exports_1("QueryOptionsCtrl", KairosDBQueryOptionsCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map