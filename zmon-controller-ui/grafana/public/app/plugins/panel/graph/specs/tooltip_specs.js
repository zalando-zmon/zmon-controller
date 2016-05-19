///<reference path="../../../../headers/common.d.ts" />
System.register(['../../../../../test/lib/common', 'jquery', '../graph_tooltip'], function(exports_1) {
    var common_1, jquery_1, graph_tooltip_1;
    var scope, elem, dashboard;
    function describeSharedTooltip(desc, fn) {
        var ctx = {};
        ctx.ctrl = scope.ctrl;
        ctx.ctrl.panel = {
            tooltip: {
                shared: true
            },
            legend: {},
            stack: false
        };
        ctx.setup = function (setupFn) {
            ctx.setupFn = setupFn;
        };
        common_1.describe(desc, function () {
            common_1.beforeEach(function () {
                ctx.setupFn();
                var tooltip = new graph_tooltip_1.default(elem, dashboard, scope);
                ctx.results = tooltip.getMultiSeriesPlotHoverInfo(ctx.data, ctx.pos);
            });
            fn(ctx);
        });
    }
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (jquery_1_1) {
                jquery_1 = jquery_1_1;
            },
            function (graph_tooltip_1_1) {
                graph_tooltip_1 = graph_tooltip_1_1;
            }],
        execute: function() {
            scope = {
                appEvent: common_1.sinon.spy(),
                onAppEvent: common_1.sinon.spy(),
                ctrl: {}
            };
            elem = jquery_1.default('<div></div>');
            dashboard = {};
            describeSharedTooltip("steppedLine false, stack false", function (ctx) {
                ctx.setup(function () {
                    ctx.data = [
                        { data: [[10, 15], [12, 20]], lines: {} },
                        { data: [[10, 2], [12, 3]], lines: {} }
                    ];
                    ctx.pos = { x: 11 };
                });
                common_1.it('should return 2 series', function () {
                    common_1.expect(ctx.results.length).to.be(2);
                });
                common_1.it('should add time to results array', function () {
                    common_1.expect(ctx.results.time).to.be(10);
                });
                common_1.it('should set value and hoverIndex', function () {
                    common_1.expect(ctx.results[0].value).to.be(15);
                    common_1.expect(ctx.results[1].value).to.be(2);
                    common_1.expect(ctx.results[0].hoverIndex).to.be(0);
                });
            });
            describeSharedTooltip("one series is hidden", function (ctx) {
                ctx.setup(function () {
                    ctx.data = [
                        { data: [[10, 15], [12, 20]], },
                        { data: [] }
                    ];
                    ctx.pos = { x: 11 };
                });
            });
            describeSharedTooltip("steppedLine false, stack true, individual false", function (ctx) {
                ctx.setup(function () {
                    ctx.data = [
                        {
                            data: [[10, 15], [12, 20]],
                            lines: {},
                            datapoints: {
                                pointsize: 2,
                                points: [[10, 15], [12, 20]],
                            },
                            stack: true,
                        },
                        {
                            data: [[10, 2], [12, 3]],
                            lines: {},
                            datapoints: {
                                pointsize: 2,
                                points: [[10, 2], [12, 3]],
                            },
                            stack: true
                        }
                    ];
                    ctx.ctrl.panel.stack = true;
                    ctx.pos = { x: 11 };
                });
                common_1.it('should show stacked value', function () {
                    common_1.expect(ctx.results[1].value).to.be(17);
                });
            });
            describeSharedTooltip("steppedLine false, stack true, individual false, series stack false", function (ctx) {
                ctx.setup(function () {
                    ctx.data = [
                        {
                            data: [[10, 15], [12, 20]],
                            lines: {},
                            datapoints: {
                                pointsize: 2,
                                points: [[10, 15], [12, 20]],
                            },
                            stack: true
                        },
                        {
                            data: [[10, 2], [12, 3]],
                            lines: {},
                            datapoints: {
                                pointsize: 2,
                                points: [[10, 2], [12, 3]],
                            },
                            stack: false
                        }
                    ];
                    ctx.ctrl.panel.stack = true;
                    ctx.pos = { x: 11 };
                });
                common_1.it('should not show stacked value', function () {
                    common_1.expect(ctx.results[1].value).to.be(2);
                });
            });
            describeSharedTooltip("steppedLine false, stack true, individual true", function (ctx) {
                ctx.setup(function () {
                    ctx.data = [
                        {
                            data: [[10, 15], [12, 20]],
                            lines: {},
                            datapoints: {
                                pointsize: 2,
                                points: [[10, 15], [12, 20]],
                            },
                            stack: true
                        },
                        {
                            data: [[10, 2], [12, 3]],
                            lines: {},
                            datapoints: {
                                pointsize: 2,
                                points: [[10, 2], [12, 3]],
                            },
                            stack: false
                        }
                    ];
                    ctx.ctrl.panel.stack = true;
                    ctx.ctrl.panel.tooltip.value_type = 'individual';
                    ctx.pos = { x: 11 };
                });
                common_1.it('should not show stacked value', function () {
                    common_1.expect(ctx.results[1].value).to.be(2);
                });
            });
        }
    }
});
//# sourceMappingURL=tooltip_specs.js.map