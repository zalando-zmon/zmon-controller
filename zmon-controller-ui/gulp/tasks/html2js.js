'use strict';

var logger = require('../logger');
var gulp = require('gulp');
var html2js = require('gulp-ng-html2js');
var minify = require('gulp-minify-html');
var concat = require('gulp-concat');

var task = function() {
    logger.start('HTML compiling...');

    return gulp.src('./application/source/**/*.html')
        .pipe(html2js({
            moduleName: 'templates'
        }))
        .pipe(concat('templates.js'))
        .pipe(gulp.dest('./.tmp/')
            .on('end', function() {
                logger.end('HTML files compiled');
            })
        );
};

module.exports = {
    run: task,
    deps: []
}
