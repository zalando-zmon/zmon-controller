exports.config = {

    specs: [
        'e2e/*.js'
    ],

    capabilities: {
        'browserName': 'chrome'
    },

    chromeOnly: true,

    baseUrl: 'https://localhost:8444',

    jasmineNodeOpts: {
        showColors: true,
        isVerbose: true,
        defaultTimeoutInterval: 60000
    }
};
