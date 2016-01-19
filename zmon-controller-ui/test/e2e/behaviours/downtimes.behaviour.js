var headerScheduleDowntimesCheckbox = element(by.id('select-all-schedule-downtimes'));

var checkedheaderScheduleDowntimesCheckbox = element.all(by.id('.select-all-schedule-downtimes:checked'));

var checkedScheduleDowntimes = element.all(by.css('.set-downtime-checkbox:checked'));

var setDowntimesButton = element(by.id('set-downtimes-button'));

var setDowntimesModal = element(by.id('set-downtimes-modal'));

var setDowntimeModalByDurationHourInput = element(by.model('hours'));

var setDowntimeModalByDurationReasonTextarea = element(by.model('models.downtimeComment'));

var setDowntimeModalOKButton = element(by.css('.set-downtimes-ok-button'));

var downtimesTabHeader = element(by.css('li#downtimes-tab > a'));

var headerDeleteDowntimesCheckbox = element(by.id('select-all-delete-downtimes'));

var deleteDowntimesButton = element(by.id('delete-downtimes-button'));

exports.checkAllScheduleDowntimesCheckboxes = function(cb) {
    headerScheduleDowntimesCheckbox.click().then(function() {
        cb(checkedScheduleDowntimes);
    });
};

exports.uncheckFirstScheduleDowntimeCheckbox = function(cb) {
    headerScheduleDowntimesCheckbox.click().then(function() {
        checkedScheduleDowntimes.get(0).click().then(function() {
            cb(checkedScheduleDowntimes, checkedheaderScheduleDowntimesCheckbox);
        });
    });
};

exports.openSetDowntimesModal = function(cb) {
    headerScheduleDowntimesCheckbox.click().then(function() {
        setDowntimesButton.click().then(function() {
            browser.driver.sleep(1000);
            cb(setDowntimesModal.isDisplayed());
        });
    });
};

exports.create1hDowntime = function(cb) {
    headerScheduleDowntimesCheckbox.click().then(function() {
        setDowntimesButton.click().then(function() {
            setDowntimeModalByDurationHourInput.clear();
            setDowntimeModalByDurationHourInput.sendKeys('1');
            setDowntimeModalByDurationReasonTextarea.sendKeys('reason for 1h downtime');
            setDowntimeModalOKButton.click().then(function() {
                var ready = false;
                browser.wait(function() {
                    downtimesTabHeader.getText().then(function(text) {
                        if (text === 'Downtimes (1)') {
                            ready = true;
                        }
                    });
                    return ready;
                }, 20000);
                cb(ready);
            });
        });
    });
};

exports.deleteAllDowntimes = function(cb) {
    downtimesTabHeader.click().then(function() {
        headerDeleteDowntimesCheckbox.click().then(function() {
            deleteDowntimesButton.click().then(function() {
                var ready = false;
                browser.wait(function() {
                    downtimesTabHeader.getText().then(function(text) {
                        if (text === 'Downtimes (0)') {
                            ready = true;
                        }
                    });
                    return ready;
                }, 20000);
                cb(ready);
            });
        });
    });
};
