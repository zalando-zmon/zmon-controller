System.register(['test/lib/common', '../query_def'], function(exports_1) {
    var common_1, query_def_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (query_def_1_1) {
                query_def_1 = query_def_1_1;
            }],
        execute: function() {
            common_1.describe('ElasticQueryDef', function () {
                common_1.describe('getPipelineAggOptions', function () {
                    common_1.describe('with zero targets', function () {
                        var response = query_def_1.default.getPipelineAggOptions([]);
                        common_1.it('should return zero', function () {
                            common_1.expect(response.length).to.be(0);
                        });
                    });
                    common_1.describe('with count and sum targets', function () {
                        var targets = {
                            metrics: [
                                { type: 'count', field: '@value' },
                                { type: 'sum', field: '@value' }
                            ]
                        };
                        var response = query_def_1.default.getPipelineAggOptions(targets);
                        common_1.it('should return zero', function () {
                            common_1.expect(response.length).to.be(2);
                        });
                    });
                    common_1.describe('with count and moving average targets', function () {
                        var targets = {
                            metrics: [
                                { type: 'count', field: '@value' },
                                { type: 'moving_avg', field: '@value' }
                            ]
                        };
                        var response = query_def_1.default.getPipelineAggOptions(targets);
                        common_1.it('should return one', function () {
                            common_1.expect(response.length).to.be(1);
                        });
                    });
                    common_1.describe('with derivatives targets', function () {
                        var targets = {
                            metrics: [
                                { type: 'derivative', field: '@value' }
                            ]
                        };
                        var response = query_def_1.default.getPipelineAggOptions(targets);
                        common_1.it('should return zero', function () {
                            common_1.expect(response.length).to.be(0);
                        });
                    });
                });
                common_1.describe('isPipelineMetric', function () {
                    common_1.describe('moving_avg', function () {
                        var result = query_def_1.default.isPipelineAgg('moving_avg');
                        common_1.it('is pipe line metric', function () {
                            common_1.expect(result).to.be(true);
                        });
                    });
                    common_1.describe('count', function () {
                        var result = query_def_1.default.isPipelineAgg('count');
                        common_1.it('is not pipe line metric', function () {
                            common_1.expect(result).to.be(false);
                        });
                    });
                });
                common_1.describe('pipeline aggs depending on esverison', function () {
                    common_1.describe('using esversion undefined', function () {
                        common_1.it('should not get pipeline aggs', function () {
                            common_1.expect(query_def_1.default.getMetricAggTypes(undefined).length).to.be(9);
                        });
                    });
                    common_1.describe('using esversion 1', function () {
                        common_1.it('should not get pipeline aggs', function () {
                            common_1.expect(query_def_1.default.getMetricAggTypes(1).length).to.be(9);
                        });
                    });
                    common_1.describe('using esversion 2', function () {
                        common_1.it('should get pipeline aggs', function () {
                            common_1.expect(query_def_1.default.getMetricAggTypes(2).length).to.be(11);
                        });
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=query_def_specs.js.map