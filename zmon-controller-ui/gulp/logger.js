var gutil  = require('gulp-util');
var prettyHrtime = require('pretty-hrtime');
var started;

var start = function(message) {
    started = process.hrtime();
    gutil.log(gutil.colors.green(message));
};

var end = function(message) {
    var duration = prettyHrtime(process.hrtime(started));
    gutil.log(gutil.colors.green(message), 'in', gutil.colors.magenta(duration));
}

var error = function(message) {
    gutil.log(gutil.colors.red(message));
};

module.exports = {
    start: start,
    end: end,
    error: error
};
