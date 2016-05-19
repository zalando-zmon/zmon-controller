///<reference path="../../headers/common.d.ts" />
System.register(['../../core/core_module'], function(exports_1) {
    var core_module_1;
    var PlaylistSearchCtrl;
    function playlistSearchDirective() {
        return {
            restrict: 'E',
            templateUrl: 'public/app/features/playlist/partials/playlist_search.html',
            controller: PlaylistSearchCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
            scope: {
                searchStarted: '&'
            },
        };
    }
    exports_1("playlistSearchDirective", playlistSearchDirective);
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            PlaylistSearchCtrl = (function () {
                /** @ngInject */
                function PlaylistSearchCtrl($scope, $location, $timeout, backendSrv, contextSrv) {
                    var _this = this;
                    this.$scope = $scope;
                    this.$location = $location;
                    this.$timeout = $timeout;
                    this.backendSrv = backendSrv;
                    this.contextSrv = contextSrv;
                    this.query = { query: '', tag: [], starred: false };
                    $timeout(function () {
                        _this.query.query = '';
                        _this.searchDashboards();
                    }, 100);
                }
                PlaylistSearchCtrl.prototype.searchDashboards = function () {
                    this.tagsMode = false;
                    var prom = {};
                    prom.promise = this.backendSrv.search(this.query).then(function (result) {
                        return {
                            dashboardResult: result,
                            tagResult: []
                        };
                    });
                    this.searchStarted(prom);
                };
                PlaylistSearchCtrl.prototype.showStarred = function () {
                    this.query.starred = !this.query.starred;
                    this.searchDashboards();
                };
                PlaylistSearchCtrl.prototype.queryHasNoFilters = function () {
                    return this.query.query === '' && this.query.starred === false && this.query.tag.length === 0;
                };
                PlaylistSearchCtrl.prototype.filterByTag = function (tag, evt) {
                    this.query.tag.push(tag);
                    this.searchDashboards();
                    if (evt) {
                        evt.stopPropagation();
                        evt.preventDefault();
                    }
                };
                PlaylistSearchCtrl.prototype.getTags = function () {
                    var prom = {};
                    prom.promise = this.backendSrv.get('/api/dashboards/tags').then(function (result) {
                        return {
                            dashboardResult: [],
                            tagResult: result
                        };
                    });
                    this.searchStarted(prom);
                };
                return PlaylistSearchCtrl;
            })();
            exports_1("PlaylistSearchCtrl", PlaylistSearchCtrl);
            core_module_1.default.directive('playlistSearch', playlistSearchDirective);
        }
    }
});
//# sourceMappingURL=playlist_search.js.map