///<amd-dependency path="app/plugins/datasource/influxdb_08/influx_series" name="InfluxSeries"/>
define(["require", "exports", "app/plugins/datasource/influxdb_08/influx_series", 'test/lib/common'], function (require, exports, InfluxSeries, common_1) {
    common_1.describe('when generating timeseries from influxdb response', function () {
        common_1.describe('given two series', function () {
            var series = new InfluxSeries({
                seriesList: [
                    {
                        columns: ['time', 'mean', 'sequence_number'],
                        name: 'prod.server1.cpu',
                        points: [[1402596000, 10, 1], [1402596001, 12, 2]]
                    },
                    {
                        columns: ['time', 'mean', 'sequence_number'],
                        name: 'prod.server2.cpu',
                        points: [[1402596000, 15, 1], [1402596001, 16, 2]]
                    }
                ]
            });
            var result = series.getTimeSeries();
            common_1.it('should generate two time series', function () {
                common_1.expect(result.length).to.be(2);
                common_1.expect(result[0].target).to.be('prod.server1.cpu.mean');
                common_1.expect(result[0].datapoints[0][0]).to.be(10);
                common_1.expect(result[0].datapoints[0][1]).to.be(1402596000);
                common_1.expect(result[0].datapoints[1][0]).to.be(12);
                common_1.expect(result[0].datapoints[1][1]).to.be(1402596001);
                common_1.expect(result[1].target).to.be('prod.server2.cpu.mean');
                common_1.expect(result[1].datapoints[0][0]).to.be(15);
                common_1.expect(result[1].datapoints[0][1]).to.be(1402596000);
                common_1.expect(result[1].datapoints[1][0]).to.be(16);
                common_1.expect(result[1].datapoints[1][1]).to.be(1402596001);
            });
        });
        common_1.describe('given an alias format', function () {
            var series = new InfluxSeries({
                seriesList: [
                    {
                        columns: ['time', 'mean', 'sequence_number'],
                        name: 'prod.server1.cpu',
                        points: [[1402596000, 10, 1], [1402596001, 12, 2]]
                    }
                ],
                alias: '$s.testing'
            });
            var result = series.getTimeSeries();
            common_1.it('should generate correct series name', function () {
                common_1.expect(result[0].target).to.be('prod.server1.cpu.testing');
            });
        });
        common_1.describe('given an alias format with segment numbers', function () {
            var series = new InfluxSeries({
                seriesList: [
                    {
                        columns: ['time', 'mean', 'sequence_number'],
                        name: 'prod.server1.cpu',
                        points: [[1402596000, 10, 1], [1402596001, 12, 2]]
                    }
                ],
                alias: '$1.mean'
            });
            var result = series.getTimeSeries();
            common_1.it('should generate correct series name', function () {
                common_1.expect(result[0].target).to.be('server1.mean');
            });
        });
        common_1.describe('given an alias format and many segments', function () {
            var series = new InfluxSeries({
                seriesList: [
                    {
                        columns: ['time', 'mean', 'sequence_number'],
                        name: 'a0.a1.a2.a3.a4.a5.a6.a7.a8.a9.a10.a11.a12',
                        points: [[1402596000, 10, 1], [1402596001, 12, 2]]
                    }
                ],
                alias: '$5.$11.mean'
            });
            var result = series.getTimeSeries();
            common_1.it('should generate correct series name', function () {
                common_1.expect(result[0].target).to.be('a5.a11.mean');
            });
        });
        common_1.describe('given an alias format with group by field', function () {
            var series = new InfluxSeries({
                seriesList: [
                    {
                        columns: ['time', 'mean', 'host'],
                        name: 'prod.cpu',
                        points: [[1402596000, 10, 'A']]
                    }
                ],
                groupByField: 'host',
                alias: '$g.$1'
            });
            var result = series.getTimeSeries();
            common_1.it('should generate correct series name', function () {
                common_1.expect(result[0].target).to.be('A.cpu');
            });
        });
        common_1.describe('given group by column', function () {
            var series = new InfluxSeries({
                seriesList: [
                    {
                        columns: ['time', 'mean', 'host'],
                        name: 'prod.cpu',
                        points: [
                            [1402596000, 10, 'A'],
                            [1402596001, 11, 'A'],
                            [1402596000, 5, 'B'],
                            [1402596001, 6, 'B'],
                        ]
                    }
                ],
                groupByField: 'host'
            });
            var result = series.getTimeSeries();
            common_1.it('should generate two time series', function () {
                common_1.expect(result.length).to.be(2);
                common_1.expect(result[0].target).to.be('prod.cpu.A');
                common_1.expect(result[0].datapoints[0][0]).to.be(10);
                common_1.expect(result[0].datapoints[0][1]).to.be(1402596000);
                common_1.expect(result[0].datapoints[1][0]).to.be(11);
                common_1.expect(result[0].datapoints[1][1]).to.be(1402596001);
                common_1.expect(result[1].target).to.be('prod.cpu.B');
                common_1.expect(result[1].datapoints[0][0]).to.be(5);
                common_1.expect(result[1].datapoints[0][1]).to.be(1402596000);
                common_1.expect(result[1].datapoints[1][0]).to.be(6);
                common_1.expect(result[1].datapoints[1][1]).to.be(1402596001);
            });
        });
    });
    common_1.describe("when creating annotations from influxdb response", function () {
        common_1.describe('given column mapping for all columns', function () {
            var series = new InfluxSeries({
                seriesList: [
                    {
                        columns: ['time', 'text', 'sequence_number', 'title', 'tags'],
                        name: 'events1',
                        points: [[1402596000000, 'some text', 1, 'Hello', 'B'], [1402596001000, 'asd', 2, 'Hello2', 'B']]
                    }
                ],
                annotation: {
                    query: 'select',
                    titleColumn: 'title',
                    tagsColumn: 'tags',
                    textColumn: 'text',
                }
            });
            var result = series.getAnnotations();
            common_1.it(' should generate 2 annnotations ', function () {
                common_1.expect(result.length).to.be(2);
                common_1.expect(result[0].annotation.query).to.be('select');
                common_1.expect(result[0].title).to.be('Hello');
                common_1.expect(result[0].time).to.be(1402596000000);
                common_1.expect(result[0].tags).to.be('B');
                common_1.expect(result[0].text).to.be('some text');
            });
        });
        common_1.describe('given no column mapping', function () {
            var series = new InfluxSeries({
                seriesList: [
                    {
                        columns: ['time', 'text', 'sequence_number'],
                        name: 'events1',
                        points: [[1402596000000, 'some text', 1]]
                    }
                ],
                annotation: { query: 'select' }
            });
            var result = series.getAnnotations();
            common_1.it('should generate 1 annnotation', function () {
                common_1.expect(result.length).to.be(1);
                common_1.expect(result[0].title).to.be('some text');
                common_1.expect(result[0].time).to.be(1402596000000);
                common_1.expect(result[0].tags).to.be(undefined);
                common_1.expect(result[0].text).to.be(undefined);
            });
        });
    });
});
//# sourceMappingURL=influx_series_specs.js.map