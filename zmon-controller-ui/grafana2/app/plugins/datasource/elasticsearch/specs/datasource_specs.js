///<amd-dependency path="../datasource" />
///<amd-dependency path="test/specs/helpers" name="helpers" />
define(["require", "exports", "test/specs/helpers", 'test/lib/common', 'moment', 'angular', "../datasource"], function (require, exports, helpers, common_1, moment, angular) {
    common_1.describe('ElasticDatasource', function () {
        var ctx = new helpers.ServiceTestContext();
        common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
        common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
        common_1.beforeEach(ctx.providePhase(['templateSrv', 'backendSrv']));
        common_1.beforeEach(ctx.createService('ElasticDatasource'));
        common_1.beforeEach(function () {
            ctx.ds = new ctx.service({ jsonData: {} });
        });
        common_1.describe('When testing datasource with index pattern', function () {
            common_1.beforeEach(function () {
                ctx.ds = new ctx.service({
                    url: 'http://es.com',
                    index: '[asd-]YYYY.MM.DD',
                    jsonData: { interval: 'Daily' }
                });
            });
            common_1.it('should translate index pattern to current day', function () {
                var requestOptions;
                ctx.backendSrv.datasourceRequest = function (options) {
                    requestOptions = options;
                    return ctx.$q.when({});
                };
                ctx.ds.testDatasource();
                ctx.$rootScope.$apply();
                var today = moment.utc().format("YYYY.MM.DD");
                common_1.expect(requestOptions.url).to.be("http://es.com/asd-" + today + '/_stats');
            });
        });
        common_1.describe('When issueing metric query with interval pattern', function () {
            var requestOptions, parts, header;
            common_1.beforeEach(function () {
                ctx.ds = new ctx.service({
                    url: 'http://es.com',
                    index: '[asd-]YYYY.MM.DD',
                    jsonData: { interval: 'Daily' }
                });
                ctx.backendSrv.datasourceRequest = function (options) {
                    requestOptions = options;
                    return ctx.$q.when({ data: { responses: [] } });
                };
                ctx.ds.query({
                    range: {
                        from: moment([2015, 4, 30, 10]),
                        to: moment([2015, 5, 1, 10])
                    },
                    targets: [{ bucketAggs: [], metrics: [], query: 'escape\\:test' }]
                });
                ctx.$rootScope.$apply();
                parts = requestOptions.data.split('\n');
                header = angular.fromJson(parts[0]);
            });
            common_1.it('should translate index pattern to current day', function () {
                common_1.expect(header.index).to.eql(['asd-2015.05.30', 'asd-2015.05.31', 'asd-2015.06.01']);
            });
            common_1.it('should json escape lucene query', function () {
                var body = angular.fromJson(parts[1]);
                common_1.expect(body.query.filtered.query.query_string.query).to.be('escape\\:test');
            });
        });
        common_1.describe('When issueing document query', function () {
            var requestOptions, parts, header;
            common_1.beforeEach(function () {
                ctx.ds = new ctx.service({ url: 'http://es.com', index: 'test', jsonData: {} });
                ctx.backendSrv.datasourceRequest = function (options) {
                    requestOptions = options;
                    return ctx.$q.when({ data: { responses: [] } });
                };
                ctx.ds.query({
                    range: { from: moment([2015, 4, 30, 10]), to: moment([2015, 5, 1, 10]) },
                    targets: [{ bucketAggs: [], metrics: [{ type: 'raw_document' }], query: 'test' }]
                });
                ctx.$rootScope.$apply();
                parts = requestOptions.data.split('\n');
                header = angular.fromJson(parts[0]);
            });
            common_1.it('should set search type to query_then_fetch', function () {
                common_1.expect(header.search_type).to.eql('query_then_fetch');
            });
            common_1.it('should set size', function () {
                var body = angular.fromJson(parts[1]);
                common_1.expect(body.size).to.be(500);
            });
        });
    });
});
//# sourceMappingURL=datasource_specs.js.map