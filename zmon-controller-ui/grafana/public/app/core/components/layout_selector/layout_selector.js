///<reference path="../../../headers/common.d.ts" />
System.register(['app/core/store', 'app/core/core_module'], function(exports_1) {
    var store_1, core_module_1;
    var template, LayoutSelectorCtrl;
    /** @ngInject **/
    function layoutSelector() {
        return {
            restrict: 'E',
            controller: LayoutSelectorCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
            scope: {},
            template: template,
        };
    }
    exports_1("layoutSelector", layoutSelector);
    /** @ngInject **/
    function layoutMode($rootScope) {
        return {
            restrict: 'A',
            scope: {},
            link: function (scope, elem) {
                var layout = store_1.default.get('grafana.list.layout.mode') || 'grid';
                var className = 'card-list-layout-' + layout;
                elem.addClass(className);
                $rootScope.onAppEvent('layout-mode-changed', function (evt, newLayout) {
                    elem.removeClass(className);
                    className = 'card-list-layout-' + newLayout;
                    elem.addClass(className);
                }, scope);
            }
        };
    }
    exports_1("layoutMode", layoutMode);
    return {
        setters:[
            function (store_1_1) {
                store_1 = store_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            template = "\n<div class=\"layout-selector\">\n  <button ng-click=\"ctrl.listView()\" ng-class=\"{active: ctrl.mode === 'list'}\">\n    <i class=\"fa fa-list\"></i>\n  </button>\n  <button ng-click=\"ctrl.gridView()\" ng-class=\"{active: ctrl.mode === 'grid'}\">\n    <i class=\"fa fa-th\"></i>\n  </button>\n</div>\n";
            LayoutSelectorCtrl = (function () {
                /** @ngInject **/
                function LayoutSelectorCtrl($rootScope) {
                    this.$rootScope = $rootScope;
                    this.mode = store_1.default.get('grafana.list.layout.mode') || 'grid';
                }
                LayoutSelectorCtrl.prototype.listView = function () {
                    this.mode = 'list';
                    store_1.default.set('grafana.list.layout.mode', 'list');
                    this.$rootScope.appEvent('layout-mode-changed', 'list');
                };
                LayoutSelectorCtrl.prototype.gridView = function () {
                    this.mode = 'grid';
                    store_1.default.set('grafana.list.layout.mode', 'grid');
                    this.$rootScope.appEvent('layout-mode-changed', 'grid');
                };
                return LayoutSelectorCtrl;
            })();
            exports_1("LayoutSelectorCtrl", LayoutSelectorCtrl);
            core_module_1.default.directive('layoutSelector', layoutSelector);
            core_module_1.default.directive('layoutMode', layoutMode);
        }
    }
});
//# sourceMappingURL=layout_selector.js.map