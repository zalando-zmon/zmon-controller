System.register(['test/lib/common', '../lexer'], function(exports_1) {
    var common_1, lexer_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (lexer_1_1) {
                lexer_1 = lexer_1_1;
            }],
        execute: function() {
            common_1.describe('when lexing graphite expression', function () {
                common_1.it('should tokenize metric expression', function () {
                    var lexer = new lexer_1.Lexer('metric.test.*.asd.count');
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[0].value).to.be('metric');
                    common_1.expect(tokens[1].value).to.be('.');
                    common_1.expect(tokens[2].type).to.be('identifier');
                    common_1.expect(tokens[4].type).to.be('identifier');
                    common_1.expect(tokens[4].pos).to.be(13);
                });
                common_1.it('should tokenize metric expression with dash', function () {
                    var lexer = new lexer_1.Lexer('metric.test.se1-server-*.asd.count');
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[4].type).to.be('identifier');
                    common_1.expect(tokens[4].value).to.be('se1-server-*');
                });
                common_1.it('should tokenize metric expression with dash2', function () {
                    var lexer = new lexer_1.Lexer('net.192-168-1-1.192-168-1-9.ping_value.*');
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[0].value).to.be('net');
                    common_1.expect(tokens[2].value).to.be('192-168-1-1');
                });
                common_1.it('should tokenize metric expression with equal sign', function () {
                    var lexer = new lexer_1.Lexer('apps=test');
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[0].value).to.be('apps=test');
                });
                common_1.it('simple function2', function () {
                    var lexer = new lexer_1.Lexer('offset(test.metric, -100)');
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[2].type).to.be('identifier');
                    common_1.expect(tokens[4].type).to.be('identifier');
                    common_1.expect(tokens[6].type).to.be('number');
                });
                common_1.it('should tokenize metric expression with curly braces', function () {
                    var lexer = new lexer_1.Lexer('metric.se1-{first, second}.count');
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens.length).to.be(10);
                    common_1.expect(tokens[3].type).to.be('{');
                    common_1.expect(tokens[4].value).to.be('first');
                    common_1.expect(tokens[5].value).to.be(',');
                    common_1.expect(tokens[6].value).to.be('second');
                });
                common_1.it('should tokenize metric expression with number segments', function () {
                    var lexer = new lexer_1.Lexer("metric.10.12_10.test");
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[0].type).to.be('identifier');
                    common_1.expect(tokens[2].type).to.be('identifier');
                    common_1.expect(tokens[2].value).to.be('10');
                    common_1.expect(tokens[4].value).to.be('12_10');
                    common_1.expect(tokens[4].type).to.be('identifier');
                });
                common_1.it('should tokenize func call with numbered metric and number arg', function () {
                    var lexer = new lexer_1.Lexer("scale(metric.10, 15)");
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[0].type).to.be('identifier');
                    common_1.expect(tokens[2].type).to.be('identifier');
                    common_1.expect(tokens[2].value).to.be('metric');
                    common_1.expect(tokens[4].value).to.be('10');
                    common_1.expect(tokens[4].type).to.be('number');
                    common_1.expect(tokens[6].type).to.be('number');
                });
                common_1.it('should tokenize metric with template parameter', function () {
                    var lexer = new lexer_1.Lexer("metric.[[server]].test");
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[2].type).to.be('identifier');
                    common_1.expect(tokens[2].value).to.be('[[server]]');
                    common_1.expect(tokens[4].type).to.be('identifier');
                });
                common_1.it('should tokenize metric with question mark', function () {
                    var lexer = new lexer_1.Lexer("metric.server_??.test");
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[2].type).to.be('identifier');
                    common_1.expect(tokens[2].value).to.be('server_??');
                    common_1.expect(tokens[4].type).to.be('identifier');
                });
                common_1.it('should handle error with unterminated string', function () {
                    var lexer = new lexer_1.Lexer("alias(metric, 'asd)");
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[0].value).to.be('alias');
                    common_1.expect(tokens[1].value).to.be('(');
                    common_1.expect(tokens[2].value).to.be('metric');
                    common_1.expect(tokens[3].value).to.be(',');
                    common_1.expect(tokens[4].type).to.be('string');
                    common_1.expect(tokens[4].isUnclosed).to.be(true);
                    common_1.expect(tokens[4].pos).to.be(20);
                });
                common_1.it('should handle float parameters', function () {
                    var lexer = new lexer_1.Lexer("alias(metric, 0.002)");
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[4].type).to.be('number');
                    common_1.expect(tokens[4].value).to.be('0.002');
                });
                common_1.it('should handle bool parameters', function () {
                    var lexer = new lexer_1.Lexer("alias(metric, true, false)");
                    var tokens = lexer.tokenize();
                    common_1.expect(tokens[4].type).to.be('bool');
                    common_1.expect(tokens[4].value).to.be('true');
                    common_1.expect(tokens[6].type).to.be('bool');
                });
            });
        }
    }
});
//# sourceMappingURL=lexer_specs.js.map