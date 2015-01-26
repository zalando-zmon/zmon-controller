var ptor = protractor.getInstance();

var search = element(by.css('[data-test=search]'));
var compact = element(by.css('[data-test=compact]'));
var tagsButton = element(by.css('[data-test=tagsButton]'));

exports.searchAlert = function(input, cb) {
    search.clear();
    search.sendKeys(input).then(function() {
        ptor.findElements(by.repeater("alertInstance in alerts | filter:alertSearch | orderBy:['alert_definition.priority', '-oldestStartTime'] track by $id(alertInstance.alert_definition.id)")).then(cb);
    });
};

exports.switchToCompactView = function(cb) {
    compact.click().then(function() {
        ptor.findElements(by.css('.compact')).then(cb);
    });
};

exports.openTags = function(cb) {
    tagsButton.click().then(function() {
        ptor.findElements(by.css('.dashboard-tags')).then(cb);
    });
};

exports.nameIsLink = function(cb) {
    ptor.findElements(by.css('a[href="#/alert-details/1"]')).then(cb);
};
