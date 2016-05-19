///<reference path="../../../../headers/common.d.ts" />
System.register(['../../../../../test/lib/common', '../module', 'angular', 'jquery', 'test/specs/helpers', 'app/core/time_series2', 'moment', 'app/core/core'], function(exports_1) {
    var common_1, angular_1, jquery_1, helpers_1, time_series2_1, moment_1, core_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (_1) {},
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (jquery_1_1) {
                jquery_1 = jquery_1_1;
            },
            function (helpers_1_1) {
                helpers_1 = helpers_1_1;
            },
            function (time_series2_1_1) {
                time_series2_1 = time_series2_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            common_1.describe('grafanaGraph', function () {
                common_1.beforeEach(common_1.angularMocks.module('grafana.directives'));
                function graphScenario(desc, func, elementWidth) {
                    if (elementWidth === void 0) { elementWidth = 500; }
                    common_1.describe(desc, function () {
                        var ctx = {};
                        ctx.setup = function (setupFunc) {
                            common_1.beforeEach(common_1.angularMocks.module(function ($provide) {
                                $provide.value("timeSrv", new helpers_1.default.TimeSrvStub());
                            }));
                            common_1.beforeEach(common_1.angularMocks.inject(function ($rootScope, $compile) {
                                var ctrl = {
                                    events: new core_1.Emitter(),
                                    height: 200,
                                    panel: {
                                        legend: {},
                                        grid: {},
                                        yaxes: [
                                            {
                                                min: null,
                                                max: null,
                                                format: 'short',
                                                logBase: 1
                                            },
                                            {
                                                min: null,
                                                max: null,
                                                format: 'short',
                                                logBase: 1
                                            }
                                        ],
                                        xaxis: {},
                                        seriesOverrides: [],
                                        tooltip: {
                                            shared: true
                                        }
                                    },
                                    renderingCompleted: common_1.sinon.spy(),
                                    hiddenSeries: {},
                                    dashboard: {
                                        getTimezone: common_1.sinon.stub().returns('browser')
                                    },
                                    range: {
                                        from: moment_1.default([2015, 1, 1, 10]),
                                        to: moment_1.default([2015, 1, 1, 22]),
                                    },
                                };
                                var scope = $rootScope.$new();
                                scope.ctrl = ctrl;
                                $rootScope.onAppEvent = common_1.sinon.spy();
                                ctx.data = [];
                                ctx.data.push(new time_series2_1.default({
                                    datapoints: [[1, 1], [2, 2]],
                                    alias: 'series1'
                                }));
                                ctx.data.push(new time_series2_1.default({
                                    datapoints: [[1, 1], [2, 2]],
                                    alias: 'series2'
                                }));
                                setupFunc(ctrl, ctx.data);
                                var element = angular_1.default.element("<div style='width:" + elementWidth + "px' grafana-graph><div>");
                                $compile(element)(scope);
                                scope.$digest();
                                jquery_1.default.plot = ctx.plotSpy = common_1.sinon.spy();
                                ctrl.events.emit('render', ctx.data);
                                ctx.plotData = ctx.plotSpy.getCall(0).args[1];
                                ctx.plotOptions = ctx.plotSpy.getCall(0).args[2];
                            }));
                        };
                        func(ctx);
                    });
                }
                graphScenario('simple lines options', function (ctx) {
                    ctx.setup(function (ctrl) {
                        ctrl.panel.lines = true;
                        ctrl.panel.fill = 5;
                        ctrl.panel.linewidth = 3;
                        ctrl.panel.steppedLine = true;
                    });
                    common_1.it('should configure plot with correct options', function () {
                        common_1.expect(ctx.plotOptions.series.lines.show).to.be(true);
                        common_1.expect(ctx.plotOptions.series.lines.fill).to.be(0.5);
                        common_1.expect(ctx.plotOptions.series.lines.lineWidth).to.be(3);
                        common_1.expect(ctx.plotOptions.series.lines.steps).to.be(true);
                    });
                });
                graphScenario('grid thresholds 100, 200', function (ctx) {
                    ctx.setup(function (ctrl) {
                        ctrl.panel.grid = {
                            threshold1: 100,
                            threshold1Color: "#111",
                            threshold2: 200,
                            threshold2Color: "#222",
                        };
                    });
                    common_1.it('should add grid markings', function () {
                        var markings = ctx.plotOptions.grid.markings;
                        common_1.expect(markings[0].yaxis.from).to.be(100);
                        common_1.expect(markings[0].yaxis.to).to.be(200);
                        common_1.expect(markings[0].color).to.be('#111');
                        common_1.expect(markings[1].yaxis.from).to.be(200);
                        common_1.expect(markings[1].yaxis.to).to.be(Infinity);
                    });
                });
                graphScenario('inverted grid thresholds 200, 100', function (ctx) {
                    ctx.setup(function (ctrl) {
                        ctrl.panel.grid = {
                            threshold1: 200,
                            threshold1Color: "#111",
                            threshold2: 100,
                            threshold2Color: "#222",
                        };
                    });
                    common_1.it('should add grid markings', function () {
                        var markings = ctx.plotOptions.grid.markings;
                        common_1.expect(markings[0].yaxis.from).to.be(200);
                        common_1.expect(markings[0].yaxis.to).to.be(100);
                        common_1.expect(markings[0].color).to.be('#111');
                        common_1.expect(markings[1].yaxis.from).to.be(100);
                        common_1.expect(markings[1].yaxis.to).to.be(-Infinity);
                    });
                });
                graphScenario('grid thresholds from zero', function (ctx) {
                    ctx.setup(function (ctrl) {
                        ctrl.panel.grid = {
                            threshold1: 0,
                            threshold1Color: "#111",
                        };
                    });
                    common_1.it('should add grid markings', function () {
                        var markings = ctx.plotOptions.grid.markings;
                        common_1.expect(markings[0].yaxis.from).to.be(0);
                    });
                });
                graphScenario('when logBase is log 10', function (ctx) {
                    ctx.setup(function (ctrl) {
                        ctrl.panel.yaxes[0].logBase = 10;
                    });
                    common_1.it('should apply axis transform and ticks', function () {
                        var axis = ctx.plotOptions.yaxes[0];
                        common_1.expect(axis.transform(100)).to.be(Math.log(100 + 0.1));
                        common_1.expect(axis.ticks[0]).to.be(0);
                        common_1.expect(axis.ticks[1]).to.be(1);
                    });
                });
                graphScenario('should use timeStep for barWidth', function (ctx) {
                    ctx.setup(function (ctrl, data) {
                        ctrl.panel.bars = true;
                        data[0] = new time_series2_1.default({
                            datapoints: [[1, 10], [2, 20]],
                            alias: 'series1',
                        });
                    });
                    common_1.it('should set barWidth', function () {
                        common_1.expect(ctx.plotOptions.series.bars.barWidth).to.be(10 / 1.5);
                    });
                });
                graphScenario('series option overrides, fill & points', function (ctx) {
                    ctx.setup(function (ctrl, data) {
                        ctrl.panel.lines = true;
                        ctrl.panel.fill = 5;
                        data[0].zindex = 10;
                        data[1].alias = 'test';
                        data[1].lines = { fill: 0.001 };
                        data[1].points = { show: true };
                    });
                    common_1.it('should match second series and fill zero, and enable points', function () {
                        common_1.expect(ctx.plotOptions.series.lines.fill).to.be(0.5);
                        common_1.expect(ctx.plotData[1].lines.fill).to.be(0.001);
                        common_1.expect(ctx.plotData[1].points.show).to.be(true);
                    });
                });
                graphScenario('should order series order according to zindex', function (ctx) {
                    ctx.setup(function (ctrl, data) {
                        data[1].zindex = 1;
                        data[0].zindex = 10;
                    });
                    common_1.it('should move zindex 2 last', function () {
                        common_1.expect(ctx.plotData[0].alias).to.be('series2');
                        common_1.expect(ctx.plotData[1].alias).to.be('series1');
                    });
                });
                graphScenario('when series is hidden', function (ctx) {
                    ctx.setup(function (ctrl) {
                        ctrl.hiddenSeries = { 'series2': true };
                    });
                    common_1.it('should remove datapoints and disable stack', function () {
                        common_1.expect(ctx.plotData[0].alias).to.be('series1');
                        common_1.expect(ctx.plotData[1].data.length).to.be(0);
                        common_1.expect(ctx.plotData[1].stack).to.be(false);
                    });
                });
                graphScenario('when stack and percent', function (ctx) {
                    ctx.setup(function (ctrl) {
                        ctrl.panel.percentage = true;
                        ctrl.panel.stack = true;
                    });
                    common_1.it('should show percentage', function () {
                        var axis = ctx.plotOptions.yaxes[0];
                        common_1.expect(axis.tickFormatter(100, axis)).to.be("100%");
                    });
                });
                graphScenario('when panel too narrow to show x-axis dates in same granularity as wide panels', function (ctx) {
                    common_1.describe('and the range is less than 24 hours', function () {
                        ctx.setup(function (ctrl) {
                            ctrl.range.from = moment_1.default([2015, 1, 1, 10]);
                            ctrl.range.to = moment_1.default([2015, 1, 1, 22]);
                        });
                        common_1.it('should format dates as hours minutes', function () {
                            var axis = ctx.plotOptions.xaxis;
                            common_1.expect(axis.timeformat).to.be('%H:%M');
                        });
                    });
                    common_1.describe('and the range is less than one year', function () {
                        ctx.setup(function (scope) {
                            scope.range.from = moment_1.default([2015, 1, 1]);
                            scope.range.to = moment_1.default([2015, 11, 20]);
                        });
                        common_1.it('should format dates as month days', function () {
                            var axis = ctx.plotOptions.xaxis;
                            common_1.expect(axis.timeformat).to.be('%m/%d');
                        });
                    });
                }, 10);
            });
        }
    }
});
//# sourceMappingURL=graph_specs.js.map