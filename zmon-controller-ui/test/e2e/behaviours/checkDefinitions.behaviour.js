var search = element(by.model('checkFilter'));

exports.searchCheck = function(input, cb) {
    search.clear();
    search.sendKeys(input).then(function() {
        browser.driver.sleep(1000);
        browser.findElements(by.repeater("def in checkDefinitions| orderBy:sortType:sortOrder | filter:checkFilter | limitTo: limit")).then(cb);
    });
};

exports.filterTeam = function(cb) {
    var teamMenu = element(by.css('.dropdown a#simple-dropdown'));
    teamMenu.click().then(function() {
        browser.findElements(by.css('.dropdown a#simple-dropdown')).then(cb);
    });
};
