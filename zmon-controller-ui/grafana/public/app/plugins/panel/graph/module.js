///<reference path="../../../headers/common.d.ts" />
System.register(['./graph', './legend', './series_overrides_ctrl', './template', 'moment', 'app/core/utils/kbn', 'lodash', 'app/core/time_series2', 'app/core/utils/file_export', 'app/plugins/sdk'], function(exports_1) {
    var __extends = (this && this.__extends) || function (d, b) {
        for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
    var template_1, moment_1, kbn_1, lodash_1, time_series2_1, fileExport, sdk_1;
    var GraphCtrl;
    return {
        setters:[
            function (_1) {},
            function (_2) {},
            function (_3) {},
            function (template_1_1) {
                template_1 = template_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (kbn_1_1) {
                kbn_1 = kbn_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (time_series2_1_1) {
                time_series2_1 = time_series2_1_1;
            },
            function (fileExport_1) {
                fileExport = fileExport_1;
            },
            function (sdk_1_1) {
                sdk_1 = sdk_1_1;
            }],
        execute: function() {
            GraphCtrl = (function (_super) {
                __extends(GraphCtrl, _super);
                /** @ngInject */
                function GraphCtrl($scope, $injector, annotationsSrv) {
                    _super.call(this, $scope, $injector);
                    this.annotationsSrv = annotationsSrv;
                    this.hiddenSeries = {};
                    this.seriesList = [];
                    this.colors = [];
                    this.panelDefaults = {
                        // datasource name, null = default datasource
                        datasource: null,
                        // sets client side (flot) or native graphite png renderer (png)
                        renderer: 'flot',
                        yaxes: [
                            {
                                label: null,
                                show: true,
                                logBase: 1,
                                min: null,
                                max: null,
                                format: 'short'
                            },
                            {
                                label: null,
                                show: true,
                                logBase: 1,
                                min: null,
                                max: null,
                                format: 'short'
                            }
                        ],
                        xaxis: {
                            show: true
                        },
                        grid: {
                            threshold1: null,
                            threshold2: null,
                            threshold1Color: 'rgba(216, 200, 27, 0.27)',
                            threshold2Color: 'rgba(234, 112, 112, 0.22)'
                        },
                        // show/hide lines
                        lines: true,
                        // fill factor
                        fill: 1,
                        // line width in pixels
                        linewidth: 2,
                        // show hide points
                        points: false,
                        // point radius in pixels
                        pointradius: 5,
                        // show hide bars
                        bars: false,
                        // enable/disable stacking
                        stack: false,
                        // stack percentage mode
                        percentage: false,
                        // legend options
                        legend: {
                            show: true,
                            values: false,
                            min: false,
                            max: false,
                            current: false,
                            total: false,
                            avg: false
                        },
                        // how null points should be handled
                        nullPointMode: 'connected',
                        // staircase line mode
                        steppedLine: false,
                        // tooltip options
                        tooltip: {
                            value_type: 'cumulative',
                            shared: true,
                            sort: 0,
                            msResolution: false,
                        },
                        // time overrides
                        timeFrom: null,
                        timeShift: null,
                        // metric queries
                        targets: [{}],
                        // series color overrides
                        aliasColors: {},
                        // other style overrides
                        seriesOverrides: [],
                    };
                    lodash_1.default.defaults(this.panel, this.panelDefaults);
                    lodash_1.default.defaults(this.panel.tooltip, this.panelDefaults.tooltip);
                    lodash_1.default.defaults(this.panel.grid, this.panelDefaults.grid);
                    lodash_1.default.defaults(this.panel.legend, this.panelDefaults.legend);
                    this.colors = $scope.$root.colors;
                    this.events.on('render', this.onRender.bind(this));
                    this.events.on('data-received', this.onDataReceived.bind(this));
                    this.events.on('data-error', this.onDataError.bind(this));
                    this.events.on('data-snapshot-load', this.onDataSnapshotLoad.bind(this));
                    this.events.on('init-edit-mode', this.onInitEditMode.bind(this));
                    this.events.on('init-panel-actions', this.onInitPanelActions.bind(this));
                }
                GraphCtrl.prototype.onInitEditMode = function () {
                    this.addEditorTab('Axes', 'public/app/plugins/panel/graph/tab_axes.html', 2);
                    this.addEditorTab('Legend', 'public/app/plugins/panel/graph/tab_legend.html', 3);
                    this.addEditorTab('Display', 'public/app/plugins/panel/graph/tab_display.html', 4);
                    this.logScales = {
                        'linear': 1,
                        'log (base 2)': 2,
                        'log (base 10)': 10,
                        'log (base 32)': 32,
                        'log (base 1024)': 1024
                    };
                    this.unitFormats = kbn_1.default.getUnitFormats();
                };
                GraphCtrl.prototype.onInitPanelActions = function (actions) {
                    actions.push({ text: 'Export CSV (series as rows)', click: 'ctrl.exportCsv()' });
                    actions.push({ text: 'Export CSV (series as columns)', click: 'ctrl.exportCsvColumns()' });
                    actions.push({ text: 'Toggle legend', click: 'ctrl.toggleLegend()' });
                };
                GraphCtrl.prototype.setUnitFormat = function (axis, subItem) {
                    axis.format = subItem.value;
                    this.render();
                };
                GraphCtrl.prototype.issueQueries = function (datasource) {
                    this.annotationsPromise = this.annotationsSrv.getAnnotations(this.dashboard);
                    return _super.prototype.issueQueries.call(this, datasource);
                };
                GraphCtrl.prototype.zoomOut = function (evt) {
                    this.publishAppEvent('zoom-out', 2);
                };
                GraphCtrl.prototype.onDataSnapshotLoad = function (snapshotData) {
                    this.annotationsPromise = this.annotationsSrv.getAnnotations(this.dashboard);
                    this.onDataReceived(snapshotData);
                };
                GraphCtrl.prototype.onDataError = function (err) {
                    this.seriesList = [];
                    this.render([]);
                };
                GraphCtrl.prototype.onDataReceived = function (dataList) {
                    var _this = this;
                    this.datapointsWarning = false;
                    this.datapointsCount = 0;
                    this.datapointsOutside = false;
                    this.seriesList = dataList.map(this.seriesHandler.bind(this));
                    this.datapointsWarning = this.datapointsCount === 0 || this.datapointsOutside;
                    this.annotationsPromise.then(function (annotations) {
                        _this.loading = false;
                        _this.seriesList.annotations = annotations;
                        _this.render(_this.seriesList);
                    }, function () {
                        _this.loading = false;
                        _this.render(_this.seriesList);
                    });
                };
                GraphCtrl.prototype.seriesHandler = function (seriesData, index) {
                    var datapoints = seriesData.datapoints;
                    var alias = seriesData.target;
                    var colorIndex = index % this.colors.length;
                    var color = this.panel.aliasColors[alias] || this.colors[colorIndex];
                    var series = new time_series2_1.default({
                        datapoints: datapoints,
                        alias: alias,
                        color: color,
                        unit: seriesData.unit,
                    });
                    if (datapoints && datapoints.length > 0) {
                        var last = moment_1.default.utc(datapoints[datapoints.length - 1][1]);
                        var from = moment_1.default.utc(this.range.from);
                        if (last - from < -10000) {
                            this.datapointsOutside = true;
                        }
                        this.datapointsCount += datapoints.length;
                        this.panel.tooltip.msResolution = this.panel.tooltip.msResolution || series.isMsResolutionNeeded();
                    }
                    return series;
                };
                GraphCtrl.prototype.onRender = function () {
                    if (!this.seriesList) {
                        return;
                    }
                    for (var _i = 0, _a = this.seriesList; _i < _a.length; _i++) {
                        var series = _a[_i];
                        series.applySeriesOverrides(this.panel.seriesOverrides);
                        if (series.unit) {
                            this.panel.yaxes[series.yaxis - 1].format = series.unit;
                        }
                    }
                };
                GraphCtrl.prototype.changeSeriesColor = function (series, color) {
                    series.color = color;
                    this.panel.aliasColors[series.alias] = series.color;
                    this.render();
                };
                GraphCtrl.prototype.toggleSeries = function (serie, event) {
                    if (event.ctrlKey || event.metaKey || event.shiftKey) {
                        if (this.hiddenSeries[serie.alias]) {
                            delete this.hiddenSeries[serie.alias];
                        }
                        else {
                            this.hiddenSeries[serie.alias] = true;
                        }
                    }
                    else {
                        this.toggleSeriesExclusiveMode(serie);
                    }
                    this.render();
                };
                GraphCtrl.prototype.toggleSeriesExclusiveMode = function (serie) {
                    var _this = this;
                    var hidden = this.hiddenSeries;
                    if (hidden[serie.alias]) {
                        delete hidden[serie.alias];
                    }
                    // check if every other series is hidden
                    var alreadyExclusive = lodash_1.default.every(this.seriesList, function (value) {
                        if (value.alias === serie.alias) {
                            return true;
                        }
                        return hidden[value.alias];
                    });
                    if (alreadyExclusive) {
                        // remove all hidden series
                        lodash_1.default.each(this.seriesList, function (value) {
                            delete _this.hiddenSeries[value.alias];
                        });
                    }
                    else {
                        // hide all but this serie
                        lodash_1.default.each(this.seriesList, function (value) {
                            if (value.alias === serie.alias) {
                                return;
                            }
                            _this.hiddenSeries[value.alias] = true;
                        });
                    }
                };
                GraphCtrl.prototype.toggleAxis = function (info) {
                    var override = lodash_1.default.findWhere(this.panel.seriesOverrides, { alias: info.alias });
                    if (!override) {
                        override = { alias: info.alias };
                        this.panel.seriesOverrides.push(override);
                    }
                    info.yaxis = override.yaxis = info.yaxis === 2 ? 1 : 2;
                    this.render();
                };
                ;
                GraphCtrl.prototype.addSeriesOverride = function (override) {
                    this.panel.seriesOverrides.push(override || {});
                };
                GraphCtrl.prototype.removeSeriesOverride = function (override) {
                    this.panel.seriesOverrides = lodash_1.default.without(this.panel.seriesOverrides, override);
                    this.render();
                };
                // Called from panel menu
                GraphCtrl.prototype.toggleLegend = function () {
                    this.panel.legend.show = !this.panel.legend.show;
                    this.refresh();
                };
                GraphCtrl.prototype.legendValuesOptionChanged = function () {
                    var legend = this.panel.legend;
                    legend.values = legend.min || legend.max || legend.avg || legend.current || legend.total;
                    this.render();
                };
                GraphCtrl.prototype.exportCsv = function () {
                    fileExport.exportSeriesListToCsv(this.seriesList);
                };
                GraphCtrl.prototype.exportCsvColumns = function () {
                    fileExport.exportSeriesListToCsvColumns(this.seriesList);
                };
                GraphCtrl.template = template_1.default;
                return GraphCtrl;
            })(sdk_1.MetricsPanelCtrl);
            exports_1("GraphCtrl", GraphCtrl);
            exports_1("PanelCtrl", GraphCtrl);
        }
    }
});
//# sourceMappingURL=module.js.map