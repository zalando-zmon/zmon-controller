///<reference path="../../headers/common.d.ts" />
System.register(['lodash', 'moment', './datemath'], function(exports_1) {
    var lodash_1, moment_1, dateMath;
    var spans, rangeOptions, absoluteFormat, rangeIndex;
    function getRelativeTimesList(timepickerSettings, currentDisplay) {
        var groups = lodash_1.default.groupBy(rangeOptions, function (option) {
            option.active = option.display === currentDisplay;
            return option.section;
        });
        // _.each(timepickerSettings.time_options, (duration: string) => {
        //   let info = describeTextRange(duration);
        //   if (info.section) {
        //     groups[info.section].push(info);
        //   }
        // });
        return groups;
    }
    exports_1("getRelativeTimesList", getRelativeTimesList);
    function formatDate(date) {
        return date.format(absoluteFormat);
    }
    // handles expressions like
    // 5m
    // 5m to now/d
    // now/d to now
    // now/d
    // if no to <expr> then to now is assumed
    function describeTextRange(expr) {
        if (expr.indexOf('now') === -1) {
            expr = 'now-' + expr;
        }
        var opt = rangeIndex[expr + ' to now'];
        if (opt) {
            return opt;
        }
        opt = { from: expr, to: 'now' };
        var parts = /^now-(\d+)(\w)/.exec(expr);
        if (parts) {
            var unit = parts[2];
            var amount = parseInt(parts[1]);
            var span = spans[unit];
            if (span) {
                opt.display = 'Last ' + amount + ' ' + span.display;
                opt.section = span.section;
                if (amount > 1) {
                    opt.display += 's';
                }
            }
        }
        else {
            opt.display = opt.from + ' to ' + opt.to;
            opt.invalid = true;
        }
        return opt;
    }
    exports_1("describeTextRange", describeTextRange);
    function describeTimeRange(range) {
        var option = rangeIndex[range.from.toString() + ' to ' + range.to.toString()];
        if (option) {
            return option.display;
        }
        if (moment_1.default.isMoment(range.from) && moment_1.default.isMoment(range.to)) {
            return formatDate(range.from) + ' to ' + formatDate(range.to);
        }
        if (moment_1.default.isMoment(range.from)) {
            var toMoment = dateMath.parse(range.to, true);
            return formatDate(range.from) + ' to ' + toMoment.fromNow();
        }
        if (moment_1.default.isMoment(range.to)) {
            var from = dateMath.parse(range.from, false);
            return from.fromNow() + ' to ' + formatDate(range.to);
        }
        if (range.to.toString() === 'now') {
            var res = describeTextRange(range.from);
            return res.display;
        }
        return range.from.toString() + ' to ' + range.to.toString();
    }
    exports_1("describeTimeRange", describeTimeRange);
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (dateMath_1) {
                dateMath = dateMath_1;
            }],
        execute: function() {
            spans = {
                's': { display: 'second' },
                'm': { display: 'minute' },
                'h': { display: 'hour' },
                'd': { display: 'day' },
                'w': { display: 'week' },
                'M': { display: 'month' },
                'y': { display: 'year' },
            };
            rangeOptions = [
                { from: 'now/d', to: 'now/d', display: 'Today', section: 2 },
                { from: 'now/d', to: 'now', display: 'Today so far', section: 2 },
                { from: 'now/w', to: 'now/w', display: 'This week', section: 2 },
                { from: 'now/w', to: 'now', display: 'This week so far', section: 2 },
                { from: 'now/M', to: 'now/M', display: 'This month', section: 2 },
                { from: 'now/y', to: 'now/y', display: 'This year', section: 2 },
                { from: 'now-1d/d', to: 'now-1d/d', display: 'Yesterday', section: 1 },
                { from: 'now-2d/d', to: 'now-2d/d', display: 'Day before yesterday', section: 1 },
                { from: 'now-7d/d', to: 'now-7d/d', display: 'This day last week', section: 1 },
                { from: 'now-1w/w', to: 'now-1w/w', display: 'Previous week', section: 1 },
                { from: 'now-1M/M', to: 'now-1M/M', display: 'Previous month', section: 1 },
                { from: 'now-1y/y', to: 'now-1y/y', display: 'Previous year', section: 1 },
                { from: 'now-5m', to: 'now', display: 'Last 5 minutes', section: 3 },
                { from: 'now-15m', to: 'now', display: 'Last 15 minutes', section: 3 },
                { from: 'now-30m', to: 'now', display: 'Last 30 minutes', section: 3 },
                { from: 'now-1h', to: 'now', display: 'Last 1 hour', section: 3 },
                { from: 'now-3h', to: 'now', display: 'Last 3 hours', section: 3 },
                { from: 'now-6h', to: 'now', display: 'Last 6 hours', section: 3 },
                { from: 'now-12h', to: 'now', display: 'Last 12 hours', section: 3 },
                { from: 'now-24h', to: 'now', display: 'Last 24 hours', section: 3 },
                { from: 'now-7d', to: 'now', display: 'Last 7 days', section: 0 },
                { from: 'now-30d', to: 'now', display: 'Last 30 days', section: 0 },
                { from: 'now-60d', to: 'now', display: 'Last 60 days', section: 0 },
                { from: 'now-90d', to: 'now', display: 'Last 90 days', section: 0 },
                { from: 'now-6M', to: 'now', display: 'Last 6 months', section: 0 },
                { from: 'now-1y', to: 'now', display: 'Last 1 year', section: 0 },
                { from: 'now-2y', to: 'now', display: 'Last 2 years', section: 0 },
                { from: 'now-5y', to: 'now', display: 'Last 5 years', section: 0 },
            ];
            absoluteFormat = 'MMM D, YYYY HH:mm:ss';
            rangeIndex = {};
            lodash_1.default.each(rangeOptions, function (frame) {
                rangeIndex[frame.from + ' to ' + frame.to] = frame;
            });
        }
    }
});
//# sourceMappingURL=rangeutil.js.map