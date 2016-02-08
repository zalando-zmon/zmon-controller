///<amd-dependency path="app/plugins/datasource/influxdb_08/datasource"/>
///<amd-dependency path="app/core/services/backend_srv"/>
///<amd-dependency path="app/core/services/alert_srv"/>
///<amd-dependency path="test/specs/helpers" name="helpers" />
define(["require", "exports", "test/specs/helpers", 'test/lib/common', "app/plugins/datasource/influxdb_08/datasource", "app/core/services/backend_srv", "app/core/services/alert_srv"], function (require, exports, helpers, common_1) {
    common_1.describe('InfluxDatasource', function () {
        var ctx = new helpers.ServiceTestContext();
        common_1.beforeEach(common_1.angularMocks.module('grafana.core'));
        common_1.beforeEach(common_1.angularMocks.module('grafana.services'));
        common_1.beforeEach(ctx.providePhase(['templateSrv']));
        common_1.beforeEach(ctx.createService('InfluxDatasource_08'));
        common_1.beforeEach(function () {
            ctx.ds = new ctx.service({ url: '', user: 'test', password: 'mupp' });
        });
        common_1.describe('When querying influxdb with one target using query editor target spec', function () {
            var results;
            var urlExpected = "/series?p=mupp&q=select+mean(value)+from+%22test%22+where+time+%3E+now()-1h+group+by+time(1s)+order+asc";
            var query = {
                rangeRaw: { from: 'now-1h', to: 'now' },
                targets: [{ series: 'test', column: 'value', function: 'mean' }],
                interval: '1s'
            };
            var response = [{
                    columns: ["time", "sequence_nr", "value"],
                    name: 'test',
                    points: [[10, 1, 1]],
                }];
            common_1.beforeEach(function () {
                ctx.$httpBackend.expect('GET', urlExpected).respond(response);
                ctx.ds.query(query).then(function (data) { results = data; });
                ctx.$httpBackend.flush();
            });
            common_1.it('should generate the correct query', function () {
                ctx.$httpBackend.verifyNoOutstandingExpectation();
            });
            common_1.it('should return series list', function () {
                common_1.expect(results.data.length).to.be(1);
                common_1.expect(results.data[0].target).to.be('test.value');
            });
        });
        common_1.describe('When querying influxdb with one raw query', function () {
            var results;
            var urlExpected = "/series?p=mupp&q=select+value+from+series+where+time+%3E+now()-1h";
            var query = {
                rangeRaw: { from: 'now-1h', to: 'now' },
                targets: [{ query: "select value from series where $timeFilter", rawQuery: true }]
            };
            var response = [];
            common_1.beforeEach(function () {
                ctx.$httpBackend.expect('GET', urlExpected).respond(response);
                ctx.ds.query(query).then(function (data) { results = data; });
                ctx.$httpBackend.flush();
            });
            common_1.it('should generate the correct query', function () {
                ctx.$httpBackend.verifyNoOutstandingExpectation();
            });
        });
        common_1.describe('When issuing annotation query', function () {
            var results;
            var urlExpected = "/series?p=mupp&q=select+title+from+events.backend_01+where+time+%3E+now()-1h";
            var range = { from: 'now-1h', to: 'now' };
            var annotation = { query: 'select title from events.$server where $timeFilter' };
            var response = [];
            common_1.beforeEach(function () {
                ctx.templateSrv.replace = function (str) {
                    return str.replace('$server', 'backend_01');
                };
                ctx.$httpBackend.expect('GET', urlExpected).respond(response);
                ctx.ds.annotationQuery({ annotation: annotation, rangeRaw: range }).then(function (data) { results = data; });
                ctx.$httpBackend.flush();
            });
            common_1.it('should generate the correct query', function () {
                ctx.$httpBackend.verifyNoOutstandingExpectation();
            });
        });
    });
});
//# sourceMappingURL=datasource-specs.js.map