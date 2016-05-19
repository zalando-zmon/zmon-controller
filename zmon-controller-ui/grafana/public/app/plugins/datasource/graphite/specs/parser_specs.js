System.register(['test/lib/common', '../parser'], function(exports_1) {
    var common_1, parser_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (parser_1_1) {
                parser_1 = parser_1_1;
            }],
        execute: function() {
            common_1.describe('when parsing', function () {
                common_1.it('simple metric expression', function () {
                    var parser = new parser_1.Parser('metric.test.*.asd.count');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('metric');
                    common_1.expect(rootNode.segments.length).to.be(5);
                    common_1.expect(rootNode.segments[0].value).to.be('metric');
                });
                common_1.it('simple metric expression with numbers in segments', function () {
                    var parser = new parser_1.Parser('metric.10.15_20.5');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('metric');
                    common_1.expect(rootNode.segments.length).to.be(4);
                    common_1.expect(rootNode.segments[1].value).to.be('10');
                    common_1.expect(rootNode.segments[2].value).to.be('15_20');
                    common_1.expect(rootNode.segments[3].value).to.be('5');
                });
                common_1.it('simple metric expression with curly braces', function () {
                    var parser = new parser_1.Parser('metric.se1-{count, max}');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('metric');
                    common_1.expect(rootNode.segments.length).to.be(2);
                    common_1.expect(rootNode.segments[1].value).to.be('se1-{count,max}');
                });
                common_1.it('simple metric expression with curly braces at start of segment and with post chars', function () {
                    var parser = new parser_1.Parser('metric.{count, max}-something.count');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('metric');
                    common_1.expect(rootNode.segments.length).to.be(3);
                    common_1.expect(rootNode.segments[1].value).to.be('{count,max}-something');
                });
                common_1.it('simple function', function () {
                    var parser = new parser_1.Parser('sum(test)');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                    common_1.expect(rootNode.params.length).to.be(1);
                });
                common_1.it('simple function2', function () {
                    var parser = new parser_1.Parser('offset(test.metric, -100)');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                    common_1.expect(rootNode.params[0].type).to.be('metric');
                    common_1.expect(rootNode.params[1].type).to.be('number');
                });
                common_1.it('simple function with string arg', function () {
                    var parser = new parser_1.Parser("randomWalk('test')");
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                    common_1.expect(rootNode.params.length).to.be(1);
                    common_1.expect(rootNode.params[0].type).to.be('string');
                });
                common_1.it('function with multiple args', function () {
                    var parser = new parser_1.Parser("sum(test, 1, 'test')");
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                    common_1.expect(rootNode.params.length).to.be(3);
                    common_1.expect(rootNode.params[0].type).to.be('metric');
                    common_1.expect(rootNode.params[1].type).to.be('number');
                    common_1.expect(rootNode.params[2].type).to.be('string');
                });
                common_1.it('function with nested function', function () {
                    var parser = new parser_1.Parser("sum(scaleToSeconds(test, 1))");
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                    common_1.expect(rootNode.params.length).to.be(1);
                    common_1.expect(rootNode.params[0].type).to.be('function');
                    common_1.expect(rootNode.params[0].name).to.be('scaleToSeconds');
                    common_1.expect(rootNode.params[0].params.length).to.be(2);
                    common_1.expect(rootNode.params[0].params[0].type).to.be('metric');
                    common_1.expect(rootNode.params[0].params[1].type).to.be('number');
                });
                common_1.it('function with multiple series', function () {
                    var parser = new parser_1.Parser("sum(test.test.*.count, test.timers.*.count)");
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                    common_1.expect(rootNode.params.length).to.be(2);
                    common_1.expect(rootNode.params[0].type).to.be('metric');
                    common_1.expect(rootNode.params[1].type).to.be('metric');
                });
                common_1.it('function with templated series', function () {
                    var parser = new parser_1.Parser("sum(test.[[server]].count)");
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.message).to.be(undefined);
                    common_1.expect(rootNode.params[0].type).to.be('metric');
                    common_1.expect(rootNode.params[0].segments[1].type).to.be('segment');
                    common_1.expect(rootNode.params[0].segments[1].value).to.be('[[server]]');
                });
                common_1.it('invalid metric expression', function () {
                    var parser = new parser_1.Parser('metric.test.*.asd.');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.message).to.be('Expected metric identifier instead found end of string');
                    common_1.expect(rootNode.pos).to.be(19);
                });
                common_1.it('invalid function expression missing closing parenthesis', function () {
                    var parser = new parser_1.Parser('sum(test');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.message).to.be('Expected closing parenthesis instead found end of string');
                    common_1.expect(rootNode.pos).to.be(9);
                });
                common_1.it('unclosed string in function', function () {
                    var parser = new parser_1.Parser("sum('test)");
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.message).to.be('Unclosed string parameter');
                    common_1.expect(rootNode.pos).to.be(11);
                });
                common_1.it('handle issue #69', function () {
                    var parser = new parser_1.Parser('cactiStyle(offset(scale(net.192-168-1-1.192-168-1-9.ping_value.*,0.001),-100))');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                });
                common_1.it('handle float function arguments', function () {
                    var parser = new parser_1.Parser('scale(test, 0.002)');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                    common_1.expect(rootNode.params[1].type).to.be('number');
                    common_1.expect(rootNode.params[1].value).to.be(0.002);
                });
                common_1.it('handle curly brace pattern at start', function () {
                    var parser = new parser_1.Parser('{apps}.test');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('metric');
                    common_1.expect(rootNode.segments[0].value).to.be('{apps}');
                    common_1.expect(rootNode.segments[1].value).to.be('test');
                });
                common_1.it('series parameters', function () {
                    var parser = new parser_1.Parser('asPercent(#A, #B)');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                    common_1.expect(rootNode.params[0].type).to.be('series-ref');
                    common_1.expect(rootNode.params[0].value).to.be('#A');
                    common_1.expect(rootNode.params[1].value).to.be('#B');
                });
                common_1.it('series parameters, issue 2788', function () {
                    var parser = new parser_1.Parser("summarize(diffSeries(#A, #B), '10m', 'sum', false)");
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.type).to.be('function');
                    common_1.expect(rootNode.params[0].type).to.be('function');
                    common_1.expect(rootNode.params[1].value).to.be('10m');
                    common_1.expect(rootNode.params[3].type).to.be('bool');
                });
                common_1.it('should parse metric expression with ip number segments', function () {
                    var parser = new parser_1.Parser('5.10.123.5');
                    var rootNode = parser.getAst();
                    common_1.expect(rootNode.segments[0].value).to.be('5');
                    common_1.expect(rootNode.segments[1].value).to.be('10');
                    common_1.expect(rootNode.segments[2].value).to.be('123');
                    common_1.expect(rootNode.segments[3].value).to.be('5');
                });
            });
        }
    }
});
//# sourceMappingURL=parser_specs.js.map