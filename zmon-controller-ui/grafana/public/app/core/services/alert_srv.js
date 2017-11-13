///<reference path="../../headers/common.d.ts" />
System.register(['angular', 'lodash', 'app/core/core_module', 'app/core/app_events'], function(exports_1) {
    var angular_1, lodash_1, core_module_1, app_events_1;
    var AlertSrv;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            },
            function (app_events_1_1) {
                app_events_1 = app_events_1_1;
            }],
        execute: function() {
            AlertSrv = (function () {
                /** @ngInject */
                function AlertSrv($timeout, $sce, $rootScope, $modal) {
                    this.$timeout = $timeout;
                    this.$sce = $sce;
                    this.$rootScope = $rootScope;
                    this.$modal = $modal;
                    this.list = [];
                }
                AlertSrv.prototype.init = function () {
                    var _this = this;
                    this.$rootScope.onAppEvent('alert-error', function (e, alert) {
                        _this.set(alert[0], alert[1], 'error', 0);
                    }, this.$rootScope);
                    this.$rootScope.onAppEvent('alert-warning', function (e, alert) {
                        _this.set(alert[0], alert[1], 'warning', 5000);
                    }, this.$rootScope);
                    this.$rootScope.onAppEvent('alert-success', function (e, alert) {
                        _this.set(alert[0], alert[1], 'success', 3000);
                    }, this.$rootScope);
                    app_events_1.default.on('confirm-modal', this.showConfirmModal.bind(this));
                };
                AlertSrv.prototype.set = function (title, text, severity, timeout) {
                    var _this = this;
                    var newAlert = {
                        title: title || '',
                        text: text || '',
                        severity: severity || 'info',
                    };
                    var newAlertJson = angular_1.default.toJson(newAlert);
                    // remove same alert if it already exists
                    lodash_1.default.remove(this.list, function (value) {
                        return angular_1.default.toJson(value) === newAlertJson;
                    });
                    this.list.push(newAlert);
                    if (timeout > 0) {
                        this.$timeout(function () {
                            _this.list = lodash_1.default.without(_this.list, newAlert);
                        }, timeout);
                    }
                    if (!this.$rootScope.$$phase) {
                        this.$rootScope.$digest();
                    }
                    return (newAlert);
                };
                AlertSrv.prototype.clear = function (alert) {
                    this.list = lodash_1.default.without(this.list, alert);
                };
                AlertSrv.prototype.clearAll = function () {
                    this.list = [];
                };
                AlertSrv.prototype.showConfirmModal = function (payload) {
                    var scope = this.$rootScope.$new();
                    scope.title = payload.title;
                    scope.text = payload.text;
                    scope.text2 = payload.text2;
                    scope.onConfirm = payload.onConfirm;
                    scope.onAltAction = payload.onAltAction;
                    scope.altActionText = payload.altActionText;
                    scope.icon = payload.icon || "fa-check";
                    scope.yesText = payload.yesText || "Yes";
                    scope.noText = payload.noText || "Cancel";
                    var confirmModal = this.$modal({
                        template: 'public/app/partials/confirm_modal.html',
                        persist: false,
                        modalClass: 'confirm-modal',
                        show: false,
                        scope: scope,
                        keyboard: false
                    });
                    confirmModal.then(function (modalEl) {
                        modalEl.modal('show');
                    });
                };
                return AlertSrv;
            })();
            exports_1("AlertSrv", AlertSrv);
            core_module_1.default.service('alertSrv', AlertSrv);
        }
    }
});
//# sourceMappingURL=alert_srv.js.map