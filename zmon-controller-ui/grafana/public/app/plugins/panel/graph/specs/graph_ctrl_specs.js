///<reference path="../../../../headers/common.d.ts" />
System.register(['../../../../../test/lib/common', '../module', '../../../../../test/specs/helpers'], function(exports_1) {
    var common_1, module_1, helpers_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (module_1_1) {
                module_1 = module_1_1;
            },
            function (helpers_1_1) {
                helpers_1 = helpers_1_1;
            }],
        execute: function() {
            common_1.describe('GraphCtrl', function () {
                var ctx = new helpers_1.default.ControllerTestContext();
                common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
                common_1.beforeEach(common_1.angularMocks.module('grafana.controllers'));
                common_1.beforeEach(ctx.providePhase());
                common_1.beforeEach(ctx.createPanelController(module_1.GraphCtrl));
                common_1.beforeEach(function () {
                    ctx.ctrl.annotationsPromise = Promise.resolve({});
                    ctx.ctrl.updateTimeRange();
                });
                common_1.describe('msResolution with second resolution timestamps', function () {
                    common_1.beforeEach(function () {
                        var data = [
                            { target: 'test.cpu1', datapoints: [[1234567890, 45], [1234567899, 60]] },
                            { target: 'test.cpu2', datapoints: [[1236547890, 55], [1234456709, 90]] }
                        ];
                        ctx.ctrl.panel.tooltip.msResolution = false;
                        ctx.ctrl.onDataReceived(data);
                    });
                    common_1.it('should not show millisecond resolution tooltip', function () {
                        common_1.expect(ctx.ctrl.panel.tooltip.msResolution).to.be(false);
                    });
                });
                common_1.describe('msResolution with millisecond resolution timestamps', function () {
                    common_1.beforeEach(function () {
                        var data = [
                            { target: 'test.cpu1', datapoints: [[1234567890000, 45], [1234567899000, 60]] },
                            { target: 'test.cpu2', datapoints: [[1236547890001, 55], [1234456709000, 90]] }
                        ];
                        ctx.ctrl.panel.tooltip.msResolution = false;
                        ctx.ctrl.onDataReceived(data);
                    });
                    common_1.it('should show millisecond resolution tooltip', function () {
                        common_1.expect(ctx.ctrl.panel.tooltip.msResolution).to.be(true);
                    });
                });
                common_1.describe('msResolution with millisecond resolution timestamps but with trailing zeroes', function () {
                    common_1.beforeEach(function () {
                        var data = [
                            { target: 'test.cpu1', datapoints: [[1234567890000, 45], [1234567899000, 60]] },
                            { target: 'test.cpu2', datapoints: [[1236547890000, 55], [1234456709000, 90]] }
                        ];
                        ctx.ctrl.panel.tooltip.msResolution = false;
                        ctx.ctrl.onDataReceived(data);
                    });
                    common_1.it('should not show millisecond resolution tooltip', function () {
                        common_1.expect(ctx.ctrl.panel.tooltip.msResolution).to.be(false);
                    });
                });
                common_1.describe('msResolution with millisecond resolution timestamps in one of the series', function () {
                    common_1.beforeEach(function () {
                        var data = [
                            { target: 'test.cpu1', datapoints: [[1234567890000, 45], [1234567899000, 60]] },
                            { target: 'test.cpu2', datapoints: [[1236547890010, 55], [1234456709000, 90]] },
                            { target: 'test.cpu3', datapoints: [[1236547890000, 65], [1234456709000, 120]] }
                        ];
                        ctx.ctrl.panel.tooltip.msResolution = false;
                        ctx.ctrl.onDataReceived(data);
                    });
                    common_1.it('should show millisecond resolution tooltip', function () {
                        common_1.expect(ctx.ctrl.panel.tooltip.msResolution).to.be(true);
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=graph_ctrl_specs.js.map