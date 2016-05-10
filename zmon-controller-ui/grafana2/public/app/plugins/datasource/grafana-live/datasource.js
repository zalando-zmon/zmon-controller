///<reference path="../../../headers/common.d.ts" />
System.register(['app/core/core'], function(exports_1) {
    var core_1;
    var DataObservable, GrafanaStreamDS;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            DataObservable = (function () {
                function DataObservable(target) {
                    this.target = target;
                }
                DataObservable.prototype.subscribe = function (options) {
                    var observable = core_1.liveSrv.subscribe(this.target.stream);
                    return observable.subscribe(function (data) {
                        console.log("grafana stream ds data!", data);
                    });
                };
                return DataObservable;
            })();
            GrafanaStreamDS = (function () {
                /** @ngInject */
                function GrafanaStreamDS() {
                }
                GrafanaStreamDS.prototype.query = function (options) {
                    if (options.targets.length === 0) {
                        return Promise.resolve({ data: [] });
                    }
                    var target = options.targets[0];
                    var observable = new DataObservable(target);
                    return Promise.resolve(observable);
                };
                return GrafanaStreamDS;
            })();
            exports_1("GrafanaStreamDS", GrafanaStreamDS);
        }
    }
});
//# sourceMappingURL=datasource.js.map