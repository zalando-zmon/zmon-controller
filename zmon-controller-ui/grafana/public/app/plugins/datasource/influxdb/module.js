System.register(['./datasource', './query_ctrl'], function(exports_1) {
    var datasource_1, query_ctrl_1;
    var InfluxConfigCtrl, InfluxQueryOptionsCtrl, InfluxAnnotationsQueryCtrl;
    return {
        setters:[
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            },
            function (query_ctrl_1_1) {
                query_ctrl_1 = query_ctrl_1_1;
            }],
        execute: function() {
            InfluxConfigCtrl = (function () {
                function InfluxConfigCtrl() {
                }
                InfluxConfigCtrl.templateUrl = 'partials/config.html';
                return InfluxConfigCtrl;
            })();
            InfluxQueryOptionsCtrl = (function () {
                function InfluxQueryOptionsCtrl() {
                }
                InfluxQueryOptionsCtrl.templateUrl = 'partials/query.options.html';
                return InfluxQueryOptionsCtrl;
            })();
            InfluxAnnotationsQueryCtrl = (function () {
                function InfluxAnnotationsQueryCtrl() {
                }
                InfluxAnnotationsQueryCtrl.templateUrl = 'partials/annotations.editor.html';
                return InfluxAnnotationsQueryCtrl;
            })();
            exports_1("Datasource", datasource_1.default);
            exports_1("QueryCtrl", query_ctrl_1.InfluxQueryCtrl);
            exports_1("ConfigCtrl", InfluxConfigCtrl);
            exports_1("QueryOptionsCtrl", InfluxQueryOptionsCtrl);
            exports_1("AnnotationsQueryCtrl", InfluxAnnotationsQueryCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map