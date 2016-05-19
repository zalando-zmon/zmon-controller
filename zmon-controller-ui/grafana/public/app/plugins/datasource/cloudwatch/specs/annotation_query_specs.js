System.register(["../datasource", 'test/lib/common', 'moment', 'test/specs/helpers', '../annotation_query'], function(exports_1) {
    var common_1, moment_1, helpers_1, datasource_1, annotation_query_1;
    return {
        setters:[
            function (datasource_1_1) {
                datasource_1 = datasource_1_1;
            },
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (helpers_1_1) {
                helpers_1 = helpers_1_1;
            },
            function (annotation_query_1_1) {
                annotation_query_1 = annotation_query_1_1;
            }],
        execute: function() {
            common_1.describe('CloudWatchAnnotationQuery', function () {
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
                }));
                common_1.describe('When performing annotationQuery', function () {
                    var parameter = {
                        annotation: {
                            region: 'us-east-1',
                            namespace: 'AWS/EC2',
                            metricName: 'CPUUtilization',
                            dimensions: {
                                InstanceId: 'i-12345678'
                            },
                            statistics: ['Average'],
                            period: 300
                        },
                        range: {
                            from: moment_1.default(1443438674760),
                            to: moment_1.default(1443460274760)
                        }
                    };
                    var alarmResponse = {
                        MetricAlarms: [
                            {
                                AlarmName: 'test_alarm_name'
                            }
                        ]
                    };
                    var historyResponse = {
                        AlarmHistoryItems: [
                            {
                                Timestamp: '2015-01-01T00:00:00.000Z',
                                HistoryItemType: 'StateUpdate',
                                AlarmName: 'test_alarm_name',
                                HistoryData: '{}',
                                HistorySummary: 'test_history_summary'
                            }
                        ]
                    };
                    common_1.beforeEach(function () {
                        ctx.backendSrv.datasourceRequest = function (params) {
                            switch (params.data.action) {
                                case 'DescribeAlarmsForMetric':
                                    return ctx.$q.when({ data: alarmResponse });
                                case 'DescribeAlarmHistory':
                                    return ctx.$q.when({ data: historyResponse });
                            }
                        };
                    });
                    common_1.it('should return annotation list', function (done) {
                        var annotationQuery = new annotation_query_1.default(ctx.ds, parameter.annotation, ctx.$q, ctx.templateSrv);
                        annotationQuery.process(parameter.range.from, parameter.range.to).then(function (result) {
                            common_1.expect(result[0].title).to.be('test_alarm_name');
                            common_1.expect(result[0].text).to.be('test_history_summary');
                            done();
                        });
                        ctx.$rootScope.$apply();
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=annotation_query_specs.js.map