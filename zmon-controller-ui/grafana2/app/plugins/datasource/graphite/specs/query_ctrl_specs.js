///<amd-dependency path="app/plugins/datasource/graphite/gfunc" name="gfunc"/>
///<amd-dependency path="app/plugins/datasource/graphite/query_ctrl" />
///<amd-dependency path="app/core/services/segment_srv" />
///<amd-dependency path="test/specs/helpers" name="helpers" />
define(["require", "exports", "app/plugins/datasource/graphite/gfunc", "test/specs/helpers", 'test/lib/common', "app/plugins/datasource/graphite/query_ctrl", "app/core/services/segment_srv"], function (require, exports, gfunc, helpers, common_1) {
    common_1.describe('GraphiteQueryCtrl', function () {
        var ctx = new helpers.ControllerTestContext();
        common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
        common_1.beforeEach(common_1.angularMocks.module('grafana.controllers'));
        common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
        common_1.beforeEach(ctx.providePhase());
        common_1.beforeEach(ctx.createControllerPhase('GraphiteQueryCtrl'));
        common_1.beforeEach(function () {
            ctx.scope.target = { target: 'aliasByNode(scaleToSeconds(test.prod.*,1),2)' };
            ctx.scope.datasource = ctx.datasource;
            ctx.scope.datasource.metricFindQuery = common_1.sinon.stub().returns(ctx.$q.when([]));
        });
        common_1.describe('init', function () {
            common_1.beforeEach(function () {
                ctx.scope.init();
                ctx.scope.$digest();
            });
            common_1.it('should validate metric key exists', function () {
                common_1.expect(ctx.scope.datasource.metricFindQuery.getCall(0).args[0]).to.be('test.prod.*');
            });
            common_1.it('should delete last segment if no metrics are found', function () {
                common_1.expect(ctx.scope.segments[2].value).to.be('select metric');
            });
            common_1.it('should parse expression and build function model', function () {
                common_1.expect(ctx.scope.functions.length).to.be(2);
            });
        });
        common_1.describe('when adding function', function () {
            common_1.beforeEach(function () {
                ctx.scope.target.target = 'test.prod.*.count';
                ctx.scope.datasource.metricFindQuery.returns(ctx.$q.when([{ expandable: false }]));
                ctx.scope.init();
                ctx.scope.$digest();
                ctx.scope.$parent = { get_data: common_1.sinon.spy() };
                ctx.scope.addFunction(gfunc.getFuncDef('aliasByNode'));
            });
            common_1.it('should add function with correct node number', function () {
                common_1.expect(ctx.scope.functions[0].params[0]).to.be(2);
            });
            common_1.it('should update target', function () {
                common_1.expect(ctx.scope.target.target).to.be('aliasByNode(test.prod.*.count, 2)');
            });
            common_1.it('should call get_data', function () {
                common_1.expect(ctx.scope.$parent.get_data.called).to.be(true);
            });
        });
        common_1.describe('when adding function before any metric segment', function () {
            common_1.beforeEach(function () {
                ctx.scope.target.target = '';
                ctx.scope.datasource.metricFindQuery.returns(ctx.$q.when([{ expandable: true }]));
                ctx.scope.init();
                ctx.scope.$digest();
                ctx.scope.$parent = { get_data: common_1.sinon.spy() };
                ctx.scope.addFunction(gfunc.getFuncDef('asPercent'));
            });
            common_1.it('should add function and remove select metric link', function () {
                common_1.expect(ctx.scope.segments.length).to.be(0);
            });
        });
        common_1.describe('when initalizing target without metric expression and only function', function () {
            common_1.beforeEach(function () {
                ctx.scope.target.target = 'asPercent(#A, #B)';
                ctx.scope.datasource.metricFindQuery.returns(ctx.$q.when([]));
                ctx.scope.init();
                ctx.scope.$digest();
                ctx.scope.$parent = { get_data: common_1.sinon.spy() };
            });
            common_1.it('should not add select metric segment', function () {
                common_1.expect(ctx.scope.segments.length).to.be(0);
            });
            common_1.it('should add both series refs as params', function () {
                common_1.expect(ctx.scope.functions[0].params.length).to.be(2);
            });
        });
        common_1.describe('when initializing a target with single param func using variable', function () {
            common_1.beforeEach(function () {
                ctx.scope.target.target = 'movingAverage(prod.count, $var)';
                ctx.scope.datasource.metricFindQuery.returns(ctx.$q.when([]));
                ctx.scope.init();
                ctx.scope.$digest();
                ctx.scope.$parent = { get_data: common_1.sinon.spy() };
            });
            common_1.it('should add 2 segments', function () {
                common_1.expect(ctx.scope.segments.length).to.be(2);
            });
            common_1.it('should add function param', function () {
                common_1.expect(ctx.scope.functions[0].params.length).to.be(1);
            });
        });
        common_1.describe('when initalizing target without metric expression and function with series-ref', function () {
            common_1.beforeEach(function () {
                ctx.scope.target.target = 'asPercent(metric.node.count, #A)';
                ctx.scope.datasource.metricFindQuery.returns(ctx.$q.when([]));
                ctx.scope.init();
                ctx.scope.$digest();
                ctx.scope.$parent = { get_data: common_1.sinon.spy() };
            });
            common_1.it('should add segments', function () {
                common_1.expect(ctx.scope.segments.length).to.be(3);
            });
            common_1.it('should have correct func params', function () {
                common_1.expect(ctx.scope.functions[0].params.length).to.be(1);
            });
        });
        common_1.describe('when getting altSegments and metricFindQuery retuns empty array', function () {
            common_1.beforeEach(function () {
                ctx.scope.target.target = 'test.count';
                ctx.scope.datasource.metricFindQuery.returns(ctx.$q.when([]));
                ctx.scope.init();
                ctx.scope.getAltSegments(1).then(function (results) {
                    ctx.altSegments = results;
                });
                ctx.scope.$digest();
                ctx.scope.$parent = { get_data: common_1.sinon.spy() };
            });
            common_1.it('should have no segments', function () {
                common_1.expect(ctx.altSegments.length).to.be(0);
            });
        });
        common_1.describe('targetChanged', function () {
            common_1.beforeEach(function () {
                ctx.scope.datasource.metricFindQuery.returns(ctx.$q.when([{ expandable: false }]));
                ctx.scope.init();
                ctx.scope.$digest();
                ctx.scope.$parent = { get_data: common_1.sinon.spy() };
                ctx.scope.target.target = '';
                ctx.scope.targetChanged();
            });
            common_1.it('should rebuld target after expression model', function () {
                common_1.expect(ctx.scope.target.target).to.be('aliasByNode(scaleToSeconds(test.prod.*, 1), 2)');
            });
            common_1.it('should call get_data', function () {
                common_1.expect(ctx.scope.$parent.get_data.called).to.be(true);
            });
        });
    });
});
//# sourceMappingURL=query_ctrl_specs.js.map