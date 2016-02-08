///<amd-dependency path="../index_pattern" name="IndexPattern"/>
///<amd-dependency path="test/specs/helpers" name="helpers" />
define(["require", "exports", "../index_pattern", "test/specs/helpers", 'test/lib/common', 'moment'], function (require, exports, IndexPattern, helpers, common_1, moment) {
    common_1.describe('IndexPattern', function () {
        common_1.describe('when getting index for today', function () {
            common_1.it('should return correct index name', function () {
                var pattern = new IndexPattern('[asd-]YYYY.MM.DD', 'Daily');
                var expected = 'asd-' + moment.utc().format('YYYY.MM.DD');
                common_1.expect(pattern.getIndexForToday()).to.be(expected);
            });
        });
        common_1.describe('when getting index list for time range', function () {
            common_1.describe('no interval', function () {
                common_1.it('should return correct index', function () {
                    var pattern = new IndexPattern('my-metrics');
                    var from = new Date(2015, 4, 30, 1, 2, 3);
                    var to = new Date(2015, 5, 1, 12, 5, 6);
                    common_1.expect(pattern.getIndexList(from, to)).to.eql('my-metrics');
                });
            });
            common_1.describe('daily', function () {
                common_1.it('should return correct index list', function () {
                    var pattern = new IndexPattern('[asd-]YYYY.MM.DD', 'Daily');
                    var from = new Date(1432940523000);
                    var to = new Date(1433153106000);
                    var expected = [
                        'asd-2015.05.29',
                        'asd-2015.05.30',
                        'asd-2015.05.31',
                        'asd-2015.06.01',
                    ];
                    common_1.expect(pattern.getIndexList(from, to)).to.eql(expected);
                });
            });
        });
    });
});
//# sourceMappingURL=index_pattern_specs.js.map