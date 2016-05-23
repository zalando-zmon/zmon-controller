System.register(["../datasource", 'test/lib/common', 'test/specs/helpers'], function(exports_1) {
    var common_1, helpers_1, datasource_1;
    return {
        setters:[
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            },
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (helpers_1_1) {
                helpers_1 = helpers_1_1;
            }],
        execute: function() {
            common_1.describe('CloudWatchDatasource', function () {
                var ctx = new helpers_1.default.ServiceTestContext();
                var instanceSettings = {
                    jsonData: { defaultRegion: 'us-east-1', access: 'proxy' },
                };
                common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
                common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
                common_1.beforeEach(common_1.angularMocks.module('grafana.controllers'));
                common_1.beforeEach(ctx.providePhase(['templateSrv', 'backendSrv']));
                common_1.beforeEach(common_1.angularMocks.inject(function ($q, $rootScope, $httpBackend, $injector) {
                    ctx.$q = $q;
                    ctx.$httpBackend = $httpBackend;
                    ctx.$rootScope = $rootScope;
                    ctx.ds = $injector.instantiate(datasource_1.CloudWatchDatasource, { instanceSettings: instanceSettings });
                    $httpBackend.when('GET', /\.html$/).respond('');
                }));
                common_1.describe('When performing CloudWatch query', function () {
                    var requestParams;
                    var query = {
                        range: { from: 'now-1h', to: 'now' },
                        targets: [
                            {
                                region: 'us-east-1',
                                namespace: 'AWS/EC2',
                                metricName: 'CPUUtilization',
                                dimensions: {
                                    InstanceId: 'i-12345678'
                                },
                                statistics: ['Average'],
                                period: 300
                            }
                        ]
                    };
                    var response = {
                        Datapoints: [
                            {
                                Average: 1,
                                Timestamp: 'Wed Dec 31 1969 16:00:00 GMT-0800 (PST)'
                            },
                            {
                                Average: 2,
                                Timestamp: 'Wed Dec 31 1969 16:05:00 GMT-0800 (PST)'
                            },
                            {
                                Average: 5,
                                Timestamp: 'Wed Dec 31 1969 16:15:00 GMT-0800 (PST)'
                            }
                        ],
                        Label: 'CPUUtilization'
                    };
                    common_1.beforeEach(function () {
                        ctx.backendSrv.datasourceRequest = function (params) {
                            requestParams = params;
                            return ctx.$q.when({ data: response });
                        };
                    });
                    common_1.it('should generate the correct query', function (done) {
                        ctx.ds.query(query).then(function () {
                            var params = requestParams.data.parameters;
                            common_1.expect(params.namespace).to.be(query.targets[0].namespace);
                            common_1.expect(params.metricName).to.be(query.targets[0].metricName);
                            common_1.expect(params.dimensions[0].Name).to.be(Object.keys(query.targets[0].dimensions)[0]);
                            common_1.expect(params.dimensions[0].Value).to.be(query.targets[0].dimensions[Object.keys(query.targets[0].dimensions)[0]]);
                            common_1.expect(params.statistics).to.eql(query.targets[0].statistics);
                            common_1.expect(params.period).to.be(query.targets[0].period);
                            done();
                        });
                        ctx.$rootScope.$apply();
                    });
                    common_1.it('should return series list', function (done) {
                        ctx.ds.query(query).then(function (result) {
                            common_1.expect(result.data[0].target).to.be('CPUUtilization_Average');
                            common_1.expect(result.data[0].datapoints[0][0]).to.be(response.Datapoints[0]['Average']);
                            done();
                        });
                        ctx.$rootScope.$apply();
                    });
                    common_1.it('should return null for missing data point', function (done) {
                        ctx.ds.query(query).then(function (result) {
                            common_1.expect(result.data[0].datapoints[2][0]).to.be(null);
                            done();
                        });
                        ctx.$rootScope.$apply();
                    });
                });
                function describeMetricFindQuery(query, func) {
                    common_1.describe('metricFindQuery ' + query, function () {
                        var scenario = {};
                        scenario.setup = function (setupCallback) {
                            common_1.beforeEach(function () {
                                setupCallback();
                                ctx.backendSrv.datasourceRequest = function (args) {
                                    scenario.request = args;
                                    return ctx.$q.when({ data: scenario.requestResponse });
                                };
                                ctx.ds.metricFindQuery(query).then(function (args) {
                                    scenario.result = args;
                                });
                                ctx.$rootScope.$apply();
                            });
                        };
                        func(scenario);
                    });
                }
                describeMetricFindQuery('regions()', function (scenario) {
                    scenario.setup(function () {
                        scenario.requestResponse = [{ text: 'us-east-1' }];
                    });
                    common_1.it('should call __GetRegions and return result', function () {
                        common_1.expect(scenario.result[0].text).to.contain('us-east-1');
                        common_1.expect(scenario.request.data.action).to.be('__GetRegions');
                    });
                });
                describeMetricFindQuery('namespaces()', function (scenario) {
                    scenario.setup(function () {
                        scenario.requestResponse = [{ text: 'AWS/EC2' }];
                    });
                    common_1.it('should call __GetNamespaces and return result', function () {
                        common_1.expect(scenario.result[0].text).to.contain('AWS/EC2');
                        common_1.expect(scenario.request.data.action).to.be('__GetNamespaces');
                    });
                });
                describeMetricFindQuery('metrics(AWS/EC2)', function (scenario) {
                    scenario.setup(function () {
                        scenario.requestResponse = [{ text: 'CPUUtilization' }];
                    });
                    common_1.it('should call __GetMetrics and return result', function () {
                        common_1.expect(scenario.result[0].text).to.be('CPUUtilization');
                        common_1.expect(scenario.request.data.action).to.be('__GetMetrics');
                    });
                });
                describeMetricFindQuery('dimension_keys(AWS/EC2)', function (scenario) {
                    scenario.setup(function () {
                        scenario.requestResponse = [{ text: 'InstanceId' }];
                    });
                    common_1.it('should call __GetDimensions and return result', function () {
                        common_1.expect(scenario.result[0].text).to.be('InstanceId');
                        common_1.expect(scenario.request.data.action).to.be('__GetDimensions');
                    });
                });
                describeMetricFindQuery('dimension_values(us-east-1,AWS/EC2,CPUUtilization,InstanceId)', function (scenario) {
                    scenario.setup(function () {
                        scenario.requestResponse = {
                            Metrics: [
                                {
                                    Namespace: 'AWS/EC2',
                                    MetricName: 'CPUUtilization',
                                    Dimensions: [
                                        {
                                            Name: 'InstanceId',
                                            Value: 'i-12345678'
                                        }
                                    ]
                                }
                            ]
                        };
                    });
                    common_1.it('should call __ListMetrics and return result', function () {
                        common_1.expect(scenario.result[0].text).to.be('i-12345678');
                        common_1.expect(scenario.request.data.action).to.be('ListMetrics');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=datasource_specs.js.map