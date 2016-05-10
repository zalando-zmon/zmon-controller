System.register(['test/lib/common', 'app/core/utils/datemath', 'moment', 'lodash'], function(exports_1) {
    var common_1, dateMath, moment_1, lodash_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (dateMath_1) {
                dateMath = dateMath_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            }],
        execute: function() {
            common_1.describe("DateMath", function () {
                var spans = ['s', 'm', 'h', 'd', 'w', 'M', 'y'];
                var anchor = '2014-01-01T06:06:06.666Z';
                var unix = moment_1.default(anchor).valueOf();
                var format = 'YYYY-MM-DDTHH:mm:ss.SSSZ';
                var clock;
                common_1.describe('errors', function () {
                    common_1.it('should return undefined if passed something falsy', function () {
                        common_1.expect(dateMath.parse(false)).to.be(undefined);
                    });
                    common_1.it('should return undefined if I pass an operator besides [+-/]', function () {
                        common_1.expect(dateMath.parse('now&1d')).to.be(undefined);
                    });
                    common_1.it('should return undefined if I pass a unit besides' + spans.toString(), function () {
                        common_1.expect(dateMath.parse('now+5f')).to.be(undefined);
                    });
                    common_1.it('should return undefined if rounding unit is not 1', function () {
                        common_1.expect(dateMath.parse('now/2y')).to.be(undefined);
                        common_1.expect(dateMath.parse('now/0.5y')).to.be(undefined);
                    });
                    common_1.it('should not go into an infinite loop when missing a unit', function () {
                        common_1.expect(dateMath.parse('now-0')).to.be(undefined);
                        common_1.expect(dateMath.parse('now-00')).to.be(undefined);
                    });
                });
                common_1.it("now/d should set to start of current day", function () {
                    var expected = new Date();
                    expected.setHours(0);
                    expected.setMinutes(0);
                    expected.setSeconds(0);
                    expected.setMilliseconds(0);
                    var startOfDay = dateMath.parse('now/d', false).valueOf();
                    common_1.expect(startOfDay).to.be(expected.getTime());
                });
                common_1.describe('subtraction', function () {
                    var now;
                    var anchored;
                    common_1.beforeEach(function () {
                        clock = common_1.sinon.useFakeTimers(unix);
                        now = moment_1.default();
                        anchored = moment_1.default(anchor);
                    });
                    lodash_1.default.each(spans, function (span) {
                        var nowEx = 'now-5' + span;
                        var thenEx = anchor + '||-5' + span;
                        common_1.it('should return 5' + span + ' ago', function () {
                            common_1.expect(dateMath.parse(nowEx).format(format)).to.eql(now.subtract(5, span).format(format));
                        });
                        common_1.it('should return 5' + span + ' before ' + anchor, function () {
                            common_1.expect(dateMath.parse(thenEx).format(format)).to.eql(anchored.subtract(5, span).format(format));
                        });
                    });
                });
                common_1.describe('rounding', function () {
                    var now;
                    var anchored;
                    common_1.beforeEach(function () {
                        clock = common_1.sinon.useFakeTimers(unix);
                        now = moment_1.default();
                        anchored = moment_1.default(anchor);
                    });
                    lodash_1.default.each(spans, function (span) {
                        common_1.it('should round now to the beginning of the ' + span, function () {
                            common_1.expect(dateMath.parse('now/' + span).format(format)).to.eql(now.startOf(span).format(format));
                        });
                        common_1.it('should round now to the end of the ' + span, function () {
                            common_1.expect(dateMath.parse('now/' + span, true).format(format)).to.eql(now.endOf(span).format(format));
                        });
                    });
                });
                common_1.describe('isValid', function () {
                    common_1.it('should return false when invalid date text', function () {
                        common_1.expect(dateMath.isValid('asd')).to.be(false);
                    });
                    common_1.it('should return true when valid date text', function () {
                        common_1.expect(dateMath.isValid('now-1h')).to.be(true);
                    });
                });
                common_1.describe('relative time to date parsing', function () {
                    common_1.it('should handle negative time', function () {
                        var date = dateMath.parseDateMath('-2d', moment_1.default([2014, 1, 5]));
                        common_1.expect(date.valueOf()).to.equal(moment_1.default([2014, 1, 3]).valueOf());
                    });
                    common_1.it('should handle multiple math expressions', function () {
                        var date = dateMath.parseDateMath('-2d-6h', moment_1.default([2014, 1, 5]));
                        common_1.expect(date.valueOf()).to.equal(moment_1.default([2014, 1, 2, 18]).valueOf());
                    });
                    common_1.it('should return false when invalid expression', function () {
                        var date = dateMath.parseDateMath('2', moment_1.default([2014, 1, 5]));
                        common_1.expect(date).to.equal(undefined);
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=datemath_specs.js.map