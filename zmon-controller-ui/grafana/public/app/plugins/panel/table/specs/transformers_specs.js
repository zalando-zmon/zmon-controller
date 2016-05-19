System.register(['test/lib/common', '../transformers'], function(exports_1) {
    var common_1, transformers_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (transformers_1_1) {
                transformers_1 = transformers_1_1;
            }],
        execute: function() {
            common_1.describe('when transforming time series table', function () {
                var table;
                common_1.describe('given 2 time series', function () {
                    var time = new Date().getTime();
                    var timeSeries = [
                        {
                            target: 'series1',
                            datapoints: [[12.12, time], [14.44, time + 1]],
                        },
                        {
                            target: 'series2',
                            datapoints: [[16.12, time]],
                        }
                    ];
                    common_1.describe('timeseries_to_rows', function () {
                        var panel = {
                            transform: 'timeseries_to_rows',
                            sort: { col: 0, desc: true },
                        };
                        common_1.beforeEach(function () {
                            table = transformers_1.transformDataToTable(timeSeries, panel);
                        });
                        common_1.it('should return 3 rows', function () {
                            common_1.expect(table.rows.length).to.be(3);
                            common_1.expect(table.rows[0][1]).to.be('series1');
                            common_1.expect(table.rows[1][1]).to.be('series1');
                            common_1.expect(table.rows[2][1]).to.be('series2');
                            common_1.expect(table.rows[0][2]).to.be(12.12);
                        });
                        common_1.it('should return 3 rows', function () {
                            common_1.expect(table.columns.length).to.be(3);
                            common_1.expect(table.columns[0].text).to.be('Time');
                            common_1.expect(table.columns[1].text).to.be('Metric');
                            common_1.expect(table.columns[2].text).to.be('Value');
                        });
                    });
                    common_1.describe('timeseries_to_columns', function () {
                        var panel = {
                            transform: 'timeseries_to_columns'
                        };
                        common_1.beforeEach(function () {
                            table = transformers_1.transformDataToTable(timeSeries, panel);
                        });
                        common_1.it('should return 3 columns', function () {
                            common_1.expect(table.columns.length).to.be(3);
                            common_1.expect(table.columns[0].text).to.be('Time');
                            common_1.expect(table.columns[1].text).to.be('series1');
                            common_1.expect(table.columns[2].text).to.be('series2');
                        });
                        common_1.it('should return 2 rows', function () {
                            common_1.expect(table.rows.length).to.be(2);
                            common_1.expect(table.rows[0][1]).to.be(12.12);
                            common_1.expect(table.rows[0][2]).to.be(16.12);
                        });
                        common_1.it('should be undefined when no value for timestamp', function () {
                            common_1.expect(table.rows[1][2]).to.be(undefined);
                        });
                    });
                    common_1.describe('timeseries_aggregations', function () {
                        var panel = {
                            transform: 'timeseries_aggregations',
                            sort: { col: 0, desc: true },
                            columns: [{ text: 'Max', value: 'max' }, { text: 'Min', value: 'min' }]
                        };
                        common_1.beforeEach(function () {
                            table = transformers_1.transformDataToTable(timeSeries, panel);
                        });
                        common_1.it('should return 2 rows', function () {
                            common_1.expect(table.rows.length).to.be(2);
                            common_1.expect(table.rows[0][0]).to.be('series1');
                            common_1.expect(table.rows[0][1]).to.be(14.44);
                            common_1.expect(table.rows[0][2]).to.be(12.12);
                        });
                        common_1.it('should return 2 columns', function () {
                            common_1.expect(table.columns.length).to.be(3);
                            common_1.expect(table.columns[0].text).to.be('Metric');
                            common_1.expect(table.columns[1].text).to.be('Max');
                            common_1.expect(table.columns[2].text).to.be('Min');
                        });
                    });
                    common_1.describe('JSON Data', function () {
                        var panel = {
                            transform: 'json',
                            columns: [
                                { text: 'Timestamp', value: 'timestamp' },
                                { text: 'Message', value: 'message' },
                                { text: 'nested.level2', value: 'nested.level2' },
                            ]
                        };
                        var rawData = [
                            {
                                type: 'docs',
                                datapoints: [
                                    {
                                        timestamp: 'time',
                                        message: 'message',
                                        nested: {
                                            level2: 'level2-value'
                                        }
                                    }
                                ]
                            }
                        ];
                        common_1.describe('getColumns', function () {
                            common_1.it('should return nested properties', function () {
                                var columns = transformers_1.transformers['json'].getColumns(rawData);
                                common_1.expect(columns[0].text).to.be('timestamp');
                                common_1.expect(columns[1].text).to.be('message');
                                common_1.expect(columns[2].text).to.be('nested.level2');
                            });
                        });
                        common_1.describe('transform', function () {
                            common_1.beforeEach(function () {
                                table = transformers_1.transformDataToTable(rawData, panel);
                            });
                            common_1.it('should return 2 columns', function () {
                                common_1.expect(table.columns.length).to.be(3);
                                common_1.expect(table.columns[0].text).to.be('Timestamp');
                                common_1.expect(table.columns[1].text).to.be('Message');
                                common_1.expect(table.columns[2].text).to.be('nested.level2');
                            });
                            common_1.it('should return 2 rows', function () {
                                common_1.expect(table.rows.length).to.be(1);
                                common_1.expect(table.rows[0][0]).to.be('time');
                                common_1.expect(table.rows[0][1]).to.be('message');
                                common_1.expect(table.rows[0][2]).to.be('level2-value');
                            });
                        });
                    });
                    common_1.describe('Annnotations', function () {
                        var panel = { transform: 'annotations' };
                        var rawData = [
                            {
                                min: 1000,
                                text: 'hej',
                                tags: ['tags', 'asd'],
                                title: 'title',
                            }
                        ];
                        common_1.beforeEach(function () {
                            table = transformers_1.transformDataToTable(rawData, panel);
                        });
                        common_1.it('should return 4 columns', function () {
                            common_1.expect(table.columns.length).to.be(4);
                            common_1.expect(table.columns[0].text).to.be('Time');
                            common_1.expect(table.columns[1].text).to.be('Title');
                            common_1.expect(table.columns[2].text).to.be('Text');
                            common_1.expect(table.columns[3].text).to.be('Tags');
                        });
                        common_1.it('should return 1 rows', function () {
                            common_1.expect(table.rows.length).to.be(1);
                            common_1.expect(table.rows[0][0]).to.be(1000);
                        });
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=transformers_specs.js.map