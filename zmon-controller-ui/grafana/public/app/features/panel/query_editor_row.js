///<reference path="../../headers/common.d.ts" />
System.register(['angular', 'lodash'], function(exports_1) {
    var angular_1, lodash_1;
    var module, QueryRowCtrl;
    /** @ngInject **/
    function queryEditorRowDirective() {
        return {
            restrict: 'E',
            controller: QueryRowCtrl,
            bindToController: true,
            controllerAs: "ctrl",
            templateUrl: 'public/app/features/panel/partials/query_editor_row.html',
            transclude: true,
            scope: {
                queryCtrl: "=",
                canCollapse: "=",
                hasTextEditMode: "=",
            },
        };
    }
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            }],
        execute: function() {
            module = angular_1.default.module('grafana.directives');
            QueryRowCtrl = (function () {
                function QueryRowCtrl() {
                    this.panelCtrl = this.queryCtrl.panelCtrl;
                    this.target = this.queryCtrl.target;
                    this.panel = this.panelCtrl.panel;
                    if (!this.target.refId) {
                        this.target.refId = this.getNextQueryLetter();
                    }
                    this.toggleCollapse(true);
                    if (this.target.isNew) {
                        delete this.target.isNew;
                        this.toggleCollapse(false);
                    }
                }
                QueryRowCtrl.prototype.toggleHideQuery = function () {
                    this.target.hide = !this.target.hide;
                    this.panelCtrl.refresh();
                };
                QueryRowCtrl.prototype.getNextQueryLetter = function () {
                    var _this = this;
                    var letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
                    return lodash_1.default.find(letters, function (refId) {
                        return lodash_1.default.every(_this.panel.targets, function (other) {
                            return other.refId !== refId;
                        });
                    });
                };
                QueryRowCtrl.prototype.toggleCollapse = function (init) {
                    if (!this.canCollapse) {
                        return;
                    }
                    if (!this.panelCtrl.__collapsedQueryCache) {
                        this.panelCtrl.__collapsedQueryCache = {};
                    }
                    if (init) {
                        this.collapsed = this.panelCtrl.__collapsedQueryCache[this.target.refId] !== false;
                    }
                    else {
                        this.collapsed = !this.collapsed;
                        this.panelCtrl.__collapsedQueryCache[this.target.refId] = this.collapsed;
                    }
                    try {
                        this.collapsedText = this.queryCtrl.getCollapsedText();
                    }
                    catch (e) {
                        var err = e.message || e.toString();
                        this.collapsedText = 'Error: ' + err;
                    }
                };
                QueryRowCtrl.prototype.toggleEditorMode = function () {
                    if (this.canCollapse && this.collapsed) {
                        this.collapsed = false;
                    }
                    this.queryCtrl.toggleEditorMode();
                };
                QueryRowCtrl.prototype.removeQuery = function () {
                    if (this.panelCtrl.__collapsedQueryCache) {
                        delete this.panelCtrl.__collapsedQueryCache[this.target.refId];
                    }
                    this.panel.targets = lodash_1.default.without(this.panel.targets, this.target);
                    this.panelCtrl.refresh();
                };
                QueryRowCtrl.prototype.duplicateQuery = function () {
                    var clone = angular_1.default.copy(this.target);
                    clone.refId = this.getNextQueryLetter();
                    this.panel.targets.push(clone);
                };
                QueryRowCtrl.prototype.moveQuery = function (direction) {
                    var index = lodash_1.default.indexOf(this.panel.targets, this.target);
                    lodash_1.default.move(this.panel.targets, index, index + direction);
                };
                return QueryRowCtrl;
            })();
            exports_1("QueryRowCtrl", QueryRowCtrl);
            module.directive('queryEditorRow', queryEditorRowDirective);
        }
    }
});
//# sourceMappingURL=query_editor_row.js.map