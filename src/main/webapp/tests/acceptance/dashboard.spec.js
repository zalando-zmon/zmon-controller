var ptor = protractor.getInstance();

var dashboard = require('./behaviours/dashboard.behaviour');

describe('Testing dashboard features', function() {

    beforeEach(function() {
        browser.get('#/dashboards/view/1');
    });

    it('should display the search form', function() {
        expect(dashboard.searchForm.isDisplayed()).toBe(true);
    });

});
