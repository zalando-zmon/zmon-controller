///<reference path="../../headers/common.d.ts" />
System.register(['lodash', 'moment'], function(exports_1) {
    var lodash_1, moment_1;
    var units;
    function parse(text, roundUp) {
        if (!text) {
            return undefined;
        }
        if (moment_1.default.isMoment(text)) {
            return text;
        }
        if (lodash_1.default.isDate(text)) {
            return moment_1.default(text);
        }
        var time;
        var mathString = '';
        var index;
        var parseString;
        if (text.substring(0, 3) === 'now') {
            time = moment_1.default();
            mathString = text.substring('now'.length);
        }
        else {
            index = text.indexOf('||');
            if (index === -1) {
                parseString = text;
                mathString = ''; // nothing else
            }
            else {
                parseString = text.substring(0, index);
                mathString = text.substring(index + 2);
            }
            // We're going to just require ISO8601 timestamps, k?
            time = moment_1.default(parseString, moment_1.default.ISO_8601);
        }
        if (!mathString.length) {
            return time;
        }
        return parseDateMath(mathString, time, roundUp);
    }
    exports_1("parse", parse);
    function isValid(text) {
        var date = parse(text);
        if (!date) {
            return false;
        }
        if (moment_1.default.isMoment(date)) {
            return date.isValid();
        }
        return false;
    }
    exports_1("isValid", isValid);
    function parseDateMath(mathString, time, roundUp) {
        var dateTime = time;
        var i = 0;
        var len = mathString.length;
        while (i < len) {
            var c = mathString.charAt(i++);
            var type;
            var num;
            var unit;
            if (c === '/') {
                type = 0;
            }
            else if (c === '+') {
                type = 1;
            }
            else if (c === '-') {
                type = 2;
            }
            else {
                return undefined;
            }
            if (isNaN(mathString.charAt(i))) {
                num = 1;
            }
            else if (mathString.length === 2) {
                num = mathString.charAt(i);
            }
            else {
                var numFrom = i;
                while (!isNaN(mathString.charAt(i))) {
                    i++;
                    if (i > 10) {
                        return undefined;
                    }
                }
                num = parseInt(mathString.substring(numFrom, i), 10);
            }
            if (type === 0) {
                // rounding is only allowed on whole, single, units (eg M or 1M, not 0.5M or 2M)
                if (num !== 1) {
                    return undefined;
                }
            }
            unit = mathString.charAt(i++);
            if (!lodash_1.default.contains(units, unit)) {
                return undefined;
            }
            else {
                if (type === 0) {
                    if (roundUp) {
                        dateTime.endOf(unit);
                    }
                    else {
                        dateTime.startOf(unit);
                    }
                }
                else if (type === 1) {
                    dateTime.add(num, unit);
                }
                else if (type === 2) {
                    dateTime.subtract(num, unit);
                }
            }
        }
        return dateTime;
    }
    exports_1("parseDateMath", parseDateMath);
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            }],
        execute: function() {
            units = ['y', 'M', 'w', 'd', 'h', 'm', 's'];
        }
    }
});
//# sourceMappingURL=datemath.js.map