System.register(['test/lib/common', '../influx_query'], function(exports_1) {
    var common_1, influx_query_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (influx_query_1_1) {
                influx_query_1 = influx_query_1_1;
            }],
        execute: function() {
            common_1.describe('InfluxQuery', function () {
                var templateSrv = { replace: function (val) { return val; } };
                common_1.describe('render series with mesurement only', function () {
                    common_1.it('should generate correct query', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT mean("value") FROM "cpu" WHERE $timeFilter GROUP BY time($interval) fill(null)');
                    });
                });
                common_1.describe('render series with policy only', function () {
                    common_1.it('should generate correct query', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            policy: '5m_avg'
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT mean("value") FROM "5m_avg"."cpu" WHERE $timeFilter GROUP BY time($interval) fill(null)');
                    });
                });
                common_1.describe('render series with math and alias', function () {
                    common_1.it('should generate correct query', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            select: [
                                [
                                    { type: 'field', params: ['value'] },
                                    { type: 'mean', params: [] },
                                    { type: 'math', params: ['/100'] },
                                    { type: 'alias', params: ['text'] },
                                ]
                            ]
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT mean("value") /100 AS "text" FROM "cpu" WHERE $timeFilter GROUP BY time($interval) fill(null)');
                    });
                });
                common_1.describe('series with single tag only', function () {
                    common_1.it('should generate correct query', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            groupBy: [{ type: 'time', params: ['auto'] }],
                            tags: [{ key: 'hostname', value: 'server\\1' }]
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT mean("value") FROM "cpu" WHERE "hostname" = \'server\\\\1\' AND $timeFilter'
                            + ' GROUP BY time($interval)');
                    });
                    common_1.it('should switch regex operator with tag value is regex', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            groupBy: [{ type: 'time', params: ['auto'] }],
                            tags: [{ key: 'app', value: '/e.*/' }]
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT mean("value") FROM "cpu" WHERE "app" =~ /e.*/ AND $timeFilter GROUP BY time($interval)');
                    });
                });
                common_1.describe('series with multiple tags only', function () {
                    common_1.it('should generate correct query', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            groupBy: [{ type: 'time', params: ['auto'] }],
                            tags: [{ key: 'hostname', value: 'server1' }, { key: 'app', value: 'email', condition: "AND" }]
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT mean("value") FROM "cpu" WHERE "hostname" = \'server1\' AND "app" = \'email\' AND ' +
                            '$timeFilter GROUP BY time($interval)');
                    });
                });
                common_1.describe('series with tags OR condition', function () {
                    common_1.it('should generate correct query', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            groupBy: [{ type: 'time', params: ['auto'] }],
                            tags: [{ key: 'hostname', value: 'server1' }, { key: 'hostname', value: 'server2', condition: "OR" }]
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT mean("value") FROM "cpu" WHERE "hostname" = \'server1\' OR "hostname" = \'server2\' AND ' +
                            '$timeFilter GROUP BY time($interval)');
                    });
                });
                common_1.describe('query with value condition', function () {
                    common_1.it('should not quote value', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            groupBy: [],
                            tags: [{ key: 'value', value: '5', operator: '>' }]
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT mean("value") FROM "cpu" WHERE "value" > 5 AND $timeFilter');
                    });
                });
                common_1.describe('series with groupByTag', function () {
                    common_1.it('should generate correct query', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            tags: [],
                            groupBy: [{ type: 'time', interval: 'auto' }, { type: 'tag', params: ['host'] }],
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT mean("value") FROM "cpu" WHERE $timeFilter ' +
                            'GROUP BY time($interval), "host"');
                    });
                });
                common_1.describe('render series without group by', function () {
                    common_1.it('should generate correct query', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            select: [[{ type: 'field', params: ['value'] }]],
                            groupBy: [],
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT "value" FROM "cpu" WHERE $timeFilter');
                    });
                });
                common_1.describe('render series without group by and fill', function () {
                    common_1.it('should generate correct query', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            select: [[{ type: 'field', params: ['value'] }]],
                            groupBy: [{ type: 'time' }, { type: 'fill', params: ['0'] }],
                        }, templateSrv, {});
                        var queryText = query.render();
                        common_1.expect(queryText).to.be('SELECT "value" FROM "cpu" WHERE $timeFilter GROUP BY time($interval) fill(0)');
                    });
                });
                common_1.describe('when adding group by part', function () {
                    common_1.it('should add tag before fill', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            groupBy: [{ type: 'time' }, { type: 'fill' }]
                        }, templateSrv, {});
                        query.addGroupBy('tag(host)');
                        common_1.expect(query.target.groupBy.length).to.be(3);
                        common_1.expect(query.target.groupBy[1].type).to.be('tag');
                        common_1.expect(query.target.groupBy[1].params[0]).to.be('host');
                        common_1.expect(query.target.groupBy[2].type).to.be('fill');
                    });
                    common_1.it('should add tag last if no fill', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            groupBy: []
                        }, templateSrv, {});
                        query.addGroupBy('tag(host)');
                        common_1.expect(query.target.groupBy.length).to.be(1);
                        common_1.expect(query.target.groupBy[0].type).to.be('tag');
                    });
                });
                common_1.describe('when adding select part', function () {
                    common_1.it('should add mean after after field', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            select: [[{ type: 'field', params: ['value'] }]]
                        }, templateSrv, {});
                        query.addSelectPart(query.selectModels[0], 'mean');
                        common_1.expect(query.target.select[0].length).to.be(2);
                        common_1.expect(query.target.select[0][1].type).to.be('mean');
                    });
                    common_1.it('should replace sum by mean', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            select: [[{ type: 'field', params: ['value'] }, { type: 'mean' }]]
                        }, templateSrv, {});
                        query.addSelectPart(query.selectModels[0], 'sum');
                        common_1.expect(query.target.select[0].length).to.be(2);
                        common_1.expect(query.target.select[0][1].type).to.be('sum');
                    });
                    common_1.it('should add math before alias', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            select: [[{ type: 'field', params: ['value'] }, { type: 'mean' }, { type: 'alias' }]]
                        }, templateSrv, {});
                        query.addSelectPart(query.selectModels[0], 'math');
                        common_1.expect(query.target.select[0].length).to.be(4);
                        common_1.expect(query.target.select[0][2].type).to.be('math');
                    });
                    common_1.it('should add math last', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            select: [[{ type: 'field', params: ['value'] }, { type: 'mean' }]]
                        }, templateSrv, {});
                        query.addSelectPart(query.selectModels[0], 'math');
                        common_1.expect(query.target.select[0].length).to.be(3);
                        common_1.expect(query.target.select[0][2].type).to.be('math');
                    });
                    common_1.it('should replace math', function () {
                        var query = new influx_query_1.default({
                            measurement: 'cpu',
                            select: [[{ type: 'field', params: ['value'] }, { type: 'mean' }, { type: 'math' }]]
                        }, templateSrv, {});
                        query.addSelectPart(query.selectModels[0], 'math');
                        common_1.expect(query.target.select[0].length).to.be(3);
                        common_1.expect(query.target.select[0][2].type).to.be('math');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=influx_query_specs.js.map