///<reference path="../../headers/common.d.ts" />
System.register(['angular', 'lodash'], function(exports_1) {
    var angular_1, lodash_1;
    var SnapshotsCtrl;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            }],
        execute: function() {
            SnapshotsCtrl = (function () {
                /** @ngInject */
                function SnapshotsCtrl($rootScope, backendSrv) {
                    var _this = this;
                    this.$rootScope = $rootScope;
                    this.backendSrv = backendSrv;
                    this.backendSrv.get('/api/dashboard/snapshots').then(function (result) {
                        _this.snapshots = result;
                    });
                }
                SnapshotsCtrl.prototype.removeSnapshotConfirmed = function (snapshot) {
                    var _this = this;
                    lodash_1.default.remove(this.snapshots, { key: snapshot.key });
                    this.backendSrv.get('/api/snapshots-delete/' + snapshot.deleteKey)
                        .then(function () {
                        _this.$rootScope.appEvent('alert-success', ['Snapshot deleted', '']);
                    }, function () {
                        _this.$rootScope.appEvent('alert-error', ['Unable to delete snapshot', '']);
                        _this.snapshots.push(snapshot);
                    });
                };
                SnapshotsCtrl.prototype.removeSnapshot = function (snapshot) {
                    var _this = this;
                    this.$rootScope.appEvent('confirm-modal', {
                        title: 'Delete',
                        text: 'Are you sure you want to delete snapshot ' + snapshot.name + '?',
                        yesText: "Delete",
                        icon: "fa-trash",
                        onConfirm: function () {
                            _this.removeSnapshotConfirmed(snapshot);
                        }
                    });
                };
                return SnapshotsCtrl;
            })();
            exports_1("SnapshotsCtrl", SnapshotsCtrl);
            angular_1.default.module('grafana.controllers').controller('SnapshotsCtrl', SnapshotsCtrl);
        }
    }
});
//# sourceMappingURL=snapshot_ctrl.js.map