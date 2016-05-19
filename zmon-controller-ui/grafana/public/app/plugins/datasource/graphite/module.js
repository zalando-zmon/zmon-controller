System.register(['./datasource', './query_ctrl'], function(exports_1) {
    var datasource_1, query_ctrl_1;
    var GraphiteConfigCtrl, GraphiteQueryOptionsCtrl, AnnotationsQueryCtrl;
    return {
        setters:[
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            },
            function (query_ctrl_1_1) {
                query_ctrl_1 = query_ctrl_1_1;
            }],
        execute: function() {
            GraphiteConfigCtrl = (function () {
                function GraphiteConfigCtrl() {
                }
                GraphiteConfigCtrl.templateUrl = 'partials/config.html';
                return GraphiteConfigCtrl;
            })();
            GraphiteQueryOptionsCtrl = (function () {
                function GraphiteQueryOptionsCtrl() {
                }
                GraphiteQueryOptionsCtrl.templateUrl = 'partials/query.options.html';
                return GraphiteQueryOptionsCtrl;
            })();
            AnnotationsQueryCtrl = (function () {
                function AnnotationsQueryCtrl() {
                }
                AnnotationsQueryCtrl.templateUrl = 'partials/annotations.editor.html';
                return AnnotationsQueryCtrl;
            })();
            exports_1("Datasource", datasource_1.GraphiteDatasource);
            exports_1("QueryCtrl", query_ctrl_1.GraphiteQueryCtrl);
            exports_1("ConfigCtrl", GraphiteConfigCtrl);
            exports_1("QueryOptionsCtrl", GraphiteQueryOptionsCtrl);
            exports_1("AnnotationsQueryCtrl", AnnotationsQueryCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map