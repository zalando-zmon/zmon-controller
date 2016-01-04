var TrialRun = function() {};

TrialRun.prototype.runBtn = element(by.id('run-button'));
TrialRun.prototype.stopBtn = element(by.id('stop-button'));
TrialRun.prototype.formPanel = element(by.className('form-panel'));
TrialRun.prototype.resultPanel = element(by.className('result-panel'));

TrialRun.prototype.submitForm = function(cb) {

    var iName = $('#inp-name');
    var iCommand = $('#inp-command textarea.ace_text-input');
    var iCondition = $('#inp-condition textarea.ace_text-input');
    var iEntities = $('#inp-entities textarea.ace_text-input');
    var iEntitiesExclude = $('#inp-entities-exclude textarea.ace_text-input');
    var aceCommand = $('#inp-command div.ace_content');
    var aceCondition = $('#inp-condition div.ace_content');
    var aceEntities = $('#inp-entities div.ace_content');
    var aceEntitiesExclude = $('#inp-entities-exclude div.ace_content');

    iName.sendKeys('Trial run');

    browser.actions().doubleClick(aceCommand).perform();
    iCommand.sendKeys('float(entity[\'longitude\'])');

    browser.actions().doubleClick(aceCondition).perform();
    iCondition.sendKeys('float(value)>5');

    browser.actions().doubleClick(aceEntities).perform();
    iEntities
        // couldn't find a better way to clear the textarea (clear() doesn't work).
        .sendKeys(protractor.Key.BACK_SPACE)
        .sendKeys(protractor.Key.BACK_SPACE)
        .sendKeys('[{"type":"city"}]');

    this.runBtn.click().then(function() {
        progress = false;
        var checkProgress = function() {
            element(by.className('progress-bar')).getAttribute('aria-valuenow').then(function(value) {
                if (!progress) {
                    progress = (parseInt(value, 10) === 100);
                }
            });
        };
        browser.wait(function() {
            checkProgress();
            return progress;
        }, 60000);

        browser.findElements(by.repeater("alert in TrialRunCtrl.alerts | filter:{is_alert:true} | filter:search | orderBy:'entity.id'").column('{{alert.entity.id}}')).then(cb);
    });
};

module.exports = TrialRun;
