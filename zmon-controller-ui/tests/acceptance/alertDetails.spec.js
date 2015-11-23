var alertDetails = require('./behaviours/alertDetails.behaviour');
var auth = require('./behaviours/auth.behaviour');

describe('Testing alert details page', function() {

    beforeEach(function() {
        browser.get('/#/alert-details/1');
    });

    it('User should be logged in', function() {
        auth.login(function(loggedIn) {
            expect(loggedIn).toBe(true);
        });
    });

    it('should show only one alert', function() {
        alertDetails.searchAlert('GLOBAL', function(alerts) {
            expect(alerts.length).toBe(1);
        });
    });

    it('should filter out all alerts', function() {
        alertDetails.searchAlert('NonexsistentAlert', function(alerts) {
            expect(alerts.length).toBe(0);
        });
    });

    it('should open the details panel', function() {
        alertDetails.openDetails(function(collapsedElements) {
            expect(collapsedElements.length).toBe(0);
        });
    });

    /* FIXME
    it('should open history tab', function() {
        alertDetails.openHistoryTab(function(elements) {
            expect(elements.length).toBe(1);
        });
    });
    */

    it('should show all alert details', function() {
        alertDetails.checkDetails(function(details) {
            expect(details.getText()).toMatch(/Random, ID: 1/);
            expect(details.getText()).toMatch(/Example Team/);
            expect(details.getText()).toMatch(/10s/);
            expect(details.getText()).toMatch(/type = GLOBAL/);
        });
    });

});
