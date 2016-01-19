var dashboard = require('./behaviours/dashboard.behaviour');
var auth = require('./behaviours/auth.behaviour');

describe('Testing dashboard features', function() {

    beforeEach(function() {
        browser.get('#/dashboards/view/1');
    });

    it('User should be logged in', function() {
        auth.login(function(loggedIn) {
            expect(loggedIn).toBe(true);
        });
    });

    it('should show only one alert', function() {
        dashboard.searchAlert('Example Alert', function(alerts) {
            expect(alerts.length).toBe(1);
        });
    });

    it('should show alert name as link to alert details', function() {
        dashboard.nameIsLink(function(elements) {
            expect(elements.length).toBe(1);
        });
    });

    it('should switch to compact view', function() {
        dashboard.switchToCompactView(function(elements) {
            expect(elements.length).toBe(1);
        });
    });

    it('should open tags popup', function() {
        dashboard.openTags(function(elements) {
            expect(elements.length).toBe(1);
        });
    });

    it('should filter out all alerts', function() {
        dashboard.searchAlert('NonexistingAlert', function(alerts) {
            expect(alerts.length).toBe(0);
        });
    });
});
