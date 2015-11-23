var ptor = protractor.getInstance();

var search = element(by.css('[data-test=search]'));
var details = element(by.css('[data-test=details]'));
var history = element(by.css('[data-test=history]'));
var detailsContainer = element(by.css('.details .panel-body'));

exports.searchAlert = function(input, cb) {
    search.clear();
    search.sendKeys(input).then(function() {
        ptor.findElements(by.repeater("entityInstance in AlertDetailsCtrl.allAlertsAndChecks | filter:alertDetailsSearch | inDisplayedGroup:AlertDetailsCtrl.showActiveAlerts:AlertDetailsCtrl.showAlertsInDowntime:AlertDetailsCtrl.showCheckResults | orderBy:'entity' | limitTo:AlertDetailsCtrl.infScrollNumAlertsVisible track by entityInstance.entity")).then(cb);
    });
};

exports.openDetails = function(cb) {
    details.click().then(function() {
        ptor.findElements(by.css('.panel-collapsed')).then(cb);
    });
};

/* FIXME
exports.openHistoryTab = function(cb) {
    history.click().then(function() {
        ptor.findElements(by.css('[data-test=lastDayButton]')).then(cb);
    });
};
*/

exports.checkDetails = function(cb) {
    details.click().then(function() {
        if(cb) cb(detailsContainer);
    });
};
