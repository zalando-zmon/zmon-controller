System.register(['../query_ctrl', 'app/core/services/segment_srv', 'test/lib/common', 'test/specs/helpers'], function(exports_1) {
    var common_1, helpers_1, query_ctrl_1;
    return {
        setters:[
            function (query_ctrl_1_1) {
                query_ctrl_1 = query_ctrl_1_1;
            },
            function (_1) {},
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (helpers_1_1) {
                helpers_1 = helpers_1_1;
            }],
        execute: function() {
            common_1.describe('InfluxDBQueryCtrl', function () {
                var ctx = new helpers_1.default.ControllerTestContext();
                common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
                common_1.beforeEach(common_1.angularMocks.module('grafana.controllers'));
                common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
                common_1.beforeEach(ctx.providePhase());
                common_1.beforeEach(common_1.angularMocks.inject(function ($rootScope, $controller, $q) {
                    ctx.$q = $q;
                    ctx.scope = $rootScope.$new();
                    ctx.datasource.metricFindQuery = common_1.sinon.stub().returns(ctx.$q.when([]));
                    ctx.panelCtrl = { panel: {} };
                    ctx.panelCtrl.refresh = common_1.sinon.spy();
                    ctx.target = { target: {} };
                    ctx.ctrl = $controller(query_ctrl_1.InfluxQueryCtrl, { $scope: ctx.scope }, {
                        panelCtrl: ctx.panelCtrl,
                        target: ctx.target,
                        datasource: ctx.datasource
                    });
                }));
                common_1.describe('init', function () {
                    common_1.it('should init tagSegments', function () {
                        common_1.expect(ctx.ctrl.tagSegments.length).to.be(1);
                    });
                    common_1.it('should init measurementSegment', function () {
                        common_1.expect(ctx.ctrl.measurementSegment.value).to.be('select measurement');
                    });
                });
                common_1.describe('when first tag segment is updated', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.tagSegmentUpdated({ value: 'asd', type: 'plus-button' }, 0);
                    });
                    common_1.it('should update tag key', function () {
                        common_1.expect(ctx.ctrl.target.tags[0].key).to.be('asd');
                        common_1.expect(ctx.ctrl.tagSegments[0].type).to.be('key');
                    });
                    common_1.it('should add tagSegments', function () {
                        common_1.expect(ctx.ctrl.tagSegments.length).to.be(3);
                    });
                });
                common_1.describe('when last tag value segment is updated', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.tagSegmentUpdated({ value: 'asd', type: 'plus-button' }, 0);
                        ctx.ctrl.tagSegmentUpdated({ value: 'server1', type: 'value' }, 2);
                    });
                    common_1.it('should update tag value', function () {
                        common_1.expect(ctx.ctrl.target.tags[0].value).to.be('server1');
                    });
                    common_1.it('should set tag operator', function () {
                        common_1.expect(ctx.ctrl.target.tags[0].operator).to.be('=');
                    });
                    common_1.it('should add plus button for another filter', function () {
                        common_1.expect(ctx.ctrl.tagSegments[3].fake).to.be(true);
                    });
                });
                common_1.describe('when last tag value segment is updated to regex', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.tagSegmentUpdated({ value: 'asd', type: 'plus-button' }, 0);
                        ctx.ctrl.tagSegmentUpdated({ value: '/server.*/', type: 'value' }, 2);
                    });
                    common_1.it('should update operator', function () {
                        common_1.expect(ctx.ctrl.tagSegments[1].value).to.be('=~');
                        common_1.expect(ctx.ctrl.target.tags[0].operator).to.be('=~');
                    });
                });
                common_1.describe('when second tag key is added', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.tagSegmentUpdated({ value: 'asd', type: 'plus-button' }, 0);
                        ctx.ctrl.tagSegmentUpdated({ value: 'server1', type: 'value' }, 2);
                        ctx.ctrl.tagSegmentUpdated({ value: 'key2', type: 'plus-button' }, 3);
                    });
                    common_1.it('should update tag key', function () {
                        common_1.expect(ctx.ctrl.target.tags[1].key).to.be('key2');
                    });
                    common_1.it('should add AND segment', function () {
                        common_1.expect(ctx.ctrl.tagSegments[3].value).to.be('AND');
                    });
                });
                common_1.describe('when condition is changed', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.tagSegmentUpdated({ value: 'asd', type: 'plus-button' }, 0);
                        ctx.ctrl.tagSegmentUpdated({ value: 'server1', type: 'value' }, 2);
                        ctx.ctrl.tagSegmentUpdated({ value: 'key2', type: 'plus-button' }, 3);
                        ctx.ctrl.tagSegmentUpdated({ value: 'OR', type: 'condition' }, 3);
                    });
                    common_1.it('should update tag condition', function () {
                        common_1.expect(ctx.ctrl.target.tags[1].condition).to.be('OR');
                    });
                    common_1.it('should update AND segment', function () {
                        common_1.expect(ctx.ctrl.tagSegments[3].value).to.be('OR');
                        common_1.expect(ctx.ctrl.tagSegments.length).to.be(7);
                    });
                });
                common_1.describe('when deleting first tag filter after value is selected', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.tagSegmentUpdated({ value: 'asd', type: 'plus-button' }, 0);
                        ctx.ctrl.tagSegmentUpdated({ value: 'server1', type: 'value' }, 2);
                        ctx.ctrl.tagSegmentUpdated(ctx.ctrl.removeTagFilterSegment, 0);
                    });
                    common_1.it('should remove tags', function () {
                        common_1.expect(ctx.ctrl.target.tags.length).to.be(0);
                    });
                    common_1.it('should remove all segment after 2 and replace with plus button', function () {
                        common_1.expect(ctx.ctrl.tagSegments.length).to.be(1);
                        common_1.expect(ctx.ctrl.tagSegments[0].type).to.be('plus-button');
                    });
                });
                common_1.describe('when deleting second tag value before second tag value is complete', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.tagSegmentUpdated({ value: 'asd', type: 'plus-button' }, 0);
                        ctx.ctrl.tagSegmentUpdated({ value: 'server1', type: 'value' }, 2);
                        ctx.ctrl.tagSegmentUpdated({ value: 'key2', type: 'plus-button' }, 3);
                        ctx.ctrl.tagSegmentUpdated(ctx.ctrl.removeTagFilterSegment, 4);
                    });
                    common_1.it('should remove all segment after 2 and replace with plus button', function () {
                        common_1.expect(ctx.ctrl.tagSegments.length).to.be(4);
                        common_1.expect(ctx.ctrl.tagSegments[3].type).to.be('plus-button');
                    });
                });
                common_1.describe('when deleting second tag value before second tag value is complete', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.tagSegmentUpdated({ value: 'asd', type: 'plus-button' }, 0);
                        ctx.ctrl.tagSegmentUpdated({ value: 'server1', type: 'value' }, 2);
                        ctx.ctrl.tagSegmentUpdated({ value: 'key2', type: 'plus-button' }, 3);
                        ctx.ctrl.tagSegmentUpdated(ctx.ctrl.removeTagFilterSegment, 4);
                    });
                    common_1.it('should remove all segment after 2 and replace with plus button', function () {
                        common_1.expect(ctx.ctrl.tagSegments.length).to.be(4);
                        common_1.expect(ctx.ctrl.tagSegments[3].type).to.be('plus-button');
                    });
                });
                common_1.describe('when deleting second tag value after second tag filter is complete', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.tagSegmentUpdated({ value: 'asd', type: 'plus-button' }, 0);
                        ctx.ctrl.tagSegmentUpdated({ value: 'server1', type: 'value' }, 2);
                        ctx.ctrl.tagSegmentUpdated({ value: 'key2', type: 'plus-button' }, 3);
                        ctx.ctrl.tagSegmentUpdated({ value: 'value', type: 'value' }, 6);
                        ctx.ctrl.tagSegmentUpdated(ctx.ctrl.removeTagFilterSegment, 4);
                    });
                    common_1.it('should remove all segment after 2 and replace with plus button', function () {
                        common_1.expect(ctx.ctrl.tagSegments.length).to.be(4);
                        common_1.expect(ctx.ctrl.tagSegments[3].type).to.be('plus-button');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=query_ctrl_specs.js.map