var cloud = require('./behaviours/cloud.behaviour');
var auth = require('./behaviours/auth.behaviour');

describe('Testing Cloud page', function() {

    beforeEach(function() {
        browser.get('/#/cloud');
    });

    it('User should be logged in', function() {
        auth.login(function(loggedIn) {
            expect(loggedIn).toBe(true);
        });
    });

    it('should show one team card', function() {
        expect(teamCards.length).toBe(1);
    });
});
