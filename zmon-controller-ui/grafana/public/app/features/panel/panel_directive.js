///<reference path="../../headers/common.d.ts" />
System.register(['angular', 'jquery'], function(exports_1) {
    var angular_1, jquery_1;
    var module, panelTemplate;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (jquery_1_1) {
                jquery_1 = jquery_1_1;
            }],
        execute: function() {
            module = angular_1.default.module('grafana.directives');
            panelTemplate = "\n  <div class=\"panel-container\" ng-class=\"{'panel-transparent': ctrl.panel.transparent}\">\n    <div class=\"panel-header\">\n      <span class=\"alert-error panel-error small pointer\" ng-if=\"ctrl.error\" ng-click=\"ctrl.openInspector()\">\n        <span data-placement=\"top\" bs-tooltip=\"ctrl.error\">\n          <i class=\"fa fa-exclamation\"></i><span class=\"panel-error-arrow\"></span>\n        </span>\n      </span>\n\n      <span class=\"panel-loading\" ng-show=\"ctrl.loading\">\n        <i class=\"fa fa-spinner fa-spin\"></i>\n      </span>\n\n      <div class=\"panel-title-container drag-handle\" panel-menu></div>\n    </div>\n\n    <div class=\"panel-content\">\n      <ng-transclude></ng-transclude>\n    </div>\n    <panel-resizer></panel-resizer>\n  </div>\n\n  <div class=\"panel-full-edit\" ng-if=\"ctrl.editMode\">\n    <div class=\"tabbed-view tabbed-view--panel-edit\">\n      <div class=\"tabbed-view-header\">\n        <h2 class=\"tabbed-view-title\">\n          {{ctrl.pluginName}}\n        </h2>\n\n        <ul class=\"gf-tabs\">\n          <li class=\"gf-tabs-item\" ng-repeat=\"tab in ::ctrl.editorTabs\">\n            <a class=\"gf-tabs-link\" ng-click=\"ctrl.changeTab($index)\" ng-class=\"{active: ctrl.editorTabIndex === $index}\">\n              {{::tab.title}}\n            </a>\n          </li>\n        </ul>\n\n        <button class=\"tabbed-view-close-btn\" ng-click=\"ctrl.exitFullscreen();\">\n          <i class=\"fa fa-remove\"></i>\n        </button>\n      </div>\n\n      <div class=\"tabbed-view-body\">\n        <div ng-repeat=\"tab in ctrl.editorTabs\" ng-if=\"ctrl.editorTabIndex === $index\">\n          <panel-editor-tab editor-tab=\"tab\" ctrl=\"ctrl\" index=\"$index\"></panel-editor-tab>\n        </div>\n      </div>\n    </div>\n  </div>\n";
            module.directive('grafanaPanel', function () {
                return {
                    restrict: 'E',
                    template: panelTemplate,
                    transclude: true,
                    scope: { ctrl: "=" },
                    link: function (scope, elem) {
                        var panelContainer = elem.find('.panel-container');
                        var ctrl = scope.ctrl;
                        scope.$watchGroup(['ctrl.fullscreen', 'ctrl.containerHeight'], function () {
                            panelContainer.css({ minHeight: ctrl.containerHeight });
                            elem.toggleClass('panel-fullscreen', ctrl.fullscreen ? true : false);
                        });
                    }
                };
            });
            module.directive('panelResizer', function ($rootScope) {
                return {
                    restrict: 'E',
                    template: '<span class="resize-panel-handle"></span>',
                    link: function (scope, elem) {
                        var resizing = false;
                        var lastPanel;
                        var ctrl = scope.ctrl;
                        var handleOffset;
                        var originalHeight;
                        var originalWidth;
                        var maxWidth;
                        function dragStartHandler(e) {
                            e.preventDefault();
                            resizing = true;
                            handleOffset = jquery_1.default(e.target).offset();
                            originalHeight = parseInt(ctrl.row.height);
                            originalWidth = ctrl.panel.span;
                            maxWidth = jquery_1.default(document).width();
                            lastPanel = ctrl.row.panels[ctrl.row.panels.length - 1];
                            jquery_1.default('body').on('mousemove', moveHandler);
                            jquery_1.default('body').on('mouseup', dragEndHandler);
                        }
                        function moveHandler(e) {
                            ctrl.row.height = originalHeight + (e.pageY - handleOffset.top);
                            ctrl.panel.span = originalWidth + (((e.pageX - handleOffset.left) / maxWidth) * 12);
                            ctrl.panel.span = Math.min(Math.max(ctrl.panel.span, 1), 12);
                            var rowSpan = ctrl.dashboard.rowSpan(ctrl.row);
                            // auto adjust other panels
                            if (Math.floor(rowSpan) < 14) {
                                // last panel should not push row down
                                if (lastPanel === ctrl.panel && rowSpan > 12) {
                                    lastPanel.span -= rowSpan - 12;
                                }
                                else if (lastPanel !== ctrl.panel) {
                                    // reduce width of last panel so total in row is 12
                                    lastPanel.span = lastPanel.span - (rowSpan - 12);
                                    lastPanel.span = Math.min(Math.max(lastPanel.span, 1), 12);
                                }
                            }
                            scope.$apply(function () {
                                ctrl.render();
                            });
                        }
                        function dragEndHandler() {
                            // if close to 12
                            var rowSpan = ctrl.dashboard.rowSpan(ctrl.row);
                            if (rowSpan < 12 && rowSpan > 11) {
                                lastPanel.span += 12 - rowSpan;
                            }
                            scope.$apply(function () {
                                $rootScope.$broadcast('render');
                            });
                            jquery_1.default('body').off('mousemove', moveHandler);
                            jquery_1.default('body').off('mouseup', dragEndHandler);
                        }
                        elem.on('mousedown', dragStartHandler);
                        scope.$on("$destroy", function () {
                            elem.off('mousedown', dragStartHandler);
                        });
                    }
                };
            });
        }
    }
});
//# sourceMappingURL=panel_directive.js.map