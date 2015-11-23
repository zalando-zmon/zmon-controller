'use strict';

var gulp = require('gulp');

module.exports = function(tasks) {
    tasks.forEach(function(command) {
        var task = require('./tasks/' + command);
        gulp.task(command, task.deps, task.run);
    });

    return gulp;
};
