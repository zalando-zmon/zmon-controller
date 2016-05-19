System.register(['test/lib/common', '../gfunc'], function(exports_1) {
    var common_1, gfunc_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (gfunc_1_1) {
                gfunc_1 = gfunc_1_1;
            }],
        execute: function() {
            common_1.describe('when creating func instance from func names', function () {
                common_1.it('should return func instance', function () {
                    var func = gfunc_1.default.createFuncInstance('sumSeries');
                    common_1.expect(func).to.be.ok();
                    common_1.expect(func.def.name).to.equal('sumSeries');
                    common_1.expect(func.def.params.length).to.equal(5);
                    common_1.expect(func.def.defaultParams.length).to.equal(1);
                });
                common_1.it('should return func instance with shortName', function () {
                    var func = gfunc_1.default.createFuncInstance('sum');
                    common_1.expect(func).to.be.ok();
                });
                common_1.it('should return func instance from funcDef', function () {
                    var func = gfunc_1.default.createFuncInstance('sum');
                    var func2 = gfunc_1.default.createFuncInstance(func.def);
                    common_1.expect(func2).to.be.ok();
                });
                common_1.it('func instance should have text representation', function () {
                    var func = gfunc_1.default.createFuncInstance('groupByNode');
                    func.params[0] = 5;
                    func.params[1] = 'avg';
                    func.updateText();
                    common_1.expect(func.text).to.equal("groupByNode(5, avg)");
                });
            });
            common_1.describe('when rendering func instance', function () {
                common_1.it('should handle single metric param', function () {
                    var func = gfunc_1.default.createFuncInstance('sumSeries');
                    common_1.expect(func.render('hello.metric')).to.equal("sumSeries(hello.metric)");
                });
                common_1.it('should include default params if options enable it', function () {
                    var func = gfunc_1.default.createFuncInstance('scaleToSeconds', { withDefaultParams: true });
                    common_1.expect(func.render('hello')).to.equal("scaleToSeconds(hello, 1)");
                });
                common_1.it('should handle int or interval params with number', function () {
                    var func = gfunc_1.default.createFuncInstance('movingMedian');
                    func.params[0] = '5';
                    common_1.expect(func.render('hello')).to.equal("movingMedian(hello, 5)");
                });
                common_1.it('should handle int or interval params with interval string', function () {
                    var func = gfunc_1.default.createFuncInstance('movingMedian');
                    func.params[0] = '5min';
                    common_1.expect(func.render('hello')).to.equal("movingMedian(hello, '5min')");
                });
                common_1.it('should handle metric param and int param and string param', function () {
                    var func = gfunc_1.default.createFuncInstance('groupByNode');
                    func.params[0] = 5;
                    func.params[1] = 'avg';
                    common_1.expect(func.render('hello.metric')).to.equal("groupByNode(hello.metric, 5, 'avg')");
                });
                common_1.it('should handle function with no metric param', function () {
                    var func = gfunc_1.default.createFuncInstance('randomWalk');
                    func.params[0] = 'test';
                    common_1.expect(func.render(undefined)).to.equal("randomWalk('test')");
                });
                common_1.it('should handle function multiple series params', function () {
                    var func = gfunc_1.default.createFuncInstance('asPercent');
                    func.params[0] = '#B';
                    common_1.expect(func.render('#A')).to.equal("asPercent(#A, #B)");
                });
            });
            common_1.describe('when requesting function categories', function () {
                common_1.it('should return function categories', function () {
                    var catIndex = gfunc_1.default.getCategories();
                    common_1.expect(catIndex.Special.length).to.be.greaterThan(8);
                });
            });
            common_1.describe('when updating func param', function () {
                common_1.it('should update param value and update text representation', function () {
                    var func = gfunc_1.default.createFuncInstance('summarize', { withDefaultParams: true });
                    func.updateParam('1h', 0);
                    common_1.expect(func.params[0]).to.be('1h');
                    common_1.expect(func.text).to.be('summarize(1h, sum, false)');
                });
                common_1.it('should parse numbers as float', function () {
                    var func = gfunc_1.default.createFuncInstance('scale');
                    func.updateParam('0.001', 0);
                    common_1.expect(func.params[0]).to.be('0.001');
                });
            });
            common_1.describe('when updating func param with optional second parameter', function () {
                common_1.it('should update value and text', function () {
                    var func = gfunc_1.default.createFuncInstance('aliasByNode');
                    func.updateParam('1', 0);
                    common_1.expect(func.params[0]).to.be('1');
                });
                common_1.it('should slit text and put value in second param', function () {
                    var func = gfunc_1.default.createFuncInstance('aliasByNode');
                    func.updateParam('4,-5', 0);
                    common_1.expect(func.params[0]).to.be('4');
                    common_1.expect(func.params[1]).to.be('-5');
                    common_1.expect(func.text).to.be('aliasByNode(4, -5)');
                });
                common_1.it('should remove second param when empty string is set', function () {
                    var func = gfunc_1.default.createFuncInstance('aliasByNode');
                    func.updateParam('4,-5', 0);
                    func.updateParam('', 1);
                    common_1.expect(func.params[0]).to.be('4');
                    common_1.expect(func.params[1]).to.be(undefined);
                    common_1.expect(func.text).to.be('aliasByNode(4)');
                });
            });
        }
    }
});
//# sourceMappingURL=gfunc_specs.js.map