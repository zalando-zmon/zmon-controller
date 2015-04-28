var alertDefinitions = require('./behaviours/alertDefinitions.behaviour');
var auth = require('./behaviours/auth.behaviour');

describe('Testing alert definitions page', function() {

    beforeEach(function() {
        browser.get('/#/alert-definitions');
    });

    it('User should be logged in', function() {
        auth.login(function(loggedIn) {
            expect(loggedIn).toBe(true);
        });
    });

    it('should show only one alert', function() {
        alertDefinitions.searchAlert('Example Alert', function(alerts) {
            expect(alerts.length).toBe(1);
        });
    });

    it('should filter out all alerts', function() {
        alertDefinitions.searchAlert('NonexsistentAlert', function(checks) {
            expect(checks.length).toBe(0);
        });
    });

    it('should show only Example Team on the Team Filter menu', function() {
        alertDefinitions.filterTeam(function(teams) {
            expect(teams.length).toBe(1);
        });
    });

});
