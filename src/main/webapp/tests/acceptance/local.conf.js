var config = require('../../package.json').gulpConfiguration.test.acceptance.local;
var protractor = '../../node_modules/protractor/';
var version = require(protractor + 'package.json').dependencies['selenium-webdriver'];
var selenium = protractor + 'selenium/selenium-server-standalone-' + version +'.jar';

exports.config = {

    // Selenium server
    seleniumServerJar: selenium,
    //seleniumAddress: 'http://localhost:4444/wd/hub',
    framework: 'jasmine',
    // Capabilities to be passed to the webdriver instance.
    // If you want to use PhantomJS as browser please intall PhantomJS globally
    // npm install -g phantomjs
    capabilities: {
        'browserName': config.browser || 'chrome'
    },

    // Spec patterns are relative to the location of the spec file. They may
    // include glob patterns.
    specs: ['./**/*.spec.js'],

    // URL of project
    baseUrl: config.address || 'http://localhost:8080/',

    // Options to be passed to Jasmine-node
    jasmineNodeOpts: {
        showColors: true,
        isVerbose: true,
        defaultTimeoutInterval: 60000
    },

    // Exporting results of test to JUnit XML format
    // jasmine.JUnitXmlReporter(savePath, consolidate, useDotNotation, filePrefix):
    onPrepare: function() {
        require('jasmine-reporters');
        reporter = new jasmine.JUnitXmlReporter('./tests/acceptance/result/', true, true, '');
        jasmine.getEnv().addReporter(reporter);
    },
};
