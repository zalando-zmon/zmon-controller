System.register(['test/lib/common', 'app/core/utils/rangeutil', 'lodash', 'moment'], function(exports_1) {
    var common_1, rangeUtil, lodash_1, moment_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (rangeUtil_1) {
                rangeUtil = rangeUtil_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            }],
        execute: function() {
            common_1.describe("rangeUtil", function () {
                common_1.describe("Can get range grouped list of ranges", function () {
                    common_1.it('when custom settings should return default range list', function () {
                        var groups = rangeUtil.getRelativeTimesList({ time_options: [] }, 'Last 5 minutes');
                        common_1.expect(lodash_1.default.keys(groups).length).to.be(4);
                        common_1.expect(groups[3][0].active).to.be(true);
                    });
                    // it('should add custom options to right section', () => {
                    //   var groups = rangeUtil.getRelativeTimesList({
                    //     time_options: ['12m', '15d']
                    //   }, '');
                    //   var value = _.findWhere(groups["3"], {display: 'Last 12 minutes'});
                    //   expect(value).to.not.be(undefined)
                    // });
                });
                common_1.describe("Can get range text described", function () {
                    common_1.it('should handle simple old expression with only amount and unit', function () {
                        var info = rangeUtil.describeTextRange('5m');
                        common_1.expect(info.display).to.be('Last 5 minutes');
                    });
                    common_1.it('should have singular when amount is 1', function () {
                        var info = rangeUtil.describeTextRange('1h');
                        common_1.expect(info.display).to.be('Last 1 hour');
                    });
                    common_1.it('should handle non default amount', function () {
                        var info = rangeUtil.describeTextRange('13h');
                        common_1.expect(info.display).to.be('Last 13 hours');
                        common_1.expect(info.from).to.be('now-13h');
                    });
                    common_1.it('should handle now/d', function () {
                        var info = rangeUtil.describeTextRange('now/d');
                        common_1.expect(info.display).to.be('Today so far');
                    });
                    common_1.it('should handle now/w', function () {
                        var info = rangeUtil.describeTextRange('now/w');
                        common_1.expect(info.display).to.be('This week so far');
                    });
                });
                common_1.describe("Can get date range described", function () {
                    common_1.it('Date range with simple ranges', function () {
                        var text = rangeUtil.describeTimeRange({ from: 'now-1h', to: 'now' });
                        common_1.expect(text).to.be('Last 1 hour');
                    });
                    common_1.it('Date range with rounding ranges', function () {
                        var text = rangeUtil.describeTimeRange({ from: 'now/d+6h', to: 'now' });
                        common_1.expect(text).to.be('now/d+6h to now');
                    });
                    common_1.it('Date range with absolute to now', function () {
                        var text = rangeUtil.describeTimeRange({ from: moment_1.default([2014, 10, 10, 2, 3, 4]), to: 'now' });
                        common_1.expect(text).to.be('Nov 10, 2014 02:03:04 to a few seconds ago');
                    });
                    common_1.it('Date range with absolute to relative', function () {
                        var text = rangeUtil.describeTimeRange({ from: moment_1.default([2014, 10, 10, 2, 3, 4]), to: 'now-1d' });
                        common_1.expect(text).to.be('Nov 10, 2014 02:03:04 to a day ago');
                    });
                    common_1.it('Date range with relative to absolute', function () {
                        var text = rangeUtil.describeTimeRange({ from: 'now-7d', to: moment_1.default([2014, 10, 10, 2, 3, 4]) });
                        common_1.expect(text).to.be('7 days ago to Nov 10, 2014 02:03:04');
                    });
                    common_1.it('Date range with non matching default ranges', function () {
                        var text = rangeUtil.describeTimeRange({ from: 'now-13h', to: 'now' });
                        common_1.expect(text).to.be('Last 13 hours');
                    });
                    common_1.it('Date range with from and to both are in now-* format', function () {
                        var text = rangeUtil.describeTimeRange({ from: 'now-6h', to: 'now-3h' });
                        common_1.expect(text).to.be('now-6h to now-3h');
                    });
                    common_1.it('Date range with from and to both are either in now-* or now/* format', function () {
                        var text = rangeUtil.describeTimeRange({ from: 'now/d+6h', to: 'now-3h' });
                        common_1.expect(text).to.be('now/d+6h to now-3h');
                    });
                    common_1.it('Date range with from and to both are either in now-* or now+* format', function () {
                        var text = rangeUtil.describeTimeRange({ from: 'now-6h', to: 'now+1h' });
                        common_1.expect(text).to.be('now-6h to now+1h');
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=rangeutil_specs.js.map