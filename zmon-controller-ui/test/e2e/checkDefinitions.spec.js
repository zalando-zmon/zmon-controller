var checkDefinitions = require('./behaviours/checkDefinitions.behaviour');
var auth = require('./behaviours/auth.behaviour');

describe('Testing check definitions page', function() {

    beforeEach(function() {
        browser.get('/#/check-definitions');
    });

    it('User should be logged in', function() {
        auth.login(function(loggedIn) {
            expect(loggedIn).toBe(true);
        });
    });

    it('should show only two checks', function() {
        checkDefinitions.searchCheck('Random', function(alerts) {
            expect(alerts.length).toBe(2);
        });
    });

    it('should filter out all checks', function() {
        checkDefinitions.searchCheck('NonexsistentCheck', function(checks) {
            expect(checks.length).toBe(0);
        });
    });

    it('should show only Example Team on the Team Filter menu', function() {
        checkDefinitions.filterTeam(function(teams) {
            expect(teams.length).toBe(1);
        });
    });

});
