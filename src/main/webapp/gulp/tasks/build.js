'use strict';

var pkg = require('../../package.json');
var logger = require('../logger');
var gulp = require('gulp');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var buffer = require('vinyl-buffer');
var uglify = require('gulp-uglify');
var sourcemaps = require('gulp-sourcemaps');

var config = pkg.gulpConfiguration.build;

var task = function() {
    var file = config.bundleName || [pkg.name, pkg.version, 'min.js'].join('.');

    var bundler = browserify({
        entries: config.entries,
        debug: config.debug
    });

    var bundle = function(cb) {
        logger.start('Bundling...')

        var bundle = bundler
            .bundle()
            .on('error', logger.error)
            .pipe(source(file))
            .pipe(buffer());

        if(config.uglify) {
            bundle = bundle
                .pipe(sourcemaps.init({loadMaps: true}))
                .pipe(uglify())
                .pipe(sourcemaps.write(config.destination))
                .on('error', logger.error);
        }

        return bundle
            .pipe(gulp.dest(config.destination))
            .on('error', logger.error)
            .on('end', function() {logger.end('Bundle ' +  file + ' cerated')});
    };

    return bundle();

};

module.exports = {
    run: task,
    deps: config.deps || []
}
