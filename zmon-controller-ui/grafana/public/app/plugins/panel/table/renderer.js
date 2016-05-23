///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', 'moment', 'app/core/utils/kbn'], function(exports_1) {
    var lodash_1, moment_1, kbn_1;
    var TableRenderer;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (kbn_1_1) {
                kbn_1 = kbn_1_1;
            }],
        execute: function() {
            TableRenderer = (function () {
                function TableRenderer(panel, table, isUtc) {
                    this.panel = panel;
                    this.table = table;
                    this.isUtc = isUtc;
                    this.formaters = [];
                    this.colorState = {};
                }
                TableRenderer.prototype.getColorForValue = function (value, style) {
                    if (!style.thresholds) {
                        return null;
                    }
                    for (var i = style.thresholds.length; i > 0; i--) {
                        if (value >= style.thresholds[i - 1]) {
                            return style.colors[i];
                        }
                    }
                    return lodash_1.default.first(style.colors);
                };
                TableRenderer.prototype.defaultCellFormater = function (v) {
                    if (v === null || v === void 0 || v === undefined) {
                        return '';
                    }
                    if (lodash_1.default.isArray(v)) {
                        v = v.join(', ');
                    }
                    return v;
                };
                TableRenderer.prototype.createColumnFormater = function (style, column) {
                    var _this = this;
                    if (!style) {
                        return this.defaultCellFormater;
                    }
                    if (style.type === 'date') {
                        return function (v) {
                            if (lodash_1.default.isArray(v)) {
                                v = v[0];
                            }
                            var date = moment_1.default(v);
                            if (_this.isUtc) {
                                date = date.utc();
                            }
                            return date.format(style.dateFormat);
                        };
                    }
                    if (style.type === 'number') {
                        var valueFormater = kbn_1.default.valueFormats[column.unit || style.unit];
                        return function (v) {
                            if (v === null || v === void 0) {
                                return '-';
                            }
                            if (lodash_1.default.isString(v)) {
                                return v;
                            }
                            if (style.colorMode) {
                                _this.colorState[style.colorMode] = _this.getColorForValue(v, style);
                            }
                            return valueFormater(v, style.decimals, null);
                        };
                    }
                    return this.defaultCellFormater;
                };
                TableRenderer.prototype.formatColumnValue = function (colIndex, value) {
                    if (this.formaters[colIndex]) {
                        return this.formaters[colIndex](value);
                    }
                    for (var i = 0; i < this.panel.styles.length; i++) {
                        var style = this.panel.styles[i];
                        var column = this.table.columns[colIndex];
                        var regex = kbn_1.default.stringToJsRegex(style.pattern);
                        if (column.text.match(regex)) {
                            this.formaters[colIndex] = this.createColumnFormater(style, column);
                            return this.formaters[colIndex](value);
                        }
                    }
                    this.formaters[colIndex] = this.defaultCellFormater;
                    return this.formaters[colIndex](value);
                };
                TableRenderer.prototype.renderCell = function (columnIndex, value, addWidthHack) {
                    if (addWidthHack === void 0) { addWidthHack = false; }
                    value = this.formatColumnValue(columnIndex, value);
                    value = lodash_1.default.escape(value);
                    var style = '';
                    if (this.colorState.cell) {
                        style = ' style="background-color:' + this.colorState.cell + ';color: white"';
                        this.colorState.cell = null;
                    }
                    else if (this.colorState.value) {
                        style = ' style="color:' + this.colorState.value + '"';
                        this.colorState.value = null;
                    }
                    // because of the fixed table headers css only solution
                    // there is an issue if header cell is wider the cell
                    // this hack adds header content to cell (not visible)
                    var widthHack = '';
                    if (addWidthHack) {
                        widthHack = '<div class="table-panel-width-hack">' + this.table.columns[columnIndex].text + '</div>';
                    }
                    return '<td' + style + '>' + value + widthHack + '</td>';
                };
                TableRenderer.prototype.render = function (page) {
                    var pageSize = this.panel.pageSize || 100;
                    var startPos = page * pageSize;
                    var endPos = Math.min(startPos + pageSize, this.table.rows.length);
                    var html = "";
                    for (var y = startPos; y < endPos; y++) {
                        var row = this.table.rows[y];
                        var cellHtml = '';
                        var rowStyle = '';
                        for (var i = 0; i < this.table.columns.length; i++) {
                            cellHtml += this.renderCell(i, row[i], y === startPos);
                        }
                        if (this.colorState.row) {
                            rowStyle = ' style="background-color:' + this.colorState.row + ';color: white"';
                            this.colorState.row = null;
                        }
                        html += '<tr ' + rowStyle + '>' + cellHtml + '</tr>';
                    }
                    return html;
                };
                return TableRenderer;
            })();
            exports_1("TableRenderer", TableRenderer);
        }
    }
});
//# sourceMappingURL=renderer.js.map