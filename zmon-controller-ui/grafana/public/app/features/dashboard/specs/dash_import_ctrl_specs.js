System.register(['test/lib/common', 'app/features/dashboard/import/dash_import', 'app/core/config'], function(exports_1) {
    var common_1, dash_import_1, config_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (dash_import_1_1) {
                dash_import_1 = dash_import_1_1;
            },
            function (config_1_1) {
                config_1 = config_1_1;
            }],
        execute: function() {
            common_1.describe('DashImportCtrl', function () {
                var ctx = {};
                var backendSrv = {
                    search: common_1.sinon.stub().returns(Promise.resolve([])),
                    get: common_1.sinon.stub()
                };
                common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
                common_1.beforeEach(common_1.angularMocks.inject(function ($rootScope, $controller, $q) {
                    ctx.$q = $q;
                    ctx.scope = $rootScope.$new();
                    ctx.ctrl = $controller(dash_import_1.DashImportCtrl, {
                        $scope: ctx.scope,
                        backendSrv: backendSrv,
                    });
                }));
                common_1.describe('when uploading json', function () {
                    common_1.beforeEach(function () {
                        config_1.default.datasources = {
                            ds: {
                                type: 'test-db',
                            }
                        };
                        ctx.ctrl.onUpload({
                            '__inputs': [
                                { name: 'ds', pluginId: 'test-db', type: 'datasource', pluginName: 'Test DB' }
                            ]
                        });
                    });
                    common_1.it('should build input model', function () {
                        common_1.expect(ctx.ctrl.inputs.length).to.eql(1);
                        common_1.expect(ctx.ctrl.inputs[0].name).to.eql('ds');
                        common_1.expect(ctx.ctrl.inputs[0].info).to.eql('Select a Test DB data source');
                    });
                    common_1.it('should set inputValid to false', function () {
                        common_1.expect(ctx.ctrl.inputsValid).to.eql(false);
                    });
                });
                common_1.describe('when specifing grafana.net url', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.gnetUrl = 'http://grafana.net/dashboards/123';
                        // setup api mock
                        backendSrv.get = common_1.sinon.spy(function () {
                            return Promise.resolve({});
                        });
                        ctx.ctrl.checkGnetDashboard();
                    });
                    common_1.it('should call gnet api with correct dashboard id', function () {
                        common_1.expect(backendSrv.get.getCall(0).args[0]).to.eql('api/gnet/dashboards/123');
                    });
                });
                common_1.describe('when specifing dashbord id', function () {
                    common_1.beforeEach(function () {
                        ctx.ctrl.gnetUrl = '2342';
                        // setup api mock
                        backendSrv.get = common_1.sinon.spy(function () {
                            return Promise.resolve({});
                        });
                        ctx.ctrl.checkGnetDashboard();
                    });
                    common_1.it('should call gnet api with correct dashboard id', function () {
                        common_1.expect(backendSrv.get.getCall(0).args[0]).to.eql('api/gnet/dashboards/2342');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=dash_import_ctrl_specs.js.map