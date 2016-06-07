var search = element(by.model('alertDetailsSearch.str'));

var details = element(by.css('[ng-click="showDetails = !showDetails"]'));
var history = element(by.css('[heading="History"] a'));
var okBtn = element(by.buttonText('OK (2)'));
var detailsContainer = element(by.css('.details .panel-body'));

exports.searchAlert = function(input, cb) {
    okBtn.click().then(function() {
        search.clear();
        search.sendKeys(input).then(function() {
            browser.driver.sleep(1000);
            browser.findElements(by.repeater("entityInstance in allAlertsAndChecks | filter:alertDetailsSearch.str | inDisplayedGroup:showActiveAlerts:showAlertsInDowntime:showCheckResults | orderBy:sortType:sortOrder track by entityInstance.entity")).then(cb);
        });
    });
};

exports.openDetails = function(cb) {
    details.click().then(function() {
        browser.findElements(by.css('.panel-collapsed')).then(cb);
    });
};

exports.openHistoryTab = function(cb) {
    history.click().then(function() {
        browser.findElements(by.css('[ng-click="fetchHistoryLastNDays(1)"]')).then(cb);
    });
};

exports.checkDetails = function(cb) {
    details.click().then(function() {
        if(cb) cb(detailsContainer);
    });
};
