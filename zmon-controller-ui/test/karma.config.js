// Karma configuration
// Generated on Mon Sep 07 2015 14:36:57 GMT+0200 (CEST)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
        '../lib/jquery/jquery.min.js',
        '../lib/angularjs/angular.js',
        '../lib/angularjs/angular-mocks.js',
        '../lib/angularjs/angular-route.min.js',
        '../lib/angularjs/angular-cookies.min.js',
        '../lib/angularjs/angular-sanitize.min.js',
        '../lib/angular-local-storage/angular-local-storage.min.js',
        '../lib/bootstrap/bootstrap.min.js',
        '../lib/ui-bootstrap/ui-bootstrap.min.js',
        '../lib/lodash/lodash.min.js',
        '../lib/angulartics/angulartics.min.js',
        '../lib/angular-debounce/angular-debounce.js',
        '../lib/js-yaml/js-yaml.min.js',
        '../lib/ng-infinite-scroll/ng-infinite-scroll.min.js',
        '../js/app.js',
        '../js/filters/*.js',
        '../js/services/*.js',
        '../js/directives/*.js',
        '../js/controllers/*.js',
        'unit/*.spec.js'
    ],


    // list of files to exclude
    exclude: [
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
        '../js/**/*.js': 'coverage'
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    //reporters: ['spec'],
    reporters: ['spec'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['PhantomJS'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Coverage reporter
    coverageReporter: {
        type : 'text',
        dir : 'coverage/'
    }
  })
}
