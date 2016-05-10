///<reference path="../../../../headers/common.d.ts" />
System.register(['../../../../../test/lib/common', '../../../../../test/specs/helpers', '../module'], function(exports_1) {
    var common_1, helpers_1, module_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (helpers_1_1) {
                helpers_1 = helpers_1_1;
            },
            function (module_1_1) {
                module_1 = module_1_1;
            }],
        execute: function() {
            common_1.describe('SingleStatCtrl', function () {
                var ctx = new helpers_1.default.ControllerTestContext();
                function singleStatScenario(desc, func) {
                    common_1.describe(desc, function () {
                        ctx.setup = function (setupFunc) {
                            common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
                            common_1.beforeEach(common_1.angularMocks.module('grafana.controllers'));
                            common_1.beforeEach(ctx.providePhase());
                            common_1.beforeEach(ctx.createPanelController(module_1.SingleStatCtrl));
                            common_1.beforeEach(function () {
                                setupFunc();
                                var data = [
                                    { target: 'test.cpu1', datapoints: ctx.datapoints }
                                ];
                                ctx.ctrl.onDataReceived(data);
                                ctx.data = ctx.ctrl.data;
                            });
                        };
                        func(ctx);
                    });
                }
                singleStatScenario('with defaults', function (ctx) {
                    ctx.setup(function () {
                        ctx.datapoints = [[10, 1], [20, 2]];
                    });
                    common_1.it('Should use series avg as default main value', function () {
                        common_1.expect(ctx.data.value).to.be(15);
                        common_1.expect(ctx.data.valueRounded).to.be(15);
                    });
                    common_1.it('should set formated falue', function () {
                        common_1.expect(ctx.data.valueFormated).to.be('15');
                    });
                });
                singleStatScenario('MainValue should use same number for decimals as displayed when checking thresholds', function (ctx) {
                    ctx.setup(function () {
                        ctx.datapoints = [[99.999, 1], [99.99999, 2]];
                    });
                    common_1.it('Should be rounded', function () {
                        common_1.expect(ctx.data.value).to.be(99.999495);
                        common_1.expect(ctx.data.valueRounded).to.be(100);
                    });
                    common_1.it('should set formated falue', function () {
                        common_1.expect(ctx.data.valueFormated).to.be('100');
                    });
                });
                singleStatScenario('When value to text mapping is specified', function (ctx) {
                    ctx.setup(function () {
                        ctx.datapoints = [[9.9, 1]];
                        ctx.ctrl.panel.valueMaps = [{ value: '10', text: 'OK' }];
                    });
                    common_1.it('value should remain', function () {
                        common_1.expect(ctx.data.value).to.be(9.9);
                    });
                    common_1.it('round should be rounded up', function () {
                        common_1.expect(ctx.data.valueRounded).to.be(10);
                    });
                    common_1.it('Should replace value with text', function () {
                        common_1.expect(ctx.data.valueFormated).to.be('OK');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=singlestat-specs.js.map