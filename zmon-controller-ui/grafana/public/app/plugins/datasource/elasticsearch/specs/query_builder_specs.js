System.register(['test/lib/common', '../query_builder'], function(exports_1) {
    var common_1, query_builder_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (query_builder_1_1) {
                query_builder_1 = query_builder_1_1;
            }],
        execute: function() {
            common_1.describe('ElasticQueryBuilder', function () {
                var builder;
                common_1.beforeEach(function () {
                    builder = new query_builder_1.default({ timeField: '@timestamp' });
                });
                common_1.it('with defaults', function () {
                    var query = builder.build({
                        metrics: [{ type: 'Count', id: '0' }],
                        timeField: '@timestamp',
                        bucketAggs: [{ type: 'date_histogram', field: '@timestamp', id: '1' }],
                    });
                    common_1.expect(query.query.filtered.filter.bool.must[0].range["@timestamp"].gte).to.be("$timeFrom");
                    common_1.expect(query.aggs["1"].date_histogram.extended_bounds.min).to.be("$timeFrom");
                });
                common_1.it('with multiple bucket aggs', function () {
                    var query = builder.build({
                        metrics: [{ type: 'count', id: '1' }],
                        timeField: '@timestamp',
                        bucketAggs: [
                            { type: 'terms', field: '@host', id: '2' },
                            { type: 'date_histogram', field: '@timestamp', id: '3' }
                        ],
                    });
                    common_1.expect(query.aggs["2"].terms.field).to.be("@host");
                    common_1.expect(query.aggs["2"].aggs["3"].date_histogram.field).to.be("@timestamp");
                });
                common_1.it('with es1.x and es2.x date histogram queries check time format', function () {
                    var builder_2x = new query_builder_1.default({
                        timeField: '@timestamp',
                        esVersion: 2
                    });
                    var query_params = {
                        metrics: [],
                        bucketAggs: [
                            { type: 'date_histogram', field: '@timestamp', id: '1' }
                        ],
                    };
                    // format should not be specified in 1.x queries
                    common_1.expect("format" in builder.build(query_params)["aggs"]["1"]["date_histogram"]).to.be(false);
                    // 2.x query should specify format to be "epoch_millis"
                    common_1.expect(builder_2x.build(query_params)["aggs"]["1"]["date_histogram"]["format"]).to.be("epoch_millis");
                });
                common_1.it('with es1.x and es2.x range filter check time format', function () {
                    var builder_2x = new query_builder_1.default({
                        timeField: '@timestamp',
                        esVersion: 2
                    });
                    // format should not be specified in 1.x queries
                    common_1.expect("format" in builder.getRangeFilter()["@timestamp"]).to.be(false);
                    // 2.x query should specify format to be "epoch_millis"
                    common_1.expect(builder_2x.getRangeFilter()["@timestamp"]["format"]).to.be("epoch_millis");
                });
                common_1.it('with select field', function () {
                    var query = builder.build({
                        metrics: [{ type: 'avg', field: '@value', id: '1' }],
                        bucketAggs: [{ type: 'date_histogram', field: '@timestamp', id: '2' }],
                    }, 100, 1000);
                    var aggs = query.aggs["2"].aggs;
                    common_1.expect(aggs["1"].avg.field).to.be("@value");
                });
                common_1.it('with term agg and order by metric agg', function () {
                    var query = builder.build({
                        metrics: [
                            { type: 'count', id: '1' },
                            { type: 'avg', field: '@value', id: '5' }
                        ],
                        bucketAggs: [
                            { type: 'terms', field: '@host', settings: { size: 5, order: 'asc', orderBy: '5' }, id: '2' },
                            { type: 'date_histogram', field: '@timestamp', id: '3' }
                        ],
                    }, 100, 1000);
                    var firstLevel = query.aggs["2"];
                    var secondLevel = firstLevel.aggs["3"];
                    common_1.expect(firstLevel.aggs["5"].avg.field).to.be("@value");
                    common_1.expect(secondLevel.aggs["5"].avg.field).to.be("@value");
                });
                common_1.it('with metric percentiles', function () {
                    var query = builder.build({
                        metrics: [
                            {
                                id: '1',
                                type: 'percentiles',
                                field: '@load_time',
                                settings: {
                                    percents: [1, 2, 3, 4]
                                }
                            }
                        ],
                        bucketAggs: [
                            { type: 'date_histogram', field: '@timestamp', id: '3' }
                        ],
                    }, 100, 1000);
                    var firstLevel = query.aggs["3"];
                    common_1.expect(firstLevel.aggs["1"].percentiles.field).to.be("@load_time");
                    common_1.expect(firstLevel.aggs["1"].percentiles.percents).to.eql([1, 2, 3, 4]);
                });
                common_1.it('with filters aggs', function () {
                    var query = builder.build({
                        metrics: [{ type: 'count', id: '1' }],
                        timeField: '@timestamp',
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
                            { type: 'date_histogram', field: '@timestamp', id: '4' }
                        ],
                    });
                    common_1.expect(query.aggs["2"].filters.filters["@metric:cpu"].query.query_string.query).to.be("@metric:cpu");
                    common_1.expect(query.aggs["2"].filters.filters["@metric:logins.count"].query.query_string.query).to.be("@metric:logins.count");
                    common_1.expect(query.aggs["2"].aggs["4"].date_histogram.field).to.be("@timestamp");
                });
                common_1.it('with raw_document metric', function () {
                    var query = builder.build({
                        metrics: [{ type: 'raw_document', id: '1' }],
                        timeField: '@timestamp',
                        bucketAggs: [],
                    });
                    common_1.expect(query.size).to.be(500);
                });
                common_1.it('with moving average', function () {
                    var query = builder.build({
                        metrics: [
                            {
                                id: '3',
                                type: 'sum',
                                field: '@value'
                            },
                            {
                                id: '2',
                                type: 'moving_avg',
                                field: '3',
                                pipelineAgg: '3'
                            }
                        ],
                        bucketAggs: [
                            { type: 'date_histogram', field: '@timestamp', id: '3' }
                        ],
                    });
                    var firstLevel = query.aggs["3"];
                    common_1.expect(firstLevel.aggs["2"]).not.to.be(undefined);
                    common_1.expect(firstLevel.aggs["2"].moving_avg).not.to.be(undefined);
                    common_1.expect(firstLevel.aggs["2"].moving_avg.buckets_path).to.be("3");
                });
                common_1.it('with broken moving average', function () {
                    var query = builder.build({
                        metrics: [
                            {
                                id: '3',
                                type: 'sum',
                                field: '@value'
                            },
                            {
                                id: '2',
                                type: 'moving_avg',
                                pipelineAgg: '3'
                            },
                            {
                                id: '4',
                                type: 'moving_avg',
                                pipelineAgg: 'Metric to apply moving average'
                            }
                        ],
                        bucketAggs: [
                            { type: 'date_histogram', field: '@timestamp', id: '3' }
                        ],
                    });
                    var firstLevel = query.aggs["3"];
                    common_1.expect(firstLevel.aggs["2"]).not.to.be(undefined);
                    common_1.expect(firstLevel.aggs["2"].moving_avg).not.to.be(undefined);
                    common_1.expect(firstLevel.aggs["2"].moving_avg.buckets_path).to.be("3");
                    common_1.expect(firstLevel.aggs["4"]).to.be(undefined);
                });
                common_1.it('with derivative', function () {
                    var query = builder.build({
                        metrics: [
                            {
                                id: '3',
                                type: 'sum',
                                field: '@value'
                            },
                            {
                                id: '2',
                                type: 'derivative',
                                pipelineAgg: '3'
                            }
                        ],
                        bucketAggs: [
                            { type: 'date_histogram', field: '@timestamp', id: '3' }
                        ],
                    });
                    var firstLevel = query.aggs["3"];
                    common_1.expect(firstLevel.aggs["2"]).not.to.be(undefined);
                    common_1.expect(firstLevel.aggs["2"].derivative).not.to.be(undefined);
                    common_1.expect(firstLevel.aggs["2"].derivative.buckets_path).to.be("3");
                });
            });
        }
    }
});
//# sourceMappingURL=query_builder_specs.js.map