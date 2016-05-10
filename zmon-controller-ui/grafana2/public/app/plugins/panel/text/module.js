///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', 'app/plugins/sdk'], function(exports_1) {
    var __extends = (this && this.__extends) || function (d, b) {
        for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
    var lodash_1, sdk_1;
    var TextPanelCtrl;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (sdk_1_1) {
                sdk_1 = sdk_1_1;
            }],
        execute: function() {
            TextPanelCtrl = (function (_super) {
                __extends(TextPanelCtrl, _super);
                /** @ngInject */
                function TextPanelCtrl($scope, $injector, templateSrv, $sce) {
                    _super.call(this, $scope, $injector);
                    this.templateSrv = templateSrv;
                    this.$sce = $sce;
                    // Set and populate defaults
                    this.panelDefaults = {
                        mode: "markdown",
                        content: "# title",
                    };
                    lodash_1.default.defaults(this.panel, this.panelDefaults);
                    this.events.on('init-edit-mode', this.onInitEditMode.bind(this));
                    this.events.on('refresh', this.onRender.bind(this));
                    this.events.on('render', this.onRender.bind(this));
                }
                TextPanelCtrl.prototype.onInitEditMode = function () {
                    this.addEditorTab('Options', 'public/app/plugins/panel/text/editor.html');
                    this.editorTabIndex = 1;
                };
                TextPanelCtrl.prototype.onRender = function () {
                    if (this.panel.mode === 'markdown') {
                        this.renderMarkdown(this.panel.content);
                    }
                    else if (this.panel.mode === 'html') {
                        this.updateContent(this.panel.content);
                    }
                    else if (this.panel.mode === 'text') {
                        this.renderText(this.panel.content);
                    }
                    this.renderingCompleted();
                };
                TextPanelCtrl.prototype.renderText = function (content) {
                    content = content
                        .replace(/&/g, '&amp;')
                        .replace(/>/g, '&gt;')
                        .replace(/</g, '&lt;')
                        .replace(/\n/g, '<br/>');
                    this.updateContent(content);
                };
                TextPanelCtrl.prototype.renderMarkdown = function (content) {
                    var _this = this;
                    if (!this.remarkable) {
                        return System.import('remarkable').then(function (Remarkable) {
                            _this.remarkable = new Remarkable();
                            _this.$scope.$apply(function () {
                                _this.updateContent(_this.remarkable.render(content));
                            });
                        });
                    }
                    this.updateContent(this.remarkable.render(content));
                };
                TextPanelCtrl.prototype.updateContent = function (html) {
                    try {
                        this.content = this.$sce.trustAsHtml(this.templateSrv.replace(html, this.panel.scopedVars));
                    }
                    catch (e) {
                        console.log('Text panel error: ', e);
                        this.content = this.$sce.trustAsHtml(html);
                    }
                };
                TextPanelCtrl.templateUrl = "public/app/plugins/panel/text/module.html";
                return TextPanelCtrl;
            })(sdk_1.PanelCtrl);
            exports_1("TextPanelCtrl", TextPanelCtrl);
            exports_1("PanelCtrl", TextPanelCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map