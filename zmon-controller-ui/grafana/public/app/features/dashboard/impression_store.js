///<reference path="../../headers/common.d.ts" />
System.register(['app/core/store', 'lodash', 'app/core/config'], function(exports_1) {
    var store_1, lodash_1, config_1;
    var ImpressionsStore, impressions;
    return {
        setters:[
            function (store_1_1) {
                store_1 = store_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (config_1_1) {
                config_1 = config_1_1;
            }],
        execute: function() {
            ImpressionsStore = (function () {
                function ImpressionsStore() {
                }
                ImpressionsStore.prototype.addDashboardImpression = function (dashboardId) {
                    var impressionsKey = this.impressionKey(config_1.default);
                    var impressions = [];
                    if (store_1.default.exists(impressionsKey)) {
                        impressions = JSON.parse(store_1.default.get(impressionsKey));
                        if (!lodash_1.default.isArray(impressions)) {
                            impressions = [];
                        }
                    }
                    impressions = impressions.filter(function (imp) {
                        return dashboardId !== imp;
                    });
                    impressions.unshift(dashboardId);
                    if (impressions.length > 50) {
                        impressions.pop();
                    }
                    store_1.default.set(impressionsKey, JSON.stringify(impressions));
                };
                ImpressionsStore.prototype.getDashboardOpened = function () {
                    var impressions = store_1.default.get(this.impressionKey(config_1.default)) || "[]";
                    impressions = JSON.parse(impressions);
                    impressions = lodash_1.default.filter(impressions, function (el) {
                        return lodash_1.default.isNumber(el);
                    });
                    return impressions;
                };
                ImpressionsStore.prototype.impressionKey = function (config) {
                    return "dashboard_impressions-" + config.bootData.user.orgId;
                };
                return ImpressionsStore;
            })();
            exports_1("ImpressionsStore", ImpressionsStore);
            impressions = new ImpressionsStore();
            exports_1("impressions", impressions);
        }
    }
});
//# sourceMappingURL=impression_store.js.map