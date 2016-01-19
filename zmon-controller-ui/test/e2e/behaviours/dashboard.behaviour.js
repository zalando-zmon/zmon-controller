var search = element(by.model('alertSearch'));
var compact = element(by.css('[ng-click="toggleCompactView()"]'));
var tagsButton = element(by.css('[ng-click="toggleTagsEditPopup()"]'));

exports.searchAlert = function(input, cb) {
    search.clear();
    search.sendKeys(input).then(function() {
        browser.driver.sleep(1000);
        browser.findElements(by.repeater("alertInstance in alerts | filter:alertSearch | orderBy:['alert_definition.priority', '-oldestStartTime'] track by $id(alertInstance.alert_definition.id)")).then(cb);
    });
};

exports.switchToCompactView = function(cb) {
    compact.click().then(function() {
        browser.findElements(by.css('.compact')).then(cb);
    });
};

exports.openTags = function(cb) {
    tagsButton.click().then(function() {
        browser.findElements(by.css('.dashboard-tags')).then(cb);
    });
};

exports.nameIsLink = function(cb) {
    browser.findElements(by.css('a[href="#/alert-details/1"]')).then(cb);
};
