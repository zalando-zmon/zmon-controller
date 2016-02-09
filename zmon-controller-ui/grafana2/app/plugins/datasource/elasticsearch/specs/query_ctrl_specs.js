///<amd-dependency path="../query_ctrl" />
///<amd-dependency path="app/core/services/segment_srv" />
///<amd-dependency path="test/specs/helpers" name="helpers" />
define(["require", "exports", "test/specs/helpers", 'test/lib/common', "../query_ctrl", "app/core/services/segment_srv"], function (require, exports, helpers, common_1) {
    common_1.describe('ElasticQueryCtrl', function () {
        var ctx = new helpers.ControllerTestContext();
        common_1.beforeEach(common_1.angularMocks.module('grafana.controllers'));
        common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
        common_1.beforeEach(ctx.providePhase());
        common_1.beforeEach(ctx.createControllerPhase('ElasticQueryCtrl'));
        common_1.beforeEach(function () {
            ctx.scope.target = {};
            ctx.scope.$parent = { get_data: common_1.sinon.spy() };
            ctx.scope.datasource = ctx.datasource;
            ctx.scope.datasource.metricFindQuery = common_1.sinon.stub().returns(ctx.$q.when([]));
        });
        common_1.describe('init', function () {
            common_1.beforeEach(function () {
                ctx.scope.init();
            });
        });
    });
});
//# sourceMappingURL=query_ctrl_specs.js.map