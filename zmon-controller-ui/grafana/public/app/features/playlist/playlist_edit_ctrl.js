///<reference path="../../headers/common.d.ts" />
System.register(['lodash', '../../core/core_module'], function(exports_1) {
    var lodash_1, core_module_1;
    var PlaylistEditCtrl;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            PlaylistEditCtrl = (function () {
                /** @ngInject */
                function PlaylistEditCtrl($scope, playlistSrv, backendSrv, $location, $route) {
                    var _this = this;
                    this.$scope = $scope;
                    this.playlistSrv = playlistSrv;
                    this.backendSrv = backendSrv;
                    this.$location = $location;
                    this.$route = $route;
                    this.filteredDashboards = [];
                    this.filteredTags = [];
                    this.searchQuery = '';
                    this.loading = false;
                    this.playlist = {
                        interval: '5m',
                    };
                    this.playlistItems = [];
                    this.dashboardresult = [];
                    this.tagresult = [];
                    if ($route.current.params.id) {
                        var playlistId = $route.current.params.id;
                        backendSrv.get('/api/playlists/' + playlistId)
                            .then(function (result) {
                            _this.playlist = result;
                        });
                        backendSrv.get('/api/playlists/' + playlistId + '/items')
                            .then(function (result) {
                            _this.playlistItems = result;
                        });
                    }
                }
                PlaylistEditCtrl.prototype.filterFoundPlaylistItems = function () {
                    var _this = this;
                    this.filteredDashboards = lodash_1.default.reject(this.dashboardresult, function (playlistItem) {
                        return lodash_1.default.findWhere(_this.playlistItems, function (listPlaylistItem) {
                            return parseInt(listPlaylistItem.value) === playlistItem.id;
                        });
                    });
                    this.filteredTags = lodash_1.default.reject(this.tagresult, function (tag) {
                        return lodash_1.default.findWhere(_this.playlistItems, function (listPlaylistItem) {
                            return listPlaylistItem.value === tag.term;
                        });
                    });
                };
                PlaylistEditCtrl.prototype.addPlaylistItem = function (playlistItem) {
                    playlistItem.value = playlistItem.id.toString();
                    playlistItem.type = 'dashboard_by_id';
                    playlistItem.order = this.playlistItems.length + 1;
                    this.playlistItems.push(playlistItem);
                    this.filterFoundPlaylistItems();
                };
                PlaylistEditCtrl.prototype.addTagPlaylistItem = function (tag) {
                    var playlistItem = {
                        value: tag.term,
                        type: 'dashboard_by_tag',
                        order: this.playlistItems.length + 1,
                        title: tag.term
                    };
                    this.playlistItems.push(playlistItem);
                    this.filterFoundPlaylistItems();
                };
                PlaylistEditCtrl.prototype.removePlaylistItem = function (playlistItem) {
                    lodash_1.default.remove(this.playlistItems, function (listedPlaylistItem) {
                        return playlistItem === listedPlaylistItem;
                    });
                    this.filterFoundPlaylistItems();
                };
                ;
                PlaylistEditCtrl.prototype.savePlaylist = function (playlist, playlistItems) {
                    var _this = this;
                    var savePromise;
                    playlist.items = playlistItems;
                    savePromise = playlist.id
                        ? this.backendSrv.put('/api/playlists/' + playlist.id, playlist)
                        : this.backendSrv.post('/api/playlists', playlist);
                    savePromise
                        .then(function () {
                        _this.$scope.appEvent('alert-success', ['Playlist saved', '']);
                        _this.$location.path('/playlists');
                    }, function () {
                        _this.$scope.appEvent('alert-error', ['Unable to save playlist', '']);
                    });
                };
                PlaylistEditCtrl.prototype.isNew = function () {
                    return !this.playlist.id;
                };
                PlaylistEditCtrl.prototype.isPlaylistEmpty = function () {
                    return !this.playlistItems.length;
                };
                PlaylistEditCtrl.prototype.backToList = function () {
                    this.$location.path('/playlists');
                };
                PlaylistEditCtrl.prototype.searchStarted = function (promise) {
                    var _this = this;
                    promise.then(function (data) {
                        _this.dashboardresult = data.dashboardResult;
                        _this.tagresult = data.tagResult;
                        _this.filterFoundPlaylistItems();
                    });
                };
                PlaylistEditCtrl.prototype.movePlaylistItem = function (playlistItem, offset) {
                    var currentPosition = this.playlistItems.indexOf(playlistItem);
                    var newPosition = currentPosition + offset;
                    if (newPosition >= 0 && newPosition < this.playlistItems.length) {
                        this.playlistItems.splice(currentPosition, 1);
                        this.playlistItems.splice(newPosition, 0, playlistItem);
                    }
                };
                PlaylistEditCtrl.prototype.movePlaylistItemUp = function (playlistItem) {
                    this.movePlaylistItem(playlistItem, -1);
                };
                PlaylistEditCtrl.prototype.movePlaylistItemDown = function (playlistItem) {
                    this.movePlaylistItem(playlistItem, 1);
                };
                return PlaylistEditCtrl;
            })();
            exports_1("PlaylistEditCtrl", PlaylistEditCtrl);
            core_module_1.default.controller('PlaylistEditCtrl', PlaylistEditCtrl);
        }
    }
});
//# sourceMappingURL=playlist_edit_ctrl.js.map