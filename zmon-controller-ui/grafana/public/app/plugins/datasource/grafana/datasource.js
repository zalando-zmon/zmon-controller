///<reference path="../../../headers/common.d.ts" />
System.register([], function(exports_1) {
    var GrafanaDatasource;
    return {
        setters:[],
        execute: function() {
            GrafanaDatasource = (function () {
                /** @ngInject */
                function GrafanaDatasource(backendSrv) {
                    this.backendSrv = backendSrv;
                }
                GrafanaDatasource.prototype.query = function (options) {
                    return this.backendSrv.get('/api/metrics/test', {
                        from: options.range.from.valueOf(),
                        to: options.range.to.valueOf(),
                        maxDataPoints: options.maxDataPoints
                    });
                };
                return GrafanaDatasource;
            })();
            exports_1("GrafanaDatasource", GrafanaDatasource);
        }
    }
});
//# sourceMappingURL=datasource.js.map