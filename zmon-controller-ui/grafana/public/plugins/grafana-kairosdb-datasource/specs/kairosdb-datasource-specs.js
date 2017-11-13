System.register(["test/lib/common", 'test/specs/helpers', '../datasource'], function(exports_1) {
    var common_1, helpers_1, datasource_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (helpers_1_1) {
                helpers_1 = helpers_1_1;
            },
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            }],
        execute: function() {
            common_1.describe('KairosDBDatasource', function () {
                var ctx = new helpers_1.default.ServiceTestContext();
                var instanceSettings = {
                    url: "",
                    name: "KairosDB datasource 1",
                    withCredentials: false,
                    type: "kairosdb"
                };
                common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
                common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
                common_1.beforeEach(common_1.angularMocks.inject(function ($q, $rootScope, $httpBackend, $injector) {
                    ctx.$q = $q;
                    ctx.$httpBackend = $httpBackend;
                    ctx.$rootScope = $rootScope;
                    ctx.ds = $injector.instantiate(datasource_1.KairosDBDatasource, { instanceSettings: instanceSettings });
                    $httpBackend.when('GET', /\.html$/).respond('');
                }));
                common_1.describe('When querying kairosdb with one target using query editor target spec', function () {
                    var results;
                    var urlExpected = "/api/v1/datapoints/query";
                    var bodyExpected = {
                        metrics: [{ name: ["test"] }],
                        cache_time: 0,
                        start_relative: {
                            value: "1",
                            unit: "hours"
                        }
                    };
                    var query = {
                        rangeRaw: { from: 'now-1h', to: 'now' },
                        targets: [{ metric: 'test', downsampling: '(NONE)' }]
                    };
                    var response = {
                        queries: [{
                                sample_size: 60,
                                results: [{
                                        name: "test",
                                        values: [[1420070400000, 1]]
                                    }]
                            }]
                    };
                    common_1.beforeEach(function () {
                        ctx.$httpBackend.expect('POST', urlExpected, bodyExpected).respond(response);
                        ctx.ds.query(query).then(function (data) { results = data; });
                        ctx.$httpBackend.flush();
                    });
                    common_1.it('should generate the correct query', function () {
                        ctx.$httpBackend.verifyNoOutstandingExpectation();
                    });
                    common_1.it('should return series list', function () {
                        common_1.expect(results.data.length).to.be(1);
                        common_1.expect(results.data[0].target).to.be('test');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=kairosdb-datasource-specs.js.map