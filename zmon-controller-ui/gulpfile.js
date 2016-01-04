var gulp = require('gulp'),
    eslint = require('gulp-eslint'),
    Server = require('karma').Server,
    protractor = require("gulp-protractor").protractor;

gulp.task('lint', function() {
    return gulp
            .src(['./js/**/*.js'])
            .pipe(eslint())
            .pipe(eslint.format())
            .pipe(eslint.failAfterError());
});

gulp.task('protractor', function() {
    return gulp
            .src(['./test/e2e/*.spec.js'])
            .pipe(protractor({
                configFile: 'test/protractor.config.js'
            }))
            .on('error', function(e) { console.log(e) })
});

gulp.task('karma', function(done) {
    new Server({
        configFile: __dirname + '/test/karma.config.js',
        singleRun: true
    }, function() { done(); }).start();
});

gulp.task('coverage', function(done) {
    new Server({
        configFile: __dirname + '/test/karma.config.js',
        reporters: ['coverage'],
        singleRun: true
    }, function() { done(); }).start();
});

gulp.task('default', ['lint']);
