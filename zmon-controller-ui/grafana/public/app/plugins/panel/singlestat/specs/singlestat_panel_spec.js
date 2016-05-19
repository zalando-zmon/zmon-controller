System.register(['test/lib/common', '../module'], function(exports_1) {
    var common_1, module_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (module_1_1) {
                module_1 = module_1_1;
            }],
        execute: function() {
            common_1.describe('grafanaSingleStat', function () {
                common_1.describe('legacy thresholds', function () {
                    common_1.describe('positive thresholds', function () {
                        var data = {
                            colorMap: ['green', 'yellow', 'red'],
                            thresholds: [20, 50]
                        };
                        common_1.it('5 should return green', function () {
                            common_1.expect(module_1.getColorForValue(data, 5)).to.be('green');
                        });
                        common_1.it('25 should return green', function () {
                            common_1.expect(module_1.getColorForValue(data, 25)).to.be('yellow');
                        });
                        common_1.it('55 should return green', function () {
                            common_1.expect(module_1.getColorForValue(data, 55)).to.be('red');
                        });
                    });
                });
                common_1.describe('negative thresholds', function () {
                    var data = {
                        colorMap: ['green', 'yellow', 'red'],
                        thresholds: [0, 20]
                    };
                    common_1.it('-30 should return green', function () {
                        common_1.expect(module_1.getColorForValue(data, -30)).to.be('green');
                    });
                    common_1.it('1 should return green', function () {
                        common_1.expect(module_1.getColorForValue(data, 1)).to.be('yellow');
                    });
                    common_1.it('22 should return green', function () {
                        common_1.expect(module_1.getColorForValue(data, 22)).to.be('red');
                    });
                });
                common_1.describe('negative thresholds', function () {
                    var data = {
                        colorMap: ['green', 'yellow', 'red'],
                        thresholds: [-27, 20]
                    };
                    common_1.it('-30 should return green', function () {
                        common_1.expect(module_1.getColorForValue(data, -26)).to.be('yellow');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=singlestat_panel_spec.js.map