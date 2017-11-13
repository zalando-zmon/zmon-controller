System.register(['test/lib/common', 'app/features/dashboard/dashboardSrv', '../dynamic_dashboard_srv'], function(exports_1) {
    var common_1, dynamic_dashboard_srv_1;
    function dynamicDashScenario(desc, func) {
        common_1.describe(desc, function () {
            var ctx = {};
            ctx.setup = function (setupFunc) {
                common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
                common_1.beforeEach(common_1.angularMocks.module(function ($provide) {
                    $provide.value('contextSrv', {
                        user: { timezone: 'utc' }
                    });
                }));
                common_1.beforeEach(common_1.angularMocks.inject(function (dashboardSrv) {
                    ctx.dashboardSrv = dashboardSrv;
                    var model = {
                        rows: [],
                        templating: { list: [] }
                    };
                    setupFunc(model);
                    ctx.dash = ctx.dashboardSrv.create(model);
                    ctx.dynamicDashboardSrv = new dynamic_dashboard_srv_1.DynamicDashboardSrv();
                    ctx.dynamicDashboardSrv.init(ctx.dash);
                    ctx.rows = ctx.dash.rows;
                }));
            };
            func(ctx);
        });
    }
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (_1) {},
            function (dynamic_dashboard_srv_1_1) {
                dynamic_dashboard_srv_1 = dynamic_dashboard_srv_1_1;
            }],
        execute: function() {
            dynamicDashScenario('given dashboard with panel repeat', function (ctx) {
                ctx.setup(function (dash) {
                    dash.rows.push({
                        panels: [{ id: 2, repeat: 'apps' }]
                    });
                    dash.templating.list.push({
                        name: 'apps',
                        current: {
                            text: 'se1, se2, se3',
                            value: ['se1', 'se2', 'se3']
                        },
                        options: [
                            { text: 'se1', value: 'se1', selected: true },
                            { text: 'se2', value: 'se2', selected: true },
                            { text: 'se3', value: 'se3', selected: true },
                            { text: 'se4', value: 'se4', selected: false }
                        ]
                    });
                });
                common_1.it('should repeat panel one time', function () {
                    common_1.expect(ctx.rows[0].panels.length).to.be(3);
                });
                common_1.it('should mark panel repeated', function () {
                    common_1.expect(ctx.rows[0].panels[0].repeat).to.be('apps');
                    common_1.expect(ctx.rows[0].panels[1].repeatPanelId).to.be(2);
                });
                common_1.it('should set scopedVars on panels', function () {
                    common_1.expect(ctx.rows[0].panels[0].scopedVars.apps.value).to.be('se1');
                    common_1.expect(ctx.rows[0].panels[1].scopedVars.apps.value).to.be('se2');
                    common_1.expect(ctx.rows[0].panels[2].scopedVars.apps.value).to.be('se3');
                });
                common_1.describe('After a second iteration', function () {
                    var repeatedPanelAfterIteration1;
                    common_1.beforeEach(function () {
                        repeatedPanelAfterIteration1 = ctx.rows[0].panels[1];
                        ctx.rows[0].panels[0].fill = 10;
                        ctx.dynamicDashboardSrv.update(ctx.dash);
                    });
                    common_1.it('should have reused same panel instances', function () {
                        common_1.expect(ctx.rows[0].panels[1]).to.be(repeatedPanelAfterIteration1);
                    });
                    common_1.it('reused panel should copy properties from source', function () {
                        common_1.expect(ctx.rows[0].panels[1].fill).to.be(10);
                    });
                    common_1.it('should have same panel count', function () {
                        common_1.expect(ctx.rows[0].panels.length).to.be(3);
                    });
                });
                common_1.describe('After a second iteration and selected values reduced', function () {
                    common_1.beforeEach(function () {
                        ctx.dash.templating.list[0].options[1].selected = false;
                        ctx.dynamicDashboardSrv.update(ctx.dash);
                    });
                    common_1.it('should clean up repeated panel', function () {
                        common_1.expect(ctx.rows[0].panels.length).to.be(2);
                    });
                });
                common_1.describe('After a second iteration and panel repeat is turned off', function () {
                    common_1.beforeEach(function () {
                        ctx.rows[0].panels[0].repeat = null;
                        ctx.dynamicDashboardSrv.update(ctx.dash);
                    });
                    common_1.it('should clean up repeated panel', function () {
                        common_1.expect(ctx.rows[0].panels.length).to.be(1);
                    });
                    common_1.it('should remove scoped vars from reused panel', function () {
                        common_1.expect(ctx.rows[0].panels[0].scopedVars).to.be.empty();
                    });
                });
            });
            dynamicDashScenario('given dashboard with row repeat', function (ctx) {
                ctx.setup(function (dash) {
                    dash.rows.push({
                        repeat: 'servers',
                        panels: [{ id: 2 }]
                    });
                    dash.rows.push({ panels: [] });
                    dash.templating.list.push({
                        name: 'servers',
                        current: {
                            text: 'se1, se2',
                            value: ['se1', 'se2']
                        },
                        options: [
                            { text: 'se1', value: 'se1', selected: true },
                            { text: 'se2', value: 'se2', selected: true },
                        ]
                    });
                });
                common_1.it('should repeat row one time', function () {
                    common_1.expect(ctx.rows.length).to.be(3);
                });
                common_1.it('should keep panel ids on first row', function () {
                    common_1.expect(ctx.rows[0].panels[0].id).to.be(2);
                });
                common_1.it('should keep first row as repeat', function () {
                    common_1.expect(ctx.rows[0].repeat).to.be('servers');
                });
                common_1.it('should clear repeat field on repeated row', function () {
                    common_1.expect(ctx.rows[1].repeat).to.be(null);
                });
                common_1.it('should add scopedVars to rows', function () {
                    common_1.expect(ctx.rows[0].scopedVars.servers.value).to.be('se1');
                    common_1.expect(ctx.rows[1].scopedVars.servers.value).to.be('se2');
                });
                common_1.it('should generate a repeartRowId based on repeat row index', function () {
                    common_1.expect(ctx.rows[1].repeatRowId).to.be(1);
                });
                common_1.it('should set scopedVars on row panels', function () {
                    common_1.expect(ctx.rows[0].panels[0].scopedVars.servers.value).to.be('se1');
                    common_1.expect(ctx.rows[1].panels[0].scopedVars.servers.value).to.be('se2');
                });
                common_1.describe('After a second iteration', function () {
                    var repeatedRowAfterFirstIteration;
                    common_1.beforeEach(function () {
                        repeatedRowAfterFirstIteration = ctx.rows[1];
                        ctx.rows[0].height = 500;
                        ctx.dynamicDashboardSrv.update(ctx.dash);
                    });
                    common_1.it('should still only have 2 rows', function () {
                        common_1.expect(ctx.rows.length).to.be(3);
                    });
                    common_1.it.skip('should have updated props from source', function () {
                        common_1.expect(ctx.rows[1].height).to.be(500);
                    });
                    common_1.it('should reuse row instance', function () {
                        common_1.expect(ctx.rows[1]).to.be(repeatedRowAfterFirstIteration);
                    });
                });
                common_1.describe('After a second iteration and selected values reduced', function () {
                    common_1.beforeEach(function () {
                        ctx.dash.templating.list[0].options[1].selected = false;
                        ctx.dynamicDashboardSrv.update(ctx.dash);
                    });
                    common_1.it('should remove repeated second row', function () {
                        common_1.expect(ctx.rows.length).to.be(2);
                    });
                });
            });
            dynamicDashScenario('given dashboard with row repeat and panel repeat', function (ctx) {
                ctx.setup(function (dash) {
                    dash.rows.push({
                        repeat: 'servers',
                        panels: [{ id: 2, repeat: 'metric' }]
                    });
                    dash.templating.list.push({
                        name: 'servers',
                        current: { text: 'se1, se2', value: ['se1', 'se2'] },
                        options: [
                            { text: 'se1', value: 'se1', selected: true },
                            { text: 'se2', value: 'se2', selected: true },
                        ]
                    });
                    dash.templating.list.push({
                        name: 'metric',
                        current: { text: 'm1, m2', value: ['m1', 'm2'] },
                        options: [
                            { text: 'm1', value: 'm1', selected: true },
                            { text: 'm2', value: 'm2', selected: true },
                        ]
                    });
                });
                common_1.it('should repeat row one time', function () {
                    common_1.expect(ctx.rows.length).to.be(2);
                });
                common_1.it('should repeat panel on both rows', function () {
                    common_1.expect(ctx.rows[0].panels.length).to.be(2);
                    common_1.expect(ctx.rows[1].panels.length).to.be(2);
                });
                common_1.it('should keep panel ids on first row', function () {
                    common_1.expect(ctx.rows[0].panels[0].id).to.be(2);
                });
                common_1.it('should mark second row as repeated', function () {
                    common_1.expect(ctx.rows[0].repeat).to.be('servers');
                });
                common_1.it('should clear repeat field on repeated row', function () {
                    common_1.expect(ctx.rows[1].repeat).to.be(null);
                });
                common_1.it('should generate a repeartRowId based on repeat row index', function () {
                    common_1.expect(ctx.rows[1].repeatRowId).to.be(1);
                });
                common_1.it('should set scopedVars on row panels', function () {
                    common_1.expect(ctx.rows[0].panels[0].scopedVars.servers.value).to.be('se1');
                    common_1.expect(ctx.rows[1].panels[0].scopedVars.servers.value).to.be('se2');
                });
            });
        }
    }
});
//# sourceMappingURL=dynamic_dashboard_srv_specs.js.map