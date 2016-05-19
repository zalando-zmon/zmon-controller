///<reference path="../../../headers/common.d.ts" />
System.register(['./datasource'], function(exports_1) {
    var datasource_1;
    return {
        setters:[
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            }],
        execute: function() {
            exports_1("MixedDatasource", datasource_1.MixedDatasource);
            exports_1("Datasource", datasource_1.MixedDatasource);
        }
    }
});
//# sourceMappingURL=module.js.map