var Scenario = require('./scenarios/dashboard.scenario');
var dashboard = new Scenario();

describe('Testing dashboard features', function() {

    beforeEach(function() {
        browser.get('/#/dashboards/view/19');
        browser.ignoreSynchronization = true;
    });

    afterEach(function() {
        browser.ignoreSynchronization = false;
    });
    it('should display the alerts container', function() {
        expect(dashboard.alertsContainer.isDisplayed()).toBe(true);
    });

    it('should display the search form', function() {
        expect(dashboard.searchForm.isDisplayed()).toBe(true);
    });

    it('alert listing should contain the "City Longitude" alert', function() {
        browser.ignoreSynchronization = false;
        expect(dashboard.alertsContainer.getText()).toMatch(/City Longitude >0/);
    });

    it('should show only one alert when typing "city longitude" in search form', function() {
        dashboard.doCityLongitudeSearch(function(alertsArray) {
            expect(alertsArray.length).toEqual(1);
        });
    });

    it('should display a "Hide Widgets" button', function() {
        expect(dashboard.widgetsButton.isDisplayed()).toBe(true);
        expect(dashboard.widgetsButton.getText()).toBe("Hide Widgets");
    });

    it('clicking the widgets button should toggle widgets panel', function() {

        // Note: the widgets panel is always displayed; to hide it we just place it outside the page's margins
        expect(browser.isElementPresent(dashboard.widgetsPanelLocator)).not.toBe(true);

        dashboard.widgetsButton.click();
        expect(browser.isElementPresent(dashboard.widgetsPanelLocator)).toBe(true);
    });

    it('typing "city longitude" in search form it should show 1 alert with a "More..." link which clicked should change to "Hide..." link and display rest of cities (in total 12) with the first being jp-tokyo and last ru-moscow', function() {
        dashboard.doCityLongitudeSearch(function(alertsArray) {
            expect(alertsArray.length).toEqual(1);
            var moreCitiesLink = alertsArray[0].findElement(by.css('.non-href-anchor'));
            expect(moreCitiesLink.isDisplayed()).toBe(true);
            expect(moreCitiesLink.getText()).toBe('More...');
            moreCitiesLink.click();
            var hideCitiesLink = alertsArray[0].findElement(by.css('.non-href-anchor'));
            expect(hideCitiesLink.isDisplayed()).toBe(true);
            expect(hideCitiesLink.getText()).toBe('Hide...');
            dashboard.confirmRestCitiesList(
                function(restOfCitiesArray) {
                    expect(restOfCitiesArray.length).toEqual(11);
                    expect(restOfCitiesArray[0].getText()).toEqual('kr-seoul (126.9783)');
                    expect(restOfCitiesArray[restOfCitiesArray.length - 1].getText()).toEqual('ru-moscow (37.615556)');
                }
            );
        });
    });
});
