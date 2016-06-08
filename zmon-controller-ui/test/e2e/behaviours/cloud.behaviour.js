exports.getTeamCards = function(cb) {
    browser.findElements(by.css('.card')).then(cb);
};
