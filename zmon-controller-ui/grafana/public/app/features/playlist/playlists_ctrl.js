///<reference path="../../headers/common.d.ts" />
System.register(['lodash', '../../core/core_module'], function(exports_1) {
    var lodash_1, core_module_1;
    var PlaylistsCtrl;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            PlaylistsCtrl = (function () {
                /** @ngInject */
                function PlaylistsCtrl($scope, $location, backendSrv) {
                    var _this = this;
                    this.$scope = $scope;
                    this.$location = $location;
                    this.backendSrv = backendSrv;
                    backendSrv.get('/api/playlists')
                        .then(function (result) {
                        _this.playlists = result;
                    });
                }
                PlaylistsCtrl.prototype.removePlaylistConfirmed = function (playlist) {
                    var _this = this;
                    lodash_1.default.remove(this.playlists, { id: playlist.id });
                    this.backendSrv.delete('/api/playlists/' + playlist.id)
                        .then(function () {
                        _this.$scope.appEvent('alert-success', ['Playlist deleted', '']);
                    }, function () {
                        _this.$scope.appEvent('alert-error', ['Unable to delete playlist', '']);
                        _this.playlists.push(playlist);
                    });
                };
                PlaylistsCtrl.prototype.removePlaylist = function (playlist) {
                    var _this = this;
                    this.$scope.appEvent('confirm-modal', {
                        title: 'Delete',
                        text: 'Are you sure you want to delete playlist ' + playlist.name + '?',
                        yesText: "Delete",
                        icon: "fa-trash",
                        onConfirm: function () {
                            _this.removePlaylistConfirmed(playlist);
                        }
                    });
                };
                return PlaylistsCtrl;
            })();
            exports_1("PlaylistsCtrl", PlaylistsCtrl);
            core_module_1.default.controller('PlaylistsCtrl', PlaylistsCtrl);
        }
    }
});
//# sourceMappingURL=playlists_ctrl.js.map