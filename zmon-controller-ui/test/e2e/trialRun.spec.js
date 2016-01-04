var TrialRun = require('./scenarios/trialRun.scenario');
var Auth = require('./scenarios/auth.scenario');

var trialRun = new TrialRun();
var auth = new Auth();

describe('Testing trial run feature', function() {

    beforeEach(function() {
        browser.get('#/trial-run');
    });

    it('User should be logged in', function() {
        auth.login(function(loggedIn) {
            expect(loggedIn).toBe(true);
        });
    });

    it('Run button should be visible', function() {
        expect(trialRun.runBtn.isDisplayed()).toBe(true);
    });

    it('Stop button should be hidden', function() {
        expect(trialRun.stopBtn.isDisplayed()).toBe(false);
    });

    it('Form panel should be displayed', function() {
        expect(trialRun.formPanel.isDisplayed()).toBe(true);
    });

    it('Results panel should be displayed', function() {
        expect(trialRun.resultPanel.isDisplayed()).toBe(true);
    });

    it('Form panel should be by default opened', function() {
        expect(trialRun.formPanel.getAttribute('class')).not.toMatch('panel-collapsed');
    });

    it('Results panel should be by default colapsed', function() {
        expect(trialRun.resultPanel.getAttribute('class')).toMatch('panel-collapsed');
    });

    it('Alert test should pass validation and must have some results', function() {
        trialRun.submitForm(function(alerts) {
            expect(alerts.length).toEqual(14);
        });
    });

    it('User should be logged out', function() {
        auth.logout(function(loggedOut) {});
    });

});
