System.register(['./query_parameter_ctrl', './datasource', './query_ctrl'], function(exports_1) {
    var datasource_1, query_ctrl_1;
    var CloudWatchConfigCtrl, CloudWatchAnnotationsQueryCtrl;
    return {
        setters:[
            function (_1) {},
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            },
            function (query_ctrl_1_1) {
                query_ctrl_1 = query_ctrl_1_1;
            }],
        execute: function() {
            CloudWatchConfigCtrl = (function () {
                function CloudWatchConfigCtrl() {
                }
                CloudWatchConfigCtrl.templateUrl = 'partials/config.html';
                return CloudWatchConfigCtrl;
            })();
            CloudWatchAnnotationsQueryCtrl = (function () {
                function CloudWatchAnnotationsQueryCtrl() {
                }
                CloudWatchAnnotationsQueryCtrl.templateUrl = 'partials/annotations.editor.html';
                return CloudWatchAnnotationsQueryCtrl;
            })();
            exports_1("Datasource", datasource_1.CloudWatchDatasource);
            exports_1("QueryCtrl", query_ctrl_1.CloudWatchQueryCtrl);
            exports_1("ConfigCtrl", CloudWatchConfigCtrl);
            exports_1("AnnotationsQueryCtrl", CloudWatchAnnotationsQueryCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map