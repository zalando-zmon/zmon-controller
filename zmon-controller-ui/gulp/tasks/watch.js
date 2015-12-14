'use strict';

var pkg = require('../../package.json');
var gulp = require('gulp');
var watch = require('gulp-watch');
var build = require('./build');
var html2js = require('./html2js');

var config = pkg.gulpConfiguration.watch;

var task = function() {
    var src = config.src || 'application/source/**/*';

    return gulp.src(src)
        .pipe(watch(src, function(files, cb) {
            html2js.run().on('end', function() {
                build.run().on('end', cb);
            });
        }));
};

module.exports = {
    run: task,
    deps: config.deps || []
}
