///<reference path="../../headers/common.d.ts" />
System.register([], function(exports_1) {
    var BundleLoader;
    return {
        setters:[],
        execute: function() {
            BundleLoader = (function () {
                function BundleLoader(bundleName) {
                    var _this = this;
                    this.lazy = ["$q", "$route", "$rootScope", function ($q, $route, $rootScope) {
                            if (_this.loadingDefer) {
                                return _this.loadingDefer.promise;
                            }
                            _this.loadingDefer = $q.defer();
                            System.import(bundleName).then(function () {
                                _this.loadingDefer.resolve();
                            });
                            return _this.loadingDefer.promise;
                        }];
                }
                return BundleLoader;
            })();
            exports_1("BundleLoader", BundleLoader);
        }
    }
});
//# sourceMappingURL=bundle_loader.js.map