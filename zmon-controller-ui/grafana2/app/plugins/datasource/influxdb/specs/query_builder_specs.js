///<amd-dependency path="app/plugins/datasource/influxdb/query_builder" name="InfluxQueryBuilder"/>
define(["require", "exports", "app/plugins/datasource/influxdb/query_builder", 'test/lib/common'], function (require, exports, InfluxQueryBuilder, common_1) {
    common_1.describe('InfluxQueryBuilder', function () {
        common_1.describe('series with mesurement only', function () {
            common_1.it('should generate correct query', function () {
                var builder = new InfluxQueryBuilder({
                    measurement: 'cpu',
                    groupBy: [{ type: 'time', interval: 'auto' }]
                });
                var query = builder.build();
                common_1.expect(query).to.be('SELECT mean("value") AS "value" FROM "cpu" WHERE $timeFilter GROUP BY time($interval)');
            });
        });
        common_1.describe('series with math expr and as expr', function () {
            common_1.it('should generate correct query', function () {
                var builder = new InfluxQueryBuilder({
                    measurement: 'cpu',
                    fields: [{ name: 'test', func: 'max', mathExpr: '*2', asExpr: 'new_name' }],
                    groupBy: [{ type: 'time', interval: 'auto' }]
                });
                var query = builder.build();
                common_1.expect(query).to.be('SELECT max("test")*2 AS "new_name" FROM "cpu" WHERE $timeFilter GROUP BY time($interval)');
            });
        });
        common_1.describe('series with single tag only', function () {
            common_1.it('should generate correct query', function () {
                var builder = new InfluxQueryBuilder({
                    measurement: 'cpu',
                    groupBy: [{ type: 'time', interval: 'auto' }],
                    tags: [{ key: 'hostname', value: 'server1' }]
                });
                var query = builder.build();
                common_1.expect(query).to.be('SELECT mean("value") AS "value" FROM "cpu" WHERE "hostname" = \'server1\' AND $timeFilter'
                    + ' GROUP BY time($interval)');
            });
            common_1.it('should switch regex operator with tag value is regex', function () {
                var builder = new InfluxQueryBuilder({
                    measurement: 'cpu',
                    groupBy: [{ type: 'time', interval: 'auto' }],
                    tags: [{ key: 'app', value: '/e.*/' }]
                });
                var query = builder.build();
                common_1.expect(query).to.be('SELECT mean("value") AS "value" FROM "cpu" WHERE "app" =~ /e.*/ AND $timeFilter GROUP BY time($interval)');
            });
        });
        common_1.describe('series with multiple fields', function () {
            common_1.it('should generate correct query', function () {
                var builder = new InfluxQueryBuilder({
                    measurement: 'cpu',
                    tags: [],
                    groupBy: [{ type: 'time', interval: 'auto' }],
                    fields: [{ name: 'tx_in', func: 'sum' }, { name: 'tx_out', func: 'mean' }]
                });
                var query = builder.build();
                common_1.expect(query).to.be('SELECT sum("tx_in") AS "tx_in", mean("tx_out") AS "tx_out" ' +
                    'FROM "cpu" WHERE $timeFilter GROUP BY time($interval)');
            });
        });
        common_1.describe('series with multiple tags only', function () {
            common_1.it('should generate correct query', function () {
                var builder = new InfluxQueryBuilder({
                    measurement: 'cpu',
                    groupBy: [{ type: 'time', interval: 'auto' }],
                    tags: [{ key: 'hostname', value: 'server1' }, { key: 'app', value: 'email', condition: "AND" }]
                });
                var query = builder.build();
                common_1.expect(query).to.be('SELECT mean("value") AS "value" FROM "cpu" WHERE "hostname" = \'server1\' AND "app" = \'email\' AND ' +
                    '$timeFilter GROUP BY time($interval)');
            });
        });
        common_1.describe('series with tags OR condition', function () {
            common_1.it('should generate correct query', function () {
                var builder = new InfluxQueryBuilder({
                    measurement: 'cpu',
                    groupBy: [{ type: 'time', interval: 'auto' }],
                    tags: [{ key: 'hostname', value: 'server1' }, { key: 'hostname', value: 'server2', condition: "OR" }]
                });
                var query = builder.build();
                common_1.expect(query).to.be('SELECT mean("value") AS "value" FROM "cpu" WHERE "hostname" = \'server1\' OR "hostname" = \'server2\' AND ' +
                    '$timeFilter GROUP BY time($interval)');
            });
        });
        common_1.describe('series with groupByTag', function () {
            common_1.it('should generate correct query', function () {
                var builder = new InfluxQueryBuilder({
                    measurement: 'cpu',
                    tags: [],
                    groupBy: [{ type: 'time', interval: 'auto' }, { type: 'tag', key: 'host' }],
                });
                var query = builder.build();
                common_1.expect(query).to.be('SELECT mean("value") AS "value" FROM "cpu" WHERE $timeFilter ' +
                    'GROUP BY time($interval), "host"');
            });
        });
        common_1.describe('when building explore queries', function () {
            common_1.it('should only have measurement condition in tag keys query given query with measurement', function () {
                var builder = new InfluxQueryBuilder({ measurement: 'cpu', tags: [] });
                var query = builder.buildExploreQuery('TAG_KEYS');
                common_1.expect(query).to.be('SHOW TAG KEYS FROM "cpu"');
            });
            common_1.it('should handle regex measurement in tag keys query', function () {
                var builder = new InfluxQueryBuilder({
                    measurement: '/.*/',
                    tags: []
                });
                var query = builder.buildExploreQuery('TAG_KEYS');
                common_1.expect(query).to.be('SHOW TAG KEYS FROM /.*/');
            });
            common_1.it('should have no conditions in tags keys query given query with no measurement or tag', function () {
                var builder = new InfluxQueryBuilder({ measurement: '', tags: [] });
                var query = builder.buildExploreQuery('TAG_KEYS');
                common_1.expect(query).to.be('SHOW TAG KEYS');
            });
            common_1.it('should have where condition in tag keys query with tags', function () {
                var builder = new InfluxQueryBuilder({ measurement: '', tags: [{ key: 'host', value: 'se1' }] });
                var query = builder.buildExploreQuery('TAG_KEYS');
                common_1.expect(query).to.be("SHOW TAG KEYS WHERE \"host\" = 'se1'");
            });
            common_1.it('should have no conditions in measurement query for query with no tags', function () {
                var builder = new InfluxQueryBuilder({ measurement: '', tags: [] });
                var query = builder.buildExploreQuery('MEASUREMENTS');
                common_1.expect(query).to.be('SHOW MEASUREMENTS');
            });
            common_1.it('should have where condition in measurement query for query with tags', function () {
                var builder = new InfluxQueryBuilder({ measurement: '', tags: [{ key: 'app', value: 'email' }] });
                var query = builder.buildExploreQuery('MEASUREMENTS');
                common_1.expect(query).to.be("SHOW MEASUREMENTS WHERE \"app\" = 'email'");
            });
            common_1.it('should have where tag name IN filter in tag values query for query with one tag', function () {
                var builder = new InfluxQueryBuilder({ measurement: '', tags: [{ key: 'app', value: 'asdsadsad' }] });
                var query = builder.buildExploreQuery('TAG_VALUES', 'app');
                common_1.expect(query).to.be('SHOW TAG VALUES WITH KEY = "app"');
            });
            common_1.it('should have measurement tag condition and tag name IN filter in tag values query', function () {
                var builder = new InfluxQueryBuilder({ measurement: 'cpu', tags: [{ key: 'app', value: 'email' }, { key: 'host', value: 'server1' }] });
                var query = builder.buildExploreQuery('TAG_VALUES', 'app');
                common_1.expect(query).to.be('SHOW TAG VALUES FROM "cpu" WITH KEY = "app" WHERE "host" = \'server1\'');
            });
            common_1.it('should switch to regex operator in tag condition', function () {
                var builder = new InfluxQueryBuilder({ measurement: 'cpu', tags: [{ key: 'host', value: '/server.*/' }] });
                var query = builder.buildExploreQuery('TAG_VALUES', 'app');
                common_1.expect(query).to.be('SHOW TAG VALUES FROM "cpu" WITH KEY = "app" WHERE "host" =~ /server.*/');
            });
            common_1.it('should build show field query', function () {
                var builder = new InfluxQueryBuilder({ measurement: 'cpu', tags: [{ key: 'app', value: 'email' }] });
                var query = builder.buildExploreQuery('FIELDS');
                common_1.expect(query).to.be('SHOW FIELD KEYS FROM "cpu"');
            });
        });
    });
});
//# sourceMappingURL=query_builder_specs.js.map