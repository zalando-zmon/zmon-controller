System.register(['test/lib/common', '../elastic_response'], function(exports_1) {
    var common_1, elastic_response_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (elastic_response_1_1) {
                elastic_response_1 = elastic_response_1_1;
            }],
        execute: function() {
            common_1.describe('ElasticResponse', function () {
                var targets;
                var response;
                var result;
                common_1.describe('simple query and count', function () {
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'count', id: '1' }],
                                bucketAggs: [{ type: 'date_histogram', field: '@timestamp', id: '2' }],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "2": {
                                            buckets: [
                                                {
                                                    doc_count: 10,
                                                    key: 1000
                                                },
                                                {
                                                    doc_count: 15,
                                                    key: 2000
                                                }
                                            ]
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return 1 series', function () {
                        common_1.expect(result.data.length).to.be(1);
                        common_1.expect(result.data[0].target).to.be('Count');
                        common_1.expect(result.data[0].datapoints.length).to.be(2);
                        common_1.expect(result.data[0].datapoints[0][0]).to.be(10);
                        common_1.expect(result.data[0].datapoints[0][1]).to.be(1000);
                    });
                });
                common_1.describe('simple query count & avg aggregation', function () {
                    var result;
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'count', id: '1' }, { type: 'avg', field: 'value', id: '2' }],
                                bucketAggs: [{ type: 'date_histogram', field: '@timestamp', id: '3' }],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "3": {
                                            buckets: [
                                                {
                                                    "2": { value: 88 },
                                                    doc_count: 10,
                                                    key: 1000
                                                },
                                                {
                                                    "2": { value: 99 },
                                                    doc_count: 15,
                                                    key: 2000
                                                }
                                            ]
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return 2 series', function () {
                        common_1.expect(result.data.length).to.be(2);
                        common_1.expect(result.data[0].datapoints.length).to.be(2);
                        common_1.expect(result.data[0].datapoints[0][0]).to.be(10);
                        common_1.expect(result.data[0].datapoints[0][1]).to.be(1000);
                        common_1.expect(result.data[1].target).to.be("Average value");
                        common_1.expect(result.data[1].datapoints[0][0]).to.be(88);
                        common_1.expect(result.data[1].datapoints[1][0]).to.be(99);
                    });
                });
                common_1.describe('single group by query one metric', function () {
                    var result;
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'count', id: '1' }],
                                bucketAggs: [{ type: 'terms', field: 'host', id: '2' }, { type: 'date_histogram', field: '@timestamp', id: '3' }],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "2": {
                                            buckets: [
                                                {
                                                    "3": {
                                                        buckets: [
                                                            { doc_count: 1, key: 1000 },
                                                            { doc_count: 3, key: 2000 }
                                                        ]
                                                    },
                                                    doc_count: 4,
                                                    key: 'server1',
                                                },
                                                {
                                                    "3": {
                                                        buckets: [
                                                            { doc_count: 2, key: 1000 },
                                                            { doc_count: 8, key: 2000 }
                                                        ]
                                                    },
                                                    doc_count: 10,
                                                    key: 'server2',
                                                },
                                            ]
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return 2 series', function () {
                        common_1.expect(result.data.length).to.be(2);
                        common_1.expect(result.data[0].datapoints.length).to.be(2);
                        common_1.expect(result.data[0].target).to.be('server1');
                        common_1.expect(result.data[1].target).to.be('server2');
                    });
                });
                common_1.describe('single group by query two metrics', function () {
                    var result;
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'count', id: '1' }, { type: 'avg', field: '@value', id: '4' }],
                                bucketAggs: [{ type: 'terms', field: 'host', id: '2' }, { type: 'date_histogram', field: '@timestamp', id: '3' }],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "2": {
                                            buckets: [
                                                {
                                                    "3": {
                                                        buckets: [
                                                            { "4": { value: 10 }, doc_count: 1, key: 1000 },
                                                            { "4": { value: 12 }, doc_count: 3, key: 2000 }
                                                        ]
                                                    },
                                                    doc_count: 4,
                                                    key: 'server1',
                                                },
                                                {
                                                    "3": {
                                                        buckets: [
                                                            { "4": { value: 20 }, doc_count: 1, key: 1000 },
                                                            { "4": { value: 32 }, doc_count: 3, key: 2000 }
                                                        ]
                                                    },
                                                    doc_count: 10,
                                                    key: 'server2',
                                                },
                                            ]
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return 2 series', function () {
                        common_1.expect(result.data.length).to.be(4);
                        common_1.expect(result.data[0].datapoints.length).to.be(2);
                        common_1.expect(result.data[0].target).to.be('server1 Count');
                        common_1.expect(result.data[1].target).to.be('server1 Average @value');
                        common_1.expect(result.data[2].target).to.be('server2 Count');
                        common_1.expect(result.data[3].target).to.be('server2 Average @value');
                    });
                });
                common_1.describe('with percentiles ', function () {
                    var result;
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'percentiles', settings: { percents: [75, 90] }, id: '1' }],
                                bucketAggs: [{ type: 'date_histogram', field: '@timestamp', id: '3' }],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "3": {
                                            buckets: [
                                                {
                                                    "1": { values: { "75": 3.3, "90": 5.5 } },
                                                    doc_count: 10,
                                                    key: 1000
                                                },
                                                {
                                                    "1": { values: { "75": 2.3, "90": 4.5 } },
                                                    doc_count: 15,
                                                    key: 2000
                                                }
                                            ]
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return 2 series', function () {
                        common_1.expect(result.data.length).to.be(2);
                        common_1.expect(result.data[0].datapoints.length).to.be(2);
                        common_1.expect(result.data[0].target).to.be('p75');
                        common_1.expect(result.data[1].target).to.be('p90');
                        common_1.expect(result.data[0].datapoints[0][0]).to.be(3.3);
                        common_1.expect(result.data[0].datapoints[0][1]).to.be(1000);
                        common_1.expect(result.data[1].datapoints[1][0]).to.be(4.5);
                    });
                });
                common_1.describe('with extended_stats', function () {
                    var result;
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'extended_stats', meta: { max: true, std_deviation_bounds_upper: true }, id: '1' }],
                                bucketAggs: [{ type: 'terms', field: 'host', id: '3' }, { type: 'date_histogram', id: '4' }],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "3": {
                                            buckets: [
                                                {
                                                    key: 'server1',
                                                    "4": {
                                                        buckets: [{
                                                                "1": { max: 10.2, min: 5.5, std_deviation_bounds: { upper: 3, lower: -2 } },
                                                                doc_count: 10,
                                                                key: 1000
                                                            }]
                                                    }
                                                },
                                                {
                                                    key: 'server2',
                                                    "4": {
                                                        buckets: [{
                                                                "1": { max: 10.2, min: 5.5, std_deviation_bounds: { upper: 3, lower: -2 } },
                                                                doc_count: 10,
                                                                key: 1000
                                                            }]
                                                    }
                                                },
                                            ]
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return 4 series', function () {
                        common_1.expect(result.data.length).to.be(4);
                        common_1.expect(result.data[0].datapoints.length).to.be(1);
                        common_1.expect(result.data[0].target).to.be('server1 Max');
                        common_1.expect(result.data[1].target).to.be('server1 Std Dev Upper');
                        common_1.expect(result.data[0].datapoints[0][0]).to.be(10.2);
                        common_1.expect(result.data[1].datapoints[0][0]).to.be(3);
                    });
                });
                common_1.describe('single group by with alias pattern', function () {
                    var result;
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'count', id: '1' }],
                                alias: '{{term @host}} {{metric}} and!',
                                bucketAggs: [
                                    { type: 'terms', field: '@host', id: '2' },
                                    { type: 'date_histogram', field: '@timestamp', id: '3' }
                                ],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "2": {
                                            buckets: [
                                                {
                                                    "3": {
                                                        buckets: [
                                                            { doc_count: 1, key: 1000 },
                                                            { doc_count: 3, key: 2000 }
                                                        ]
                                                    },
                                                    doc_count: 4,
                                                    key: 'server1',
                                                },
                                                {
                                                    "3": {
                                                        buckets: [
                                                            { doc_count: 2, key: 1000 },
                                                            { doc_count: 8, key: 2000 }
                                                        ]
                                                    },
                                                    doc_count: 10,
                                                    key: 'server2',
                                                },
                                            ]
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return 2 series', function () {
                        common_1.expect(result.data.length).to.be(2);
                        common_1.expect(result.data[0].datapoints.length).to.be(2);
                        common_1.expect(result.data[0].target).to.be('server1 Count and!');
                        common_1.expect(result.data[1].target).to.be('server2 Count and!');
                    });
                });
                common_1.describe('with two filters agg', function () {
                    var result;
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'count', id: '1' }],
                                bucketAggs: [
                                    {
                                        id: '2',
                                        type: 'filters',
                                        settings: {
                                            filters: [
                                                { query: '@metric:cpu' },
                                                { query: '@metric:logins.count' },
                                            ]
                                        }
                                    },
                                    { type: 'date_histogram', field: '@timestamp', id: '3' }
                                ],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "2": {
                                            buckets: {
                                                "@metric:cpu": {
                                                    "3": {
                                                        buckets: [
                                                            { doc_count: 1, key: 1000 },
                                                            { doc_count: 3, key: 2000 }
                                                        ]
                                                    },
                                                },
                                                "@metric:logins.count": {
                                                    "3": {
                                                        buckets: [
                                                            { doc_count: 2, key: 1000 },
                                                            { doc_count: 8, key: 2000 }
                                                        ]
                                                    },
                                                },
                                            }
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return 2 series', function () {
                        common_1.expect(result.data.length).to.be(2);
                        common_1.expect(result.data[0].datapoints.length).to.be(2);
                        common_1.expect(result.data[0].target).to.be('@metric:cpu');
                        common_1.expect(result.data[1].target).to.be('@metric:logins.count');
                    });
                });
                common_1.describe('with dropfirst and last aggregation', function () {
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'avg', id: '1' }, { type: 'count' }],
                                bucketAggs: [{ id: '2', type: 'date_histogram', field: 'host', settings: { trimEdges: 1 } }],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "2": {
                                            buckets: [
                                                {
                                                    "1": { value: 1000 },
                                                    key: 1,
                                                    doc_count: 369,
                                                },
                                                {
                                                    "1": { value: 2000 },
                                                    key: 2,
                                                    doc_count: 200,
                                                },
                                                {
                                                    "1": { value: 2000 },
                                                    key: 3,
                                                    doc_count: 200,
                                                },
                                            ]
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should remove first and last value', function () {
                        common_1.expect(result.data.length).to.be(2);
                        common_1.expect(result.data[0].datapoints.length).to.be(1);
                    });
                });
                common_1.describe('No group by time', function () {
                    common_1.beforeEach(function () {
                        targets = [{
                                refId: 'A',
                                metrics: [{ type: 'avg', id: '1' }, { type: 'count' }],
                                bucketAggs: [{ id: '2', type: 'terms', field: 'host' }],
                            }];
                        response = {
                            responses: [{
                                    aggregations: {
                                        "2": {
                                            buckets: [
                                                {
                                                    "1": { value: 1000 },
                                                    key: "server-1",
                                                    doc_count: 369,
                                                },
                                                {
                                                    "1": { value: 2000 },
                                                    key: "server-2",
                                                    doc_count: 200,
                                                },
                                            ]
                                        }
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return table', function () {
                        common_1.expect(result.data.length).to.be(1);
                        common_1.expect(result.data[0].type).to.be('docs');
                        common_1.expect(result.data[0].datapoints.length).to.be(2);
                        common_1.expect(result.data[0].datapoints[0].host).to.be("server-1");
                        common_1.expect(result.data[0].datapoints[0].Average).to.be(1000);
                        common_1.expect(result.data[0].datapoints[0].Count).to.be(369);
                        common_1.expect(result.data[0].datapoints[1].host).to.be("server-2");
                        common_1.expect(result.data[0].datapoints[1].Average).to.be(2000);
                    });
                });
                common_1.describe('Raw documents query', function () {
                    common_1.beforeEach(function () {
                        targets = [{ refId: 'A', metrics: [{ type: 'raw_document', id: '1' }], bucketAggs: [] }];
                        response = {
                            responses: [{
                                    hits: {
                                        total: 100,
                                        hits: [
                                            {
                                                _id: '1',
                                                _type: 'type',
                                                _index: 'index',
                                                _source: { sourceProp: "asd" },
                                                fields: { fieldProp: "field" },
                                            },
                                            {
                                                _source: { sourceProp: "asd2" },
                                                fields: { fieldProp: "field2" },
                                            }
                                        ]
                                    }
                                }]
                        };
                        result = new elastic_response_1.default(targets, response).getTimeSeries();
                    });
                    common_1.it('should return docs', function () {
                        common_1.expect(result.data.length).to.be(1);
                        common_1.expect(result.data[0].type).to.be('docs');
                        common_1.expect(result.data[0].total).to.be(100);
                        common_1.expect(result.data[0].datapoints.length).to.be(2);
                        common_1.expect(result.data[0].datapoints[0].sourceProp).to.be("asd");
                        common_1.expect(result.data[0].datapoints[0].fieldProp).to.be("field");
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=elastic_response_specs.js.map