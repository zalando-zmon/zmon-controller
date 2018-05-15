///<reference path="../../../headers/common.d.ts" />
System.register(['app/core/config', 'lodash', '../../core_module'], function(exports_1) {
    var config_1, lodash_1, core_module_1;
    var SearchCtrl;
    function searchDirective() {
        return {
            restrict: 'E',
            templateUrl: 'public/app/core/components/search/search.html',
            controller: SearchCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
        };
    }
    exports_1("searchDirective", searchDirective);
    return {
        setters:[
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            SearchCtrl = (function () {
                /** @ngInject */
                function SearchCtrl($scope, $location, $timeout, backendSrv, contextSrv, $rootScope) {
                    this.$scope = $scope;
                    this.$location = $location;
                    this.$timeout = $timeout;
                    this.backendSrv = backendSrv;
                    this.contextSrv = contextSrv;
                    this.$rootScope = $rootScope;
                    $rootScope.onAppEvent('show-dash-search', this.openSearch.bind(this), $scope);
                    $rootScope.onAppEvent('hide-dash-search', this.closeSearch.bind(this), $scope);
                }
                SearchCtrl.prototype.closeSearch = function () {
                    this.isOpen = this.ignoreClose;
                };
                SearchCtrl.prototype.openSearch = function () {
                    var _this = this;
                    if (this.isOpen) {
                        this.isOpen = false;
                        return;
                    }
                    this.isOpen = true;
                    this.giveSearchFocus = 0;
                    this.selectedIndex = -1;
                    this.results = [];
                    this.query = { query: '', tag: [], starred: false };
                    this.currentSearchId = 0;
                    this.ignoreClose = true;
                    this.$timeout(function () {
                        _this.ignoreClose = false;
                        _this.giveSearchFocus = _this.giveSearchFocus + 1;
                        _this.query.query = '';
                        _this.search();
                    }, 100);
                };
                SearchCtrl.prototype.keyDown = function (evt) {
                    if (evt.keyCode === 27) {
                        this.closeSearch();
                    }
                    if (evt.keyCode === 40) {
                        this.moveSelection(1);
                    }
                    if (evt.keyCode === 38) {
                        this.moveSelection(-1);
                    }
                    if (evt.keyCode === 13) {
                        if (this.$scope.tagMode) {
                            var tag = this.results[this.selectedIndex];
                            if (tag) {
                                this.filterByTag(tag.term, null);
                            }
                            return;
                        }
                        var selectedDash = this.results[this.selectedIndex];
                        if (selectedDash) {
                            this.$location.search({});
                            this.$location.path(selectedDash.url);
                        }
                    }
                };
                SearchCtrl.prototype.moveSelection = function (direction) {
                    var max = (this.results || []).length;
                    var newIndex = this.selectedIndex + direction;
                    this.selectedIndex = ((newIndex %= max) < 0) ? newIndex + max : newIndex;
                };
                SearchCtrl.prototype.searchDashboards = function () {
                    var _this = this;
                    this.tagsMode = false;
                    this.currentSearchId = this.currentSearchId + 1;
                    var localSearchId = this.currentSearchId;
                    return this.backendSrv.search(this.query).then(function (results) {
                        if (localSearchId < _this.currentSearchId) {
                            return;
                        }
                        _this.results = lodash_1.default.map(results, function (dash) {
                            dash.url = 'dashboard/' + dash.uri;
                            return dash;
                        });
                        if (_this.queryHasNoFilters()) {
                            _this.results.unshift({ title: 'Home', url: config_1.default.appSubUrl + '/', type: 'dash-home' });
                        }
                    });
                };
                SearchCtrl.prototype.queryHasNoFilters = function () {
                    var query = this.query;
                    return query.query === '' && query.starred === false && query.tag.length === 0;
                };
                ;
                SearchCtrl.prototype.filterByTag = function (tag, evt) {
                    this.query.tag.push(tag);
                    this.search();
                    this.giveSearchFocus = this.giveSearchFocus + 1;
                    if (evt) {
                        evt.stopPropagation();
                        evt.preventDefault();
                    }
                };
                ;
                SearchCtrl.prototype.removeTag = function (tag, evt) {
                    this.query.tag = lodash_1.default.without(this.query.tag, tag);
                    this.search();
                    this.giveSearchFocus = this.giveSearchFocus + 1;
                    evt.stopPropagation();
                    evt.preventDefault();
                };
                ;
                SearchCtrl.prototype.getTags = function () {
                    var _this = this;
                    return this.backendSrv.get('/api/dashboards/tags').then(function (results) {
                        _this.tagsMode = !_this.tagsMode;
                        _this.results = results;
                        _this.giveSearchFocus = _this.giveSearchFocus + 1;
                        if (!_this.tagsMode) {
                            _this.search();
                        }
                    });
                };
                ;
                SearchCtrl.prototype.showStarred = function () {
                    this.query.starred = !this.query.starred;
                    this.giveSearchFocus = this.giveSearchFocus + 1;
                    this.search();
                };
                ;
                SearchCtrl.prototype.search = function () {
                    this.showImport = false;
                    this.selectedIndex = 0;
                    this.searchDashboards();
                };
                ;
                return SearchCtrl;
            })();
            exports_1("SearchCtrl", SearchCtrl);
            core_module_1.default.directive('dashboardSearch', searchDirective);
        }
    }
});
//# sourceMappingURL=search.js.map