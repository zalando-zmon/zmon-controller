///<reference path="../../headers/common.d.ts" />
define(["require", "exports", 'lodash', 'app/core/utils/kbn', 'moment'], function (require, exports, _, kbn, moment) {
    var TableRenderer = (function () {
        function TableRenderer(panel, table, timezone) {
            this.panel = panel;
            this.table = table;
            this.timezone = timezone;
            this.formaters = [];
            this.colorState = {};
        }
        TableRenderer.prototype.getColorForValue = function (value, style) {
            if (!style.thresholds) {
                return null;
            }
            for (var i = style.thresholds.length - 1; i >= 0; i--) {
                if (value >= style.thresholds[i]) {
                    return style.colors[i];
                }
            }
            return null;
        };
        TableRenderer.prototype.defaultCellFormater = function (v) {
            if (v === null || v === void 0) {
                return '';
            }
            if (_.isArray(v)) {
                v = v.join(',&nbsp;');
            }
            return v;
        };
        TableRenderer.prototype.createColumnFormater = function (style) {
            var _this = this;
            if (!style) {
                return this.defaultCellFormater;
            }
            if (style.type === 'date') {
                return function (v) {
                    if (_.isArray(v)) {
                        v = v[0];
                    }
                    var date = moment(v);
                    if (_this.timezone === 'utc') {
                        date = date.utc();
                    }
                    return date.format(style.dateFormat);
                };
            }
            if (style.type === 'number') {
                var valueFormater = kbn.valueFormats[style.unit];
                return function (v) {
                    if (v === null || v === void 0) {
                        return '-';
                    }
                    if (_.isString(v)) {
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
                var regex = kbn.stringToJsRegex(style.pattern);
                if (column.text.match(regex)) {
                    this.formaters[colIndex] = this.createColumnFormater(style);
                    return this.formaters[colIndex](value);
                }
            }
            this.formaters[colIndex] = this.defaultCellFormater;
            return this.formaters[colIndex](value);
        };
        TableRenderer.prototype.renderCell = function (columnIndex, value, addWidthHack) {
            if (addWidthHack === void 0) { addWidthHack = false; }
            var value = this.formatColumnValue(columnIndex, value);
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
                widthHack = '<div class="table-panel-width-hack">' + this.table.columns[columnIndex].text + '<div>';
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
    exports.TableRenderer = TableRenderer;
});
//# sourceMappingURL=renderer.js.map