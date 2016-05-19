System.register(['test/lib/common', 'moment', 'angular', 'test/specs/helpers', "../datasource"], function(exports_1) {
    var common_1, moment_1, angular_1, helpers_1, datasource_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (helpers_1_1) {
                helpers_1 = helpers_1_1;
            },
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            }],
        execute: function() {
            common_1.describe('ElasticDatasource', function () {
                var ctx = new helpers_1.default.ServiceTestContext();
                var instanceSettings = { jsonData: {} };
                common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
                common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
                common_1.beforeEach(ctx.providePhase(['templateSrv', 'backendSrv']));
                common_1.beforeEach(common_1.angularMocks.inject(function ($q, $rootScope, $httpBackend, $injector) {
                    ctx.$q = $q;
                    ctx.$httpBackend = $httpBackend;
                    ctx.$rootScope = $rootScope;
                    ctx.$injector = $injector;
                    $httpBackend.when('GET', /\.html$/).respond('');
                }));
                function createDatasource(instanceSettings) {
                    instanceSettings.jsonData = instanceSettings.jsonData || {};
                    ctx.ds = ctx.$injector.instantiate(datasource_1.ElasticDatasource, { instanceSettings: instanceSettings });
                }
                common_1.describe('When testing datasource with index pattern', function () {
                    common_1.beforeEach(function () {
                        createDatasource({ url: 'http://es.com', index: '[asd-]YYYY.MM.DD', jsonData: { interval: 'Daily' } });
                    });
                    common_1.it('should translate index pattern to current day', function () {
                        var requestOptions;
                        ctx.backendSrv.datasourceRequest = function (options) {
                            requestOptions = options;
                            return ctx.$q.when({ data: {} });
                        };
                        ctx.ds.testDatasource();
                        ctx.$rootScope.$apply();
                        var today = moment_1.default.utc().format("YYYY.MM.DD");
                        common_1.expect(requestOptions.url).to.be("http://es.com/asd-" + today + '/_stats');
                    });
                });
                common_1.describe('When issueing metric query with interval pattern', function () {
                    var requestOptions, parts, header;
                    common_1.beforeEach(function () {
                        createDatasource({ url: 'http://es.com', index: '[asd-]YYYY.MM.DD', jsonData: { interval: 'Daily' } });
                        ctx.backendSrv.datasourceRequest = function (options) {
                            requestOptions = options;
                            return ctx.$q.when({ data: { responses: [] } });
                        };
                        ctx.ds.query({
                            range: {
                                from: moment_1.default([2015, 4, 30, 10]),
                                to: moment_1.default([2015, 5, 1, 10])
                            },
                            targets: [{ bucketAggs: [], metrics: [], query: 'escape\\:test' }]
                        });
                        ctx.$rootScope.$apply();
                        parts = requestOptions.data.split('\n');
                        header = angular_1.default.fromJson(parts[0]);
                    });
                    common_1.it('should translate index pattern to current day', function () {
                        common_1.expect(header.index).to.eql(['asd-2015.05.30', 'asd-2015.05.31', 'asd-2015.06.01']);
                    });
                    common_1.it('should json escape lucene query', function () {
                        var body = angular_1.default.fromJson(parts[1]);
                        common_1.expect(body.query.filtered.query.query_string.query).to.be('escape\\:test');
                    });
                });
                common_1.describe('When issueing document query', function () {
                    var requestOptions, parts, header;
                    common_1.beforeEach(function () {
                        createDatasource({ url: 'http://es.com', index: 'test' });
                        ctx.backendSrv.datasourceRequest = function (options) {
                            requestOptions = options;
                            return ctx.$q.when({ data: { responses: [] } });
                        };
                        ctx.ds.query({
                            range: { from: moment_1.default([2015, 4, 30, 10]), to: moment_1.default([2015, 5, 1, 10]) },
                            targets: [{ bucketAggs: [], metrics: [{ type: 'raw_document' }], query: 'test' }]
                        });
                        ctx.$rootScope.$apply();
                        parts = requestOptions.data.split('\n');
                        header = angular_1.default.fromJson(parts[0]);
                    });
                    common_1.it('should set search type to query_then_fetch', function () {
                        common_1.expect(header.search_type).to.eql('query_then_fetch');
                    });
                    common_1.it('should set size', function () {
                        var body = angular_1.default.fromJson(parts[1]);
                        common_1.expect(body.size).to.be(500);
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=datasource_specs.js.map