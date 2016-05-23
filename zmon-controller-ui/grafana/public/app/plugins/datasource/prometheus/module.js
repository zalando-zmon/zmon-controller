System.register(['./datasource', './query_ctrl'], function(exports_1) {
    var datasource_1, query_ctrl_1;
    var PrometheusConfigCtrl, PrometheusAnnotationsQueryCtrl;
    return {
        setters:[
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            },
            function (query_ctrl_1_1) {
                query_ctrl_1 = query_ctrl_1_1;
            }],
        execute: function() {
            PrometheusConfigCtrl = (function () {
                function PrometheusConfigCtrl() {
                }
                PrometheusConfigCtrl.templateUrl = 'partials/config.html';
                return PrometheusConfigCtrl;
            })();
            PrometheusAnnotationsQueryCtrl = (function () {
                function PrometheusAnnotationsQueryCtrl() {
                }
                PrometheusAnnotationsQueryCtrl.templateUrl = 'partials/annotations.editor.html';
                return PrometheusAnnotationsQueryCtrl;
            })();
            exports_1("Datasource", datasource_1.PrometheusDatasource);
            exports_1("QueryCtrl", query_ctrl_1.PrometheusQueryCtrl);
            exports_1("ConfigCtrl", PrometheusConfigCtrl);
            exports_1("AnnotationsQueryCtrl", PrometheusAnnotationsQueryCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map