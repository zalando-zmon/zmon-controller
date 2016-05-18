var search = element(by.model('alertDetailsSearch'));

var details = element(by.css('[ng-click="showDetails = !showDetails"]'));
var history = element(by.css('[heading="History"] a'));
var detailsContainer = element(by.css('.details .panel-body'));

exports.searchAlert = function(input, cb) {
    search.clear();
    search.sendKeys(input).then(function() {
        browser.driver.sleep(1000);
        browser.findElements(by.repeater("entityInstance in allAlertsAndChecks | filter:alertDetailsSearch | inDisplayedGroup:showActiveAlerts:showAlertsInDowntime:showCheckResults | orderBy:sortType:sortOrder track by entityInstance.entity")).then(cb);
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
