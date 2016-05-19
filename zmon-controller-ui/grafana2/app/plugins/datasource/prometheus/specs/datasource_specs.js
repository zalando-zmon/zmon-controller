///<amd-dependency path="app/plugins/datasource/prometheus/datasource" />
///<amd-dependency path="test/specs/helpers" name="helpers" />
define(["require", "exports", "test/specs/helpers", 'test/lib/common', 'moment', "app/plugins/datasource/prometheus/datasource"], function (require, exports, helpers, common_1, moment) {
    common_1.describe('PrometheusDatasource', function () {
        var ctx = new helpers.ServiceTestContext();
        common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
        common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
        common_1.beforeEach(ctx.createService('PrometheusDatasource'));
        common_1.beforeEach(function () {
            ctx.ds = new ctx.service({ url: 'proxied', directUrl: 'direct', user: 'test', password: 'mupp' });
        });
        common_1.describe('When querying prometheus with one target using query editor target spec', function () {
            var results;
            var urlExpected = 'proxied/api/v1/query_range?query=' +
                encodeURIComponent('test{job="testjob"}') +
                '&start=1443438675&end=1443460275&step=60';
            var query = {
                range: { from: moment(1443438674760), to: moment(1443460274760) },
                targets: [{ expr: 'test{job="testjob"}' }],
                interval: '60s'
            };
            var response = {
                status: "success",
                data: {
                    resultType: "matrix",
                    result: [{
                            metric: { "__name__": "test", job: "testjob" },
                            values: [[1443454528, "3846"]]
                        }]
                }
            };
            common_1.beforeEach(function () {
                ctx.$httpBackend.expect('GET', urlExpected).respond(response);
                ctx.ds.query(query).then(function (data) { results = data; });
                ctx.$httpBackend.flush();
            });
            common_1.it('should generate the correct query', function () {
                ctx.$httpBackend.verifyNoOutstandingExpectation();
            });
            common_1.it('should return series list', function () {
                common_1.expect(results.data.length).to.be(1);
                common_1.expect(results.data[0].target).to.be('test{job="testjob"}');
            });
        });
        common_1.describe('When performing metricFindQuery', function () {
            var results;
            var response;
            common_1.it('label_values(resource) should generate label search query', function () {
                response = {
                    status: "success",
                    data: ["value1", "value2", "value3"]
                };
                ctx.$httpBackend.expect('GET', 'proxied/api/v1/label/resource/values').respond(response);
                ctx.ds.metricFindQuery('label_values(resource)').then(function (data) { results = data; });
                ctx.$httpBackend.flush();
                ctx.$rootScope.$apply();
                common_1.expect(results.length).to.be(3);
            });
            common_1.it('label_values(metric, resource) should generate series query', function () {
                response = {
                    status: "success",
                    data: [
                        { __name__: "metric", resource: "value1" },
                        { __name__: "metric", resource: "value2" },
                        { __name__: "metric", resource: "value3" }
                    ]
                };
                ctx.$httpBackend.expect('GET', 'proxied/api/v1/series?match[]=metric').respond(response);
                ctx.ds.metricFindQuery('label_values(metric, resource)').then(function (data) { results = data; });
                ctx.$httpBackend.flush();
                ctx.$rootScope.$apply();
                common_1.expect(results.length).to.be(3);
            });
            common_1.it('metrics(metric.*) should generate metric name query', function () {
                response = {
                    status: "success",
                    data: ["metric1", "metric2", "metric3", "nomatch"]
                };
                ctx.$httpBackend.expect('GET', 'proxied/api/v1/label/__name__/values').respond(response);
                ctx.ds.metricFindQuery('metrics(metric.*)').then(function (data) { results = data; });
                ctx.$httpBackend.flush();
                ctx.$rootScope.$apply();
                common_1.expect(results.length).to.be(3);
            });
        });
    });
});
//# sourceMappingURL=datasource_specs.js.map