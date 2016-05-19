System.register(['test/lib/common', 'app/core/table_model'], function(exports_1) {
    var common_1, table_model_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (table_model_1_1) {
                table_model_1 = table_model_1_1;
            }],
        execute: function() {
            common_1.describe('when sorting table desc', function () {
                var table;
                var panel = {
                    sort: { col: 0, desc: true },
                };
                common_1.beforeEach(function () {
                    table = new table_model_1.default();
                    table.columns = [{}, {}];
                    table.rows = [[100, 12], [105, 10], [103, 11]];
                    table.sort(panel.sort);
                });
                common_1.it('should sort by time', function () {
                    common_1.expect(table.rows[0][0]).to.be(105);
                    common_1.expect(table.rows[1][0]).to.be(103);
                    common_1.expect(table.rows[2][0]).to.be(100);
                });
                common_1.it('should mark column being sorted', function () {
                    common_1.expect(table.columns[0].sort).to.be(true);
                    common_1.expect(table.columns[0].desc).to.be(true);
                });
            });
            common_1.describe('when sorting table asc', function () {
                var table;
                var panel = {
                    sort: { col: 1, desc: false },
                };
                common_1.beforeEach(function () {
                    table = new table_model_1.default();
                    table.columns = [{}, {}];
                    table.rows = [[100, 11], [105, 15], [103, 10]];
                    table.sort(panel.sort);
                });
                common_1.it('should sort by time', function () {
                    common_1.expect(table.rows[0][1]).to.be(10);
                    common_1.expect(table.rows[1][1]).to.be(11);
                    common_1.expect(table.rows[2][1]).to.be(15);
                });
            });
        }
    }
});
//# sourceMappingURL=table_model_specs.js.map