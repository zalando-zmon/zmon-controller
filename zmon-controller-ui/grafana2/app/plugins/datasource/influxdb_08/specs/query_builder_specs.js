///<amd-dependency path="app/plugins/datasource/influxdb_08/query_builder" name="InfluxQueryBuilder"/>
define(["require", "exports", "app/plugins/datasource/influxdb_08/query_builder", 'test/lib/common'], function (require, exports, InfluxQueryBuilder, common_1) {
    common_1.describe('InfluxQueryBuilder', function () {
        common_1.describe('series with conditon and group by', function () {
            var builder = new InfluxQueryBuilder({
                series: 'google.test',
                column: 'value',
                function: 'mean',
                condition: "code=1",
                groupby_field: 'code'
            });
            var query = builder.build();
            common_1.it('should generate correct query', function () {
                common_1.expect(query).to.be('select code, mean(value) from "google.test" where $timeFilter and code=1 ' +
                    'group by time($interval), code order asc');
            });
            common_1.it('should expose groupByFiled', function () {
                common_1.expect(builder.groupByField).to.be('code');
            });
        });
        common_1.describe('series with fill and minimum group by time', function () {
            var builder = new InfluxQueryBuilder({
                series: 'google.test',
                column: 'value',
                function: 'mean',
                fill: '0',
            });
            var query = builder.build();
            common_1.it('should generate correct query', function () {
                common_1.expect(query).to.be('select mean(value) from "google.test" where $timeFilter ' +
                    'group by time($interval) fill(0) order asc');
            });
        });
        common_1.describe('merge function detection', function () {
            common_1.it('should not quote wrap regex merged series', function () {
                var builder = new InfluxQueryBuilder({
                    series: 'merge(/^google.test/)',
                    column: 'value',
                    function: 'mean'
                });
                var query = builder.build();
                common_1.expect(query).to.be('select mean(value) from merge(/^google.test/) where $timeFilter ' +
                    'group by time($interval) order asc');
            });
            common_1.it('should quote wrap series names that start with "merge"', function () {
                var builder = new InfluxQueryBuilder({
                    series: 'merge.google.test',
                    column: 'value',
                    function: 'mean'
                });
                var query = builder.build();
                common_1.expect(query).to.be('select mean(value) from "merge.google.test" where $timeFilter ' +
                    'group by time($interval) order asc');
            });
        });
    });
});
//# sourceMappingURL=query_builder_specs.js.map