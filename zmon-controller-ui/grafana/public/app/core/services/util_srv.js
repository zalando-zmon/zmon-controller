///<reference path="../../headers/common.d.ts" />
System.register(['app/core/core_module', 'app/core/app_events'], function(exports_1) {
    var core_module_1, app_events_1;
    var UtilSrv;
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            },
            function (app_events_1_1) {
                app_events_1 = app_events_1_1;
            }],
        execute: function() {
            UtilSrv = (function () {
                /** @ngInject */
                function UtilSrv($rootScope, $modal) {
                    this.$rootScope = $rootScope;
                    this.$modal = $modal;
                }
                UtilSrv.prototype.init = function () {
                    app_events_1.default.on('show-modal', this.showModal.bind(this), this.$rootScope);
                };
                UtilSrv.prototype.showModal = function (options) {
                    if (options.model) {
                        options.scope = this.$rootScope.$new();
                        options.scope.model = options.model;
                    }
                    var modal = this.$modal({
                        modalClass: options.modalClass,
                        template: options.src,
                        templateHtml: options.templateHtml,
                        persist: false,
                        show: false,
                        scope: options.scope,
                        keyboard: false,
                        backdrop: options.backdrop
                    });
                    Promise.resolve(modal).then(function (modalEl) {
                        modalEl.modal('show');
                    });
                };
                return UtilSrv;
            })();
            exports_1("UtilSrv", UtilSrv);
            core_module_1.default.service('utilSrv', UtilSrv);
        }
    }
});
//# sourceMappingURL=util_srv.js.map