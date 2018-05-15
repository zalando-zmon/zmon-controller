///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', 'jquery', 'jquery.flot', 'jquery.flot.gauge', 'app/core/utils/kbn', 'app/core/config', 'app/core/time_series2', 'app/plugins/sdk'], function(exports_1) {
    var __extends = (this && this.__extends) || function (d, b) {
        for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
    var lodash_1, jquery_1, kbn_1, config_1, time_series2_1, sdk_1;
    var SingleStatCtrl;
    function getColorForValue(data, value) {
        for (var i = data.thresholds.length; i > 0; i--) {
            if (value >= data.thresholds[i - 1]) {
                return data.colorMap[i];
            }
        }
        return lodash_1.default.first(data.colorMap);
    }
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (jquery_1_1) {
                jquery_1 = jquery_1_1;
            },
            function (_1) {},
            function (_2) {},
            function (kbn_1_1) {
                kbn_1 = kbn_1_1;
            },
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (time_series2_1_1) {
                time_series2_1 = time_series2_1_1;
            },
            function (sdk_1_1) {
                sdk_1 = sdk_1_1;
            }],
        execute: function() {
            SingleStatCtrl = (function (_super) {
                __extends(SingleStatCtrl, _super);
                /** @ngInject */
                function SingleStatCtrl($scope, $injector, $location, linkSrv) {
                    _super.call(this, $scope, $injector);
                    this.$location = $location;
                    this.linkSrv = linkSrv;
                    // Set and populate defaults
                    this.panelDefaults = {
                        links: [],
                        datasource: null,
                        maxDataPoints: 100,
                        interval: null,
                        targets: [{}],
                        cacheTimeout: null,
                        format: 'none',
                        prefix: '',
                        postfix: '',
                        nullText: null,
                        valueMaps: [
                            { value: 'null', op: '=', text: 'N/A' }
                        ],
                        mappingTypes: [
                            { name: 'value to text', value: 1 },
                            { name: 'range to text', value: 2 },
                        ],
                        rangeMaps: [
                            { from: 'null', to: 'null', text: 'N/A' }
                        ],
                        mappingType: 1,
                        nullPointMode: 'connected',
                        valueName: 'avg',
                        prefixFontSize: '50%',
                        valueFontSize: '80%',
                        postfixFontSize: '50%',
                        thresholds: '',
                        colorBackground: false,
                        colorValue: false,
                        colors: ["rgba(245, 54, 54, 0.9)", "rgba(237, 129, 40, 0.89)", "rgba(50, 172, 45, 0.97)"],
                        sparkline: {
                            show: false,
                            full: false,
                            lineColor: 'rgb(31, 120, 193)',
                            fillColor: 'rgba(31, 118, 189, 0.18)',
                        },
                        gauge: {
                            show: false,
                            minValue: 0,
                            maxValue: 100,
                            thresholdMarkers: true,
                            thresholdLabels: false
                        }
                    };
                    lodash_1.default.defaults(this.panel, this.panelDefaults);
                    this.events.on('data-received', this.onDataReceived.bind(this));
                    this.events.on('data-error', this.onDataError.bind(this));
                    this.events.on('data-snapshot-load', this.onDataReceived.bind(this));
                    this.events.on('init-edit-mode', this.onInitEditMode.bind(this));
                }
                SingleStatCtrl.prototype.onInitEditMode = function () {
                    this.fontSizes = ['20%', '30%', '50%', '70%', '80%', '100%', '110%', '120%', '150%', '170%', '200%'];
                    this.addEditorTab('Options', 'public/app/plugins/panel/singlestat/editor.html', 2);
                    this.addEditorTab('Value Mappings', 'public/app/plugins/panel/singlestat/mappings.html', 3);
                    this.unitFormats = kbn_1.default.getUnitFormats();
                };
                SingleStatCtrl.prototype.setUnitFormat = function (subItem) {
                    this.panel.format = subItem.value;
                    this.render();
                };
                SingleStatCtrl.prototype.onDataError = function (err) {
                    this.onDataReceived([]);
                };
                SingleStatCtrl.prototype.onDataReceived = function (dataList) {
                    this.series = dataList.map(this.seriesHandler.bind(this));
                    var data = {};
                    this.setValues(data);
                    this.data = data;
                    this.render();
                };
                SingleStatCtrl.prototype.seriesHandler = function (seriesData) {
                    var series = new time_series2_1.default({
                        datapoints: seriesData.datapoints,
                        alias: seriesData.target,
                    });
                    series.flotpairs = series.getFlotPairs(this.panel.nullPointMode);
                    return series;
                };
                SingleStatCtrl.prototype.setColoring = function (options) {
                    if (options.background) {
                        this.panel.colorValue = false;
                        this.panel.colors = ['rgba(71, 212, 59, 0.4)', 'rgba(245, 150, 40, 0.73)', 'rgba(225, 40, 40, 0.59)'];
                    }
                    else {
                        this.panel.colorBackground = false;
                        this.panel.colors = ['rgba(50, 172, 45, 0.97)', 'rgba(237, 129, 40, 0.89)', 'rgba(245, 54, 54, 0.9)'];
                    }
                    this.render();
                };
                SingleStatCtrl.prototype.invertColorOrder = function () {
                    var tmp = this.panel.colors[0];
                    this.panel.colors[0] = this.panel.colors[2];
                    this.panel.colors[2] = tmp;
                    this.render();
                };
                SingleStatCtrl.prototype.getDecimalsForValue = function (value) {
                    if (lodash_1.default.isNumber(this.panel.decimals)) {
                        return { decimals: this.panel.decimals, scaledDecimals: null };
                    }
                    var delta = value / 2;
                    var dec = -Math.floor(Math.log(delta) / Math.LN10);
                    var magn = Math.pow(10, -dec), norm = delta / magn, // norm is between 1.0 and 10.0
                    size;
                    if (norm < 1.5) {
                        size = 1;
                    }
                    else if (norm < 3) {
                        size = 2;
                        // special case for 2.5, requires an extra decimal
                        if (norm > 2.25) {
                            size = 2.5;
                            ++dec;
                        }
                    }
                    else if (norm < 7.5) {
                        size = 5;
                    }
                    else {
                        size = 10;
                    }
                    size *= magn;
                    // reduce starting decimals if not needed
                    if (Math.floor(value) === value) {
                        dec = 0;
                    }
                    var result = {};
                    result.decimals = Math.max(0, dec);
                    result.scaledDecimals = result.decimals - Math.floor(Math.log(size) / Math.LN10) + 2;
                    return result;
                };
                SingleStatCtrl.prototype.setValues = function (data) {
                    data.flotpairs = [];
                    if (this.series.length > 1) {
                        var error = new Error();
                        error.message = 'Multiple Series Error';
                        error.data = 'Metric query returns ' + this.series.length +
                            ' series. Single Stat Panel expects a single series.\n\nResponse:\n' + JSON.stringify(this.series);
                        throw error;
                    }
                    if (this.series && this.series.length > 0) {
                        var lastPoint = lodash_1.default.last(this.series[0].datapoints);
                        var lastValue = lodash_1.default.isArray(lastPoint) ? lastPoint[0] : null;
                        if (lodash_1.default.isString(lastValue)) {
                            data.value = 0;
                            data.valueFormated = lastValue;
                            data.valueRounded = 0;
                        }
                        else {
                            data.value = this.series[0].stats[this.panel.valueName];
                            data.flotpairs = this.series[0].flotpairs;
                            var decimalInfo = this.getDecimalsForValue(data.value);
                            var formatFunc = kbn_1.default.valueFormats[this.panel.format];
                            data.valueFormated = formatFunc(data.value, decimalInfo.decimals, decimalInfo.scaledDecimals);
                            data.valueRounded = kbn_1.default.roundValue(data.value, decimalInfo.decimals);
                        }
                    }
                    // check value to text mappings if its enabled
                    if (this.panel.mappingType === 1) {
                        for (var i = 0; i < this.panel.valueMaps.length; i++) {
                            var map = this.panel.valueMaps[i];
                            // special null case
                            if (map.value === 'null') {
                                if (data.value === null || data.value === void 0) {
                                    data.valueFormated = map.text;
                                    return;
                                }
                                continue;
                            }
                            // value/number to text mapping
                            var value = parseFloat(map.value);
                            if (value === data.valueRounded) {
                                data.valueFormated = map.text;
                                return;
                            }
                        }
                    }
                    else if (this.panel.mappingType === 2) {
                        for (var i = 0; i < this.panel.rangeMaps.length; i++) {
                            var map = this.panel.rangeMaps[i];
                            // special null case
                            if (map.from === 'null' && map.to === 'null') {
                                if (data.value === null || data.value === void 0) {
                                    data.valueFormated = map.text;
                                    return;
                                }
                                continue;
                            }
                            // value/number to range mapping
                            var from = parseFloat(map.from);
                            var to = parseFloat(map.to);
                            if (to >= data.valueRounded && from <= data.valueRounded) {
                                data.valueFormated = map.text;
                                return;
                            }
                        }
                    }
                    if (data.value === null || data.value === void 0) {
                        data.valueFormated = "no value";
                    }
                };
                ;
                SingleStatCtrl.prototype.removeValueMap = function (map) {
                    var index = lodash_1.default.indexOf(this.panel.valueMaps, map);
                    this.panel.valueMaps.splice(index, 1);
                    this.render();
                };
                ;
                SingleStatCtrl.prototype.addValueMap = function () {
                    this.panel.valueMaps.push({ value: '', op: '=', text: '' });
                };
                SingleStatCtrl.prototype.removeRangeMap = function (rangeMap) {
                    var index = lodash_1.default.indexOf(this.panel.rangeMaps, rangeMap);
                    this.panel.rangeMaps.splice(index, 1);
                    this.render();
                };
                ;
                SingleStatCtrl.prototype.addRangeMap = function () {
                    this.panel.rangeMaps.push({ from: '', to: '', text: '' });
                };
                SingleStatCtrl.prototype.link = function (scope, elem, attrs, ctrl) {
                    var $location = this.$location;
                    var linkSrv = this.linkSrv;
                    var $timeout = this.$timeout;
                    var panel = ctrl.panel;
                    var templateSrv = this.templateSrv;
                    var data, linkInfo;
                    var $panelContainer = elem.find('.panel-container');
                    elem = elem.find('.singlestat-panel');
                    function setElementHeight() {
                        elem.css('height', ctrl.height + 'px');
                    }
                    function applyColoringThresholds(value, valueString) {
                        if (!panel.colorValue) {
                            return valueString;
                        }
                        var color = getColorForValue(data, value);
                        if (color) {
                            return '<span style="color:' + color + '">' + valueString + '</span>';
                        }
                        return valueString;
                    }
                    function getSpan(className, fontSize, value) {
                        value = templateSrv.replace(value);
                        return '<span class="' + className + '" style="font-size:' + fontSize + '">' +
                            value + '</span>';
                    }
                    function getBigValueHtml() {
                        var body = '<div class="singlestat-panel-value-container">';
                        if (panel.prefix) {
                            body += getSpan('singlestat-panel-prefix', panel.prefixFontSize, panel.prefix);
                        }
                        var value = applyColoringThresholds(data.value, data.valueFormated);
                        body += getSpan('singlestat-panel-value', panel.valueFontSize, value);
                        if (panel.postfix) {
                            body += getSpan('singlestat-panel-postfix', panel.postfixFontSize, panel.postfix);
                        }
                        body += '</div>';
                        return body;
                    }
                    function getValueText() {
                        var result = panel.prefix ? panel.prefix : '';
                        result += data.valueFormated;
                        result += panel.postfix ? panel.postfix : '';
                        return result;
                    }
                    function addGauge() {
                        var width = elem.width();
                        var height = elem.height();
                        ctrl.invalidGaugeRange = false;
                        if (panel.gauge.minValue > panel.gauge.maxValue) {
                            ctrl.invalidGaugeRange = true;
                            return;
                        }
                        var plotCanvas = jquery_1.default('<div></div>');
                        var plotCss = {
                            top: '10px',
                            margin: 'auto',
                            position: 'relative',
                            height: (height * 0.9) + 'px',
                            width: width + 'px'
                        };
                        plotCanvas.css(plotCss);
                        var thresholds = [];
                        for (var i = 0; i < data.thresholds.length; i++) {
                            thresholds.push({
                                value: data.thresholds[i],
                                color: data.colorMap[i]
                            });
                        }
                        thresholds.push({
                            value: panel.gauge.maxValue,
                            color: data.colorMap[data.colorMap.length - 1]
                        });
                        var bgColor = config_1.default.bootData.user.lightTheme
                            ? 'rgb(230,230,230)'
                            : 'rgb(38,38,38)';
                        var fontScale = parseInt(panel.valueFontSize) / 100;
                        var dimension = Math.min(width, height);
                        var fontSize = Math.min(dimension / 5, 100) * fontScale;
                        var gaugeWidth = Math.min(dimension / 6, 60);
                        var thresholdMarkersWidth = gaugeWidth / 5;
                        var options = {
                            series: {
                                gauges: {
                                    gauge: {
                                        min: panel.gauge.minValue,
                                        max: panel.gauge.maxValue,
                                        background: { color: bgColor },
                                        border: { color: null },
                                        shadow: { show: false },
                                        width: gaugeWidth,
                                    },
                                    frame: { show: false },
                                    label: { show: false },
                                    layout: { margin: 0, thresholdWidth: 0 },
                                    cell: { border: { width: 0 } },
                                    threshold: {
                                        values: thresholds,
                                        label: {
                                            show: panel.gauge.thresholdLabels,
                                            margin: 8,
                                            font: { size: 18 }
                                        },
                                        show: panel.gauge.thresholdMarkers,
                                        width: thresholdMarkersWidth,
                                    },
                                    value: {
                                        color: panel.colorValue ? getColorForValue(data, data.valueRounded) : null,
                                        formatter: function () { return getValueText(); },
                                        font: { size: fontSize, family: 'Helvetica Neue", Helvetica, Arial, sans-serif' }
                                    },
                                    show: true
                                }
                            }
                        };
                        elem.append(plotCanvas);
                        var plotSeries = {
                            data: [[0, data.valueRounded]]
                        };
                        jquery_1.default.plot(plotCanvas, [plotSeries], options);
                    }
                    function addSparkline() {
                        var width = elem.width() + 20;
                        if (width < 30) {
                            // element has not gotten it's width yet
                            // delay sparkline render
                            setTimeout(addSparkline, 30);
                            return;
                        }
                        var height = ctrl.height;
                        var plotCanvas = jquery_1.default('<div></div>');
                        var plotCss = {};
                        plotCss.position = 'absolute';
                        if (panel.sparkline.full) {
                            plotCss.bottom = '5px';
                            plotCss.left = '-5px';
                            plotCss.width = (width - 10) + 'px';
                            var dynamicHeightMargin = height <= 100 ? 5 : (Math.round((height / 100)) * 15) + 5;
                            plotCss.height = (height - dynamicHeightMargin) + 'px';
                        }
                        else {
                            plotCss.bottom = "0px";
                            plotCss.left = "-5px";
                            plotCss.width = (width - 10) + 'px';
                            plotCss.height = Math.floor(height * 0.25) + "px";
                        }
                        plotCanvas.css(plotCss);
                        var options = {
                            legend: { show: false },
                            series: {
                                lines: {
                                    show: true,
                                    fill: 1,
                                    lineWidth: 1,
                                    fillColor: panel.sparkline.fillColor,
                                },
                            },
                            yaxes: { show: false },
                            xaxis: {
                                show: false,
                                mode: "time",
                                min: ctrl.range.from.valueOf(),
                                max: ctrl.range.to.valueOf(),
                            },
                            grid: { hoverable: false, show: false },
                        };
                        elem.append(plotCanvas);
                        var plotSeries = {
                            data: data.flotpairs,
                            color: panel.sparkline.lineColor
                        };
                        jquery_1.default.plot(plotCanvas, [plotSeries], options);
                    }
                    function render() {
                        if (!ctrl.data) {
                            return;
                        }
                        data = ctrl.data;
                        // get thresholds
                        data.thresholds = panel.thresholds.split(',').map(function (strVale) {
                            return Number(strVale.trim());
                        });
                        data.colorMap = panel.colors;
                        setElementHeight();
                        var body = panel.gauge.show ? '' : getBigValueHtml();
                        if (panel.colorBackground && !isNaN(data.valueRounded)) {
                            var color = getColorForValue(data, data.valueRounded);
                            if (color) {
                                $panelContainer.css('background-color', color);
                                if (scope.fullscreen) {
                                    elem.css('background-color', color);
                                }
                                else {
                                    elem.css('background-color', '');
                                }
                            }
                        }
                        else {
                            $panelContainer.css('background-color', '');
                            elem.css('background-color', '');
                        }
                        elem.html(body);
                        if (panel.sparkline.show) {
                            addSparkline();
                        }
                        if (panel.gauge.show) {
                            addGauge();
                        }
                        elem.toggleClass('pointer', panel.links.length > 0);
                        if (panel.links.length > 0) {
                            linkInfo = linkSrv.getPanelLinkAnchorInfo(panel.links[0], panel.scopedVars);
                        }
                        else {
                            linkInfo = null;
                        }
                    }
                    function hookupDrilldownLinkTooltip() {
                        // drilldown link tooltip
                        var drilldownTooltip = jquery_1.default('<div id="tooltip" class="">hello</div>"');
                        elem.mouseleave(function () {
                            if (panel.links.length === 0) {
                                return;
                            }
                            drilldownTooltip.detach();
                        });
                        elem.click(function (evt) {
                            if (!linkInfo) {
                                return;
                            }
                            // ignore title clicks in title
                            if (jquery_1.default(evt).parents('.panel-header').length > 0) {
                                return;
                            }
                            if (linkInfo.target === '_blank') {
                                var redirectWindow = window.open(linkInfo.href, '_blank');
                                redirectWindow.location;
                                return;
                            }
                            if (linkInfo.href.indexOf('http') === 0) {
                                window.location.href = linkInfo.href;
                            }
                            else {
                                $timeout(function () {
                                    $location.url(linkInfo.href);
                                });
                            }
                            drilldownTooltip.detach();
                        });
                        elem.mousemove(function (e) {
                            if (!linkInfo) {
                                return;
                            }
                            drilldownTooltip.text('click to go to: ' + linkInfo.title);
                            drilldownTooltip.place_tt(e.pageX + 20, e.pageY - 15);
                        });
                    }
                    hookupDrilldownLinkTooltip();
                    this.events.on('render', function () {
                        render();
                        ctrl.renderingCompleted();
                    });
                };
                SingleStatCtrl.templateUrl = 'module.html';
                return SingleStatCtrl;
            })(sdk_1.MetricsPanelCtrl);
            exports_1("SingleStatCtrl", SingleStatCtrl);
            exports_1("PanelCtrl", SingleStatCtrl);
            exports_1("getColorForValue", getColorForValue);
        }
    }
});
//# sourceMappingURL=module.js.map