var ptor = protractor.getInstance();

var search = element(by.css('[data-test=search]'));

exports.searchCheck = function(input, cb) {
    search.clear();
    search.sendKeys(input).then(function() {
        ptor.findElements(by.repeater("def in checkDefinitions| orderBy:'name' | filter:checkFilter")).then(cb);
    });
};

exports.filterTeam = function(cb) {
    var teamMenu = element(by.css('[data-test=teamMenu]'));
    teamMenu.click().then(function() {
        ptor.findElements(by.css('[data-test=team]')).then(cb);
    });
};
