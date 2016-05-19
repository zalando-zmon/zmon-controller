///<reference path="../../headers/common.d.ts" />
System.register(['angular'], function(exports_1) {
    var angular_1;
    var directiveModule;
    /** @ngInject */
    function panelEditorTab(dynamicDirectiveSrv) {
        return dynamicDirectiveSrv.create({
            scope: {
                ctrl: "=",
                editorTab: "=",
                index: "=",
            },
            directive: function (scope) {
                var pluginId = scope.ctrl.pluginId;
                var tabIndex = scope.index;
                return Promise.resolve({
                    name: "panel-editor-tab-" + pluginId + tabIndex,
                    fn: scope.editorTab.directiveFn,
                });
            }
        });
    }
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            }],
        execute: function() {
            directiveModule = angular_1.default.module('grafana.directives');
            directiveModule.directive('panelEditorTab', panelEditorTab);
        }
    }
});
//# sourceMappingURL=panel_editor_tab.js.map