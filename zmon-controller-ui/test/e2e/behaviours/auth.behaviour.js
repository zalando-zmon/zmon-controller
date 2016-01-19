var flow = protractor.promise.controlFlow();
var logout = element(by.css('[data-test="logout"]'));
var button = element(by.id('clickme'));

exports.login = function(cb) {

    // login through github must be done manually for now
    // once only, at the beginning of the test run
    // browser.driver.get('https://localhost:8443/signin');
    browser.driver.sleep(10000);
    cb(true);
};
