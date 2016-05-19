System.register(['test/lib/common', 'test/specs/helpers', "../datasource"], function(exports_1) {
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
            common_1.describe('graphiteDatasource', function () {
                var ctx = new helpers_1.default.ServiceTestContext();
                var instanceSettings = { url: [''] };
                common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
                common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
                common_1.beforeEach(ctx.providePhase(['backendSrv']));
                common_1.beforeEach(common_1.angularMocks.inject(function ($q, $rootScope, $httpBackend, $injector) {
                    ctx.$q = $q;
                    ctx.$httpBackend = $httpBackend;
                    ctx.$rootScope = $rootScope;
                    ctx.$injector = $injector;
                    $httpBackend.when('GET', /\.html$/).respond('');
                }));
                common_1.beforeEach(function () {
                    ctx.ds = ctx.$injector.instantiate(datasource_1.GraphiteDatasource, { instanceSettings: instanceSettings });
                });
                common_1.describe('When querying influxdb with one target using query editor target spec', function () {
                    var query = {
                        rangeRaw: { from: 'now-1h', to: 'now' },
                        targets: [{ target: 'prod1.count' }, { target: 'prod2.count' }],
                        maxDataPoints: 500,
                    };
                    var results;
                    var requestOptions;
                    common_1.beforeEach(function () {
                        ctx.backendSrv.datasourceRequest = function (options) {
                            requestOptions = options;
                            return ctx.$q.when({ data: [{ target: 'prod1.count', datapoints: [[10, 1], [12, 1]] }] });
                        };
                        ctx.ds.query(query).then(function (data) { results = data; });
                        ctx.$rootScope.$apply();
                    });
                    common_1.it('should generate the correct query', function () {
                        common_1.expect(requestOptions.url).to.be('/render');
                    });
                    common_1.it('should query correctly', function () {
                        var params = requestOptions.data.split('&');
                        common_1.expect(params).to.contain('target=prod1.count');
                        common_1.expect(params).to.contain('target=prod2.count');
                        common_1.expect(params).to.contain('from=-1h');
                        common_1.expect(params).to.contain('until=now');
                    });
                    common_1.it('should exclude undefined params', function () {
                        var params = requestOptions.data.split('&');
                        common_1.expect(params).to.not.contain('cacheTimeout=undefined');
                    });
                    common_1.it('should return series list', function () {
                        common_1.expect(results.data.length).to.be(1);
                        common_1.expect(results.data[0].target).to.be('prod1.count');
                    });
                    common_1.it('should convert to millisecond resolution', function () {
                        common_1.expect(results.data[0].datapoints[0][0]).to.be(10);
                    });
                });
                common_1.describe('building graphite params', function () {
                    common_1.it('should return empty array if no targets', function () {
                        var results = ctx.ds.buildGraphiteParams({
                            targets: [{}]
                        });
                        common_1.expect(results.length).to.be(0);
                    });
                    common_1.it('should uri escape targets', function () {
                        var results = ctx.ds.buildGraphiteParams({
                            targets: [{ target: 'prod1.{test,test2}' }, { target: 'prod2.count' }]
                        });
                        common_1.expect(results).to.contain('target=prod1.%7Btest%2Ctest2%7D');
                    });
                    common_1.it('should replace target placeholder', function () {
                        var results = ctx.ds.buildGraphiteParams({
                            targets: [{ target: 'series1' }, { target: 'series2' }, { target: 'asPercent(#A,#B)' }]
                        });
                        common_1.expect(results[2]).to.be('target=asPercent(series1%2Cseries2)');
                    });
                    common_1.it('should replace target placeholder for hidden series', function () {
                        var results = ctx.ds.buildGraphiteParams({
                            targets: [{ target: 'series1', hide: true }, { target: 'sumSeries(#A)', hide: true }, { target: 'asPercent(#A,#B)' }]
                        });
                        common_1.expect(results[0]).to.be('target=' + encodeURIComponent('asPercent(series1,sumSeries(series1))'));
                    });
                    common_1.it('should replace target placeholder when nesting query references', function () {
                        var results = ctx.ds.buildGraphiteParams({
                            targets: [{ target: 'series1' }, { target: 'sumSeries(#A)' }, { target: 'asPercent(#A,#B)' }]
                        });
                        common_1.expect(results[2]).to.be('target=' + encodeURIComponent("asPercent(series1,sumSeries(series1))"));
                    });
                    common_1.it('should fix wrong minute interval parameters', function () {
                        var results = ctx.ds.buildGraphiteParams({
                            targets: [{ target: "summarize(prod.25m.count, '25m', 'sum')" }]
                        });
                        common_1.expect(results[0]).to.be('target=' + encodeURIComponent("summarize(prod.25m.count, '25min', 'sum')"));
                    });
                    common_1.it('should fix wrong month interval parameters', function () {
                        var results = ctx.ds.buildGraphiteParams({
                            targets: [{ target: "summarize(prod.5M.count, '5M', 'sum')" }]
                        });
                        common_1.expect(results[0]).to.be('target=' + encodeURIComponent("summarize(prod.5M.count, '5mon', 'sum')"));
                    });
                    common_1.it('should ignore empty targets', function () {
                        var results = ctx.ds.buildGraphiteParams({
                            targets: [{ target: 'series1' }, { target: '' }]
                        });
                        common_1.expect(results.length).to.be(2);
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=datasource_specs.js.map