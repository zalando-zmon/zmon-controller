var ptor = protractor.getInstance();
var flow = protractor.promise.controlFlow();

var credentials = {
    username: 'admin',
    password: 'admin'
};

var username = element(by.css('[data-test="username"]'));
var password = element(by.css('[data-test="password"]'));
var login = element(by.css('[data-test="login"]'));
var logout = element(by.css('[data-test="logout"]'));
var page = element(by.css('[data-test="pagexxx"]'));

exports.login = function(cb) {
    browser.ignoreSynchronization = true;

    browser.get('/login.jsp');

    element(by.css('[name="j_username"]')).sendKeys('elauria');
    element(by.css('[name="j_password"]')).sendKeys('Test12345');
    element(by.css('button')).click().then(function() {
        if(cb) cb(true);
        browser.ignoreSynchronization = false;
    });
};

exports.logout = function(cb) {

    return flow.execute(function() {
        var deferred = protractor.promise.defer();

        logout.click().then(function(){
            browser.wait(function(){
                var deferred = protractor.promise.defer();

                page.isPresent().then(function(present) {
                    deferred.fulfill(!present);
                });

                return deferred.promise;
            }, 5000);

        });

        return deferred.promise;
    });

};
