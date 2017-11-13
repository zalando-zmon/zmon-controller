System.register(['test/lib/common', 'moment', 'test/specs/helpers', '../datasource', '../metric_find_query'], function(exports_1) {
    var common_1, moment_1, helpers_1, datasource_1, metric_find_query_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (helpers_1_1) {
                helpers_1 = helpers_1_1;
            },
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            },
            function (metric_find_query_1_1) {
                metric_find_query_1 = metric_find_query_1_1;
            }],
        execute: function() {
            common_1.describe('PrometheusMetricFindQuery', function () {
                var ctx = new helpers_1.default.ServiceTestContext();
                var instanceSettings = { url: 'proxied', directUrl: 'direct', user: 'test', password: 'mupp' };
                common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
                common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
                common_1.beforeEach(common_1.angularMocks.inject(function ($q, $rootScope, $httpBackend, $injector) {
                    ctx.$q = $q;
                    ctx.$httpBackend = $httpBackend;
                    ctx.$rootScope = $rootScope;
                    ctx.ds = $injector.instantiate(datasource_1.PrometheusDatasource, { instanceSettings: instanceSettings });
                    $httpBackend.when('GET', /\.html$/).respond('');
                }));
                common_1.describe('When performing metricFindQuery', function () {
                    var results;
                    var response;
                    common_1.it('label_values(resource) should generate label search query', function () {
                        response = {
                            status: "success",
                            data: ["value1", "value2", "value3"]
                        };
                        ctx.$httpBackend.expect('GET', 'proxied/api/v1/label/resource/values').respond(response);
                        var pm = new metric_find_query_1.default(ctx.ds, 'label_values(resource)', ctx.timeSrv);
                        pm.process().then(function (data) { results = data; });
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
                        ctx.$httpBackend.expect('GET', /proxied\/api\/v1\/series\?match\[\]=metric&start=.*&end=.*/).respond(response);
                        var pm = new metric_find_query_1.default(ctx.ds, 'label_values(metric, resource)', ctx.timeSrv);
                        pm.process().then(function (data) { results = data; });
                        ctx.$httpBackend.flush();
                        ctx.$rootScope.$apply();
                        common_1.expect(results.length).to.be(3);
                    });
                    common_1.it('label_values(metric, resource) should pass correct time', function () {
                        ctx.timeSrv.setTime({ from: moment_1.default.utc('2011-01-01'), to: moment_1.default.utc('2015-01-01') });
                        ctx.$httpBackend.expect('GET', /proxied\/api\/v1\/series\?match\[\]=metric&start=1293840000&end=1420070400/).respond(response);
                        var pm = new metric_find_query_1.default(ctx.ds, 'label_values(metric, resource)', ctx.timeSrv);
                        pm.process().then(function (data) { results = data; });
                        ctx.$httpBackend.flush();
                        ctx.$rootScope.$apply();
                    });
                    common_1.it('label_values(metric{label1="foo", label2="bar", label3="baz"}, resource) should generate series query', function () {
                        response = {
                            status: "success",
                            data: [
                                { __name__: "metric", resource: "value1" },
                                { __name__: "metric", resource: "value2" },
                                { __name__: "metric", resource: "value3" }
                            ]
                        };
                        ctx.$httpBackend.expect('GET', /proxied\/api\/v1\/series\?match\[\]=metric&start=.*&end=.*/).respond(response);
                        var pm = new metric_find_query_1.default(ctx.ds, 'label_values(metric, resource)', ctx.timeSrv);
                        pm.process().then(function (data) { results = data; });
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
                        var pm = new metric_find_query_1.default(ctx.ds, 'metrics(metric.*)', ctx.timeSrv);
                        pm.process().then(function (data) { results = data; });
                        ctx.$httpBackend.flush();
                        ctx.$rootScope.$apply();
                        common_1.expect(results.length).to.be(3);
                    });
                    common_1.it('query_result(metric) should generate metric name query', function () {
                        response = {
                            status: "success",
                            data: {
                                resultType: "vector",
                                result: [{
                                        metric: { "__name__": "metric", job: "testjob" },
                                        value: [1443454528.000, "3846"]
                                    }]
                            }
                        };
                        ctx.$httpBackend.expect('GET', /proxied\/api\/v1\/query\?query=metric&time=.*/).respond(response);
                        var pm = new metric_find_query_1.default(ctx.ds, 'query_result(metric)', ctx.timeSrv);
                        pm.process().then(function (data) { results = data; });
                        ctx.$httpBackend.flush();
                        ctx.$rootScope.$apply();
                        common_1.expect(results.length).to.be(1);
                        common_1.expect(results[0].text).to.be('metric{job="testjob"} 3846 1443454528000');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=metric_find_query_specs.js.map