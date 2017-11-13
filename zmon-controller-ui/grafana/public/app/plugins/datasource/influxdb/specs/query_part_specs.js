System.register(['test/lib/common', '../query_part'], function(exports_1) {
    var common_1, query_part_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (query_part_1_1) {
                query_part_1 = query_part_1_1;
            }],
        execute: function() {
            common_1.describe('InfluxQueryPart', function () {
                common_1.describe('series with mesurement only', function () {
                    common_1.it('should handle nested function parts', function () {
                        var part = query_part_1.default.create({
                            type: 'derivative',
                            params: ['10s'],
                        });
                        common_1.expect(part.text).to.be('derivative(10s)');
                        common_1.expect(part.render('mean(value)')).to.be('derivative(mean(value), 10s)');
                    });
                    common_1.it('should nest spread function', function () {
                        var part = query_part_1.default.create({
                            type: 'spread'
                        });
                        common_1.expect(part.text).to.be('spread()');
                        common_1.expect(part.render('value')).to.be('spread(value)');
                    });
                    common_1.it('should handle suffirx parts', function () {
                        var part = query_part_1.default.create({
                            type: 'math',
                            params: ['/ 100'],
                        });
                        common_1.expect(part.text).to.be('math(/ 100)');
                        common_1.expect(part.render('mean(value)')).to.be('mean(value) / 100');
                    });
                    common_1.it('should handle alias parts', function () {
                        var part = query_part_1.default.create({
                            type: 'alias',
                            params: ['test'],
                        });
                        common_1.expect(part.text).to.be('alias(test)');
                        common_1.expect(part.render('mean(value)')).to.be('mean(value) AS "test"');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=query_part_specs.js.map