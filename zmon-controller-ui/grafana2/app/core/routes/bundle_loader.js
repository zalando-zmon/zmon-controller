///<reference path="../../headers/require/require.d.ts" />
define(["require", "exports"], function (require, exports) {
    var BundleLoader = (function () {
        function BundleLoader(bundleName) {
            var _this = this;
            this.lazy = ["$q", "$route", "$rootScope", function ($q, $route, $rootScope) {
                    if (_this.loadingDefer) {
                        return _this.loadingDefer.promise;
                    }
                    _this.loadingDefer = $q.defer();
                    require([bundleName], function () {
                        _this.loadingDefer.resolve();
                    });
                    return _this.loadingDefer.promise;
                }];
        }
        return BundleLoader;
    })();
    exports.BundleLoader = BundleLoader;
});
//# sourceMappingURL=bundle_loader.js.map