///<reference path="../../headers/common.d.ts" />
System.register(['angular', 'lodash', 'app/core/core_module'], function(exports_1) {
    var angular_1, lodash_1, core_module_1;
    var DynamicDashboardSrv;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            DynamicDashboardSrv = (function () {
                function DynamicDashboardSrv() {
                }
                DynamicDashboardSrv.prototype.init = function (dashboard) {
                    if (dashboard.snapshot) {
                        return;
                    }
                    this.process(dashboard, {});
                };
                DynamicDashboardSrv.prototype.update = function (dashboard) {
                    if (dashboard.snapshot) {
                        return;
                    }
                    this.process(dashboard, {});
                };
                DynamicDashboardSrv.prototype.process = function (dashboard, options) {
                    if (dashboard.templating.list.length === 0) {
                        return;
                    }
                    this.dashboard = dashboard;
                    this.iteration = (this.iteration || new Date().getTime()) + 1;
                    var cleanUpOnly = options.cleanUpOnly;
                    var i, j, row, panel;
                    for (i = 0; i < this.dashboard.rows.length; i++) {
                        row = this.dashboard.rows[i];
                        // handle row repeats
                        if (row.repeat) {
                            if (!cleanUpOnly) {
                                this.repeatRow(row, i);
                            }
                        }
                        else if (row.repeatRowId && row.repeatIteration !== this.iteration) {
                            // clean up old left overs
                            this.dashboard.rows.splice(i, 1);
                            i = i - 1;
                            continue;
                        }
                        // repeat panels
                        for (j = 0; j < row.panels.length; j++) {
                            panel = row.panels[j];
                            if (panel.repeat) {
                                if (!cleanUpOnly) {
                                    this.repeatPanel(panel, row);
                                }
                            }
                            else if (panel.repeatPanelId && panel.repeatIteration !== this.iteration) {
                                // clean up old left overs
                                row.panels = lodash_1.default.without(row.panels, panel);
                                j = j - 1;
                            }
                            else if (!lodash_1.default.isEmpty(panel.scopedVars) && panel.repeatIteration !== this.iteration) {
                                panel.scopedVars = {};
                            }
                        }
                    }
                };
                // returns a new row clone or reuses a clone from previous iteration
                DynamicDashboardSrv.prototype.getRowClone = function (sourceRow, repeatIndex, sourceRowIndex) {
                    if (repeatIndex === 0) {
                        return sourceRow;
                    }
                    var i, panel, row, copy;
                    var sourceRowId = sourceRowIndex + 1;
                    // look for row to reuse
                    for (i = 0; i < this.dashboard.rows.length; i++) {
                        row = this.dashboard.rows[i];
                        if (row.repeatRowId === sourceRowId && row.repeatIteration !== this.iteration) {
                            copy = row;
                            break;
                        }
                    }
                    if (!copy) {
                        copy = angular_1.default.copy(sourceRow);
                        this.dashboard.rows.splice(sourceRowIndex + repeatIndex, 0, copy);
                        // set new panel ids
                        for (i = 0; i < copy.panels.length; i++) {
                            panel = copy.panels[i];
                            panel.id = this.dashboard.getNextPanelId();
                        }
                    }
                    copy.repeat = null;
                    copy.repeatRowId = sourceRowId;
                    copy.repeatIteration = this.iteration;
                    return copy;
                };
                // returns a new row clone or reuses a clone from previous iteration
                DynamicDashboardSrv.prototype.repeatRow = function (row, rowIndex) {
                    var _this = this;
                    var variables = this.dashboard.templating.list;
                    var variable = lodash_1.default.findWhere(variables, { name: row.repeat });
                    if (!variable) {
                        return;
                    }
                    var selected, copy, i, panel;
                    if (variable.current.text === 'All') {
                        selected = variable.options.slice(1, variable.options.length);
                    }
                    else {
                        selected = lodash_1.default.filter(variable.options, { selected: true });
                    }
                    lodash_1.default.each(selected, function (option, index) {
                        copy = _this.getRowClone(row, index, rowIndex);
                        copy.scopedVars = {};
                        copy.scopedVars[variable.name] = option;
                        for (i = 0; i < copy.panels.length; i++) {
                            panel = copy.panels[i];
                            panel.scopedVars = {};
                            panel.scopedVars[variable.name] = option;
                            panel.repeatIteration = _this.iteration;
                        }
                    });
                };
                DynamicDashboardSrv.prototype.getPanelClone = function (sourcePanel, row, index) {
                    // if first clone return source
                    if (index === 0) {
                        return sourcePanel;
                    }
                    var i, tmpId, panel, clone;
                    // first try finding an existing clone to use
                    for (i = 0; i < row.panels.length; i++) {
                        panel = row.panels[i];
                        if (panel.repeatIteration !== this.iteration && panel.repeatPanelId === sourcePanel.id) {
                            clone = panel;
                            break;
                        }
                    }
                    if (!clone) {
                        clone = { id: this.dashboard.getNextPanelId() };
                        row.panels.push(clone);
                    }
                    // save id
                    tmpId = clone.id;
                    // copy properties from source
                    angular_1.default.copy(sourcePanel, clone);
                    // restore id
                    clone.id = tmpId;
                    clone.repeatIteration = this.iteration;
                    clone.repeatPanelId = sourcePanel.id;
                    clone.repeat = null;
                    return clone;
                };
                DynamicDashboardSrv.prototype.repeatPanel = function (panel, row) {
                    var _this = this;
                    var variables = this.dashboard.templating.list;
                    var variable = lodash_1.default.findWhere(variables, { name: panel.repeat });
                    if (!variable) {
                        return;
                    }
                    var selected;
                    if (variable.current.text === 'All') {
                        selected = variable.options.slice(1, variable.options.length);
                    }
                    else {
                        selected = lodash_1.default.filter(variable.options, { selected: true });
                    }
                    lodash_1.default.each(selected, function (option, index) {
                        var copy = _this.getPanelClone(panel, row, index);
                        copy.span = Math.max(12 / selected.length, panel.minSpan);
                        copy.scopedVars = copy.scopedVars || {};
                        copy.scopedVars[variable.name] = option;
                    });
                };
                return DynamicDashboardSrv;
            })();
            exports_1("DynamicDashboardSrv", DynamicDashboardSrv);
            core_module_1.default.service('dynamicDashboardSrv', DynamicDashboardSrv);
        }
    }
});
//# sourceMappingURL=dynamic_dashboard_srv.js.map