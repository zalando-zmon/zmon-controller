///<reference path="../../headers/common.d.ts" />
System.register(['app/core/config', 'jquery', 'lodash', 'app/core/utils/kbn', './panel_ctrl', 'app/core/utils/rangeutil', 'app/core/utils/datemath'], function(exports_1) {
    var __extends = (this && this.__extends) || function (d, b) {
        for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
    var config_1, jquery_1, lodash_1, kbn_1, panel_ctrl_1, rangeUtil, dateMath;
    var MetricsPanelCtrl;
    return {
        setters:[
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (jquery_1_1) {
                jquery_1 = jquery_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (kbn_1_1) {
                kbn_1 = kbn_1_1;
            },
            function (panel_ctrl_1_1) {
                panel_ctrl_1 = panel_ctrl_1_1;
            },
            function (rangeUtil_1) {
                rangeUtil = rangeUtil_1;
            },
            function (dateMath_1) {
                dateMath = dateMath_1;
            }],
        execute: function() {
            MetricsPanelCtrl = (function (_super) {
                __extends(MetricsPanelCtrl, _super);
                function MetricsPanelCtrl($scope, $injector) {
                    _super.call(this, $scope, $injector);
                    // make metrics tab the default
                    this.editorTabIndex = 1;
                    this.$q = $injector.get('$q');
                    this.datasourceSrv = $injector.get('datasourceSrv');
                    this.timeSrv = $injector.get('timeSrv');
                    this.templateSrv = $injector.get('templateSrv');
                    if (!this.panel.targets) {
                        this.panel.targets = [{}];
                    }
                    this.events.on('refresh', this.onMetricsPanelRefresh.bind(this));
                    this.events.on('init-edit-mode', this.onInitMetricsPanelEditMode.bind(this));
                }
                MetricsPanelCtrl.prototype.onInitMetricsPanelEditMode = function () {
                    this.addEditorTab('Metrics', 'public/app/partials/metrics.html');
                    this.addEditorTab('Time range', 'public/app/features/panel/partials/panelTime.html');
                };
                MetricsPanelCtrl.prototype.onMetricsPanelRefresh = function () {
                    var _this = this;
                    // ignore fetching data if another panel is in fullscreen
                    if (this.otherPanelInFullscreenMode()) {
                        return;
                    }
                    // if we have snapshot data use that
                    if (this.panel.snapshotData) {
                        this.updateTimeRange();
                        var data = this.panel.snapshotData;
                        // backward compatability
                        if (!lodash_1.default.isArray(data)) {
                            data = data.data;
                        }
                        this.events.emit('data-snapshot-load', data);
                        return;
                    }
                    // // ignore if we have data stream
                    if (this.dataStream) {
                        return;
                    }
                    // clear loading/error state
                    delete this.error;
                    this.loading = true;
                    // load datasource service
                    this.setTimeQueryStart();
                    this.datasourceSrv.get(this.panel.datasource)
                        .then(this.issueQueries.bind(this))
                        .then(this.handleQueryResult.bind(this))
                        .catch(function (err) {
                        // if cancelled  keep loading set to true
                        if (err.cancelled) {
                            console.log('Panel request cancelled', err);
                            return;
                        }
                        _this.loading = false;
                        _this.error = err.message || "Request Error";
                        _this.inspector = { error: err };
                        _this.events.emit('data-error', err);
                        console.log('Panel data error:', err);
                    });
                };
                MetricsPanelCtrl.prototype.setTimeQueryStart = function () {
                    this.timing.queryStart = new Date().getTime();
                };
                MetricsPanelCtrl.prototype.setTimeQueryEnd = function () {
                    this.timing.queryEnd = new Date().getTime();
                };
                MetricsPanelCtrl.prototype.updateTimeRange = function () {
                    this.range = this.timeSrv.timeRange();
                    this.rangeRaw = this.timeSrv.timeRange(false);
                    this.applyPanelTimeOverrides();
                    if (this.panel.maxDataPoints) {
                        this.resolution = this.panel.maxDataPoints;
                    }
                    else {
                        this.resolution = Math.ceil(jquery_1.default(window).width() * (this.panel.span / 12));
                    }
                    var panelInterval = this.panel.interval;
                    var datasourceInterval = (this.datasource || {}).interval;
                    this.interval = kbn_1.default.calculateInterval(this.range, this.resolution, panelInterval || datasourceInterval);
                };
                ;
                MetricsPanelCtrl.prototype.applyPanelTimeOverrides = function () {
                    this.timeInfo = '';
                    // check panel time overrrides
                    if (this.panel.timeFrom) {
                        var timeFromInterpolated = this.templateSrv.replace(this.panel.timeFrom, this.panel.scopedVars);
                        var timeFromInfo = rangeUtil.describeTextRange(timeFromInterpolated);
                        if (timeFromInfo.invalid) {
                            this.timeInfo = 'invalid time override';
                            return;
                        }
                        if (lodash_1.default.isString(this.rangeRaw.from)) {
                            var timeFromDate = dateMath.parse(timeFromInfo.from);
                            this.timeInfo = timeFromInfo.display;
                            this.rangeRaw.from = timeFromInfo.from;
                            this.rangeRaw.to = timeFromInfo.to;
                            this.range.from = timeFromDate;
                            this.range.to = dateMath.parse(timeFromInfo.to);
                        }
                    }
                    if (this.panel.timeShift) {
                        var timeShiftInterpolated = this.templateSrv.replace(this.panel.timeShift, this.panel.scopedVars);
                        var timeShiftInfo = rangeUtil.describeTextRange(timeShiftInterpolated);
                        if (timeShiftInfo.invalid) {
                            this.timeInfo = 'invalid timeshift';
                            return;
                        }
                        var timeShift = '-' + timeShiftInterpolated;
                        this.timeInfo += ' timeshift ' + timeShift;
                        this.range.from = dateMath.parseDateMath(timeShift, this.range.from, false);
                        this.range.to = dateMath.parseDateMath(timeShift, this.range.to, true);
                        this.rangeRaw = this.range;
                    }
                    if (this.panel.hideTimeOverride) {
                        this.timeInfo = '';
                    }
                };
                ;
                MetricsPanelCtrl.prototype.issueQueries = function (datasource) {
                    this.updateTimeRange();
                    this.datasource = datasource;
                    if (!this.panel.targets || this.panel.targets.length === 0) {
                        return this.$q.when([]);
                    }
                    var metricsQuery = {
                        panelId: this.panel.id,
                        range: this.range,
                        rangeRaw: this.rangeRaw,
                        interval: this.interval,
                        targets: this.panel.targets,
                        format: this.panel.renderer === 'png' ? 'png' : 'json',
                        maxDataPoints: this.resolution,
                        scopedVars: this.panel.scopedVars,
                        cacheTimeout: this.panel.cacheTimeout
                    };
                    return datasource.query(metricsQuery);
                };
                MetricsPanelCtrl.prototype.handleQueryResult = function (result) {
                    this.setTimeQueryEnd();
                    this.loading = false;
                    // check for if data source returns subject
                    if (result && result.subscribe) {
                        this.handleDataStream(result);
                        return;
                    }
                    if (this.dashboard.snapshot) {
                        this.panel.snapshotData = result.data;
                    }
                    if (!result || !result.data) {
                        console.log('Data source query result invalid, missing data field:', result);
                        result = { data: [] };
                    }
                    return this.events.emit('data-received', result.data);
                };
                MetricsPanelCtrl.prototype.handleDataStream = function (stream) {
                    var _this = this;
                    // if we already have a connection
                    if (this.dataStream) {
                        console.log('two stream observables!');
                        return;
                    }
                    this.dataStream = stream;
                    this.dataSubscription = stream.subscribe({
                        next: function (data) {
                            console.log('dataSubject next!');
                            if (data.range) {
                                _this.range = data.range;
                            }
                            _this.events.emit('data-received', data.data);
                        },
                        error: function (error) {
                            _this.events.emit('data-error', error);
                            console.log('panel: observer got error');
                        },
                        complete: function () {
                            console.log('panel: observer got complete');
                        }
                    });
                };
                MetricsPanelCtrl.prototype.setDatasource = function (datasource) {
                    var _this = this;
                    // switching to mixed
                    if (datasource.meta.mixed) {
                        lodash_1.default.each(this.panel.targets, function (target) {
                            target.datasource = _this.panel.datasource;
                            if (target.datasource === null) {
                                target.datasource = config_1.default.defaultDatasource;
                            }
                        });
                    }
                    else if (this.datasource && this.datasource.meta.mixed) {
                        lodash_1.default.each(this.panel.targets, function (target) {
                            delete target.datasource;
                        });
                    }
                    this.panel.datasource = datasource.value;
                    this.datasourceName = datasource.name;
                    this.datasource = null;
                    this.refresh();
                };
                return MetricsPanelCtrl;
            })(panel_ctrl_1.PanelCtrl);
            exports_1("MetricsPanelCtrl", MetricsPanelCtrl);
        }
    }
});
//# sourceMappingURL=metrics_panel_ctrl.js.map