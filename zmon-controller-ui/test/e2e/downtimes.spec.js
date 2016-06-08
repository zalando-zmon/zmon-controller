var downtimes = require('./behaviours/downtimes.behaviour');
var auth = require('./behaviours/auth.behaviour');

describe('Testing downtimes', function() {

    beforeEach(function() {
        browser.get('/#/alert-details/2');
    });

    it('User should be logged in', function() {
        auth.login(function(loggedIn) {
            expect(loggedIn).toBe(true);
        });
    });

    it('checking the header checkbox for "Schedule downtimes" should check checkboxes of all entities', function() {
        downtimes.checkAllScheduleDowntimesCheckboxes(function(checkedScheduleDowntimes) {
            expect(checkedScheduleDowntimes.count()).toBe(2);
        });

    });

    it('checking the header checkbox for "Schedule downtimes" & unchecking the first checkbox for "Schedule downtimes" should reduce total checked checkboxes by one and also uncheck header checkbox', function() {
        downtimes.uncheckFirstScheduleDowntimeCheckbox(function(checkedScheduleDowntimes, checkedheaderScheduleDowntimesCheckbox) {
            expect(checkedScheduleDowntimes.count()).toBe(1);
            expect(checkedheaderScheduleDowntimesCheckbox.count()).toBe(0);
        });
    });

    it('should select all alerts using header checkbox and open the downtimes modal when the flag icon is clicked', function() {
        downtimes.openSetDowntimesModal(function(modalDisplayed) {
            expect(modalDisplayed).toBe(true);
        });
    });
});

