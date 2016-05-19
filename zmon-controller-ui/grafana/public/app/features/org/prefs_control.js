///<reference path="../../headers/common.d.ts" />
System.register(['app/core/config', 'app/core/core_module'], function(exports_1) {
    var config_1, core_module_1;
    var PrefsControlCtrl, template;
    function prefsControlDirective() {
        return {
            restrict: 'E',
            controller: PrefsControlCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
            template: template,
            scope: {
                mode: "@"
            }
        };
    }
    exports_1("prefsControlDirective", prefsControlDirective);
    return {
        setters:[
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            PrefsControlCtrl = (function () {
                /** @ngInject **/
                function PrefsControlCtrl(backendSrv, $location) {
                    this.backendSrv = backendSrv;
                    this.$location = $location;
                    this.timezones = [
                        { value: '', text: 'Default' },
                        { value: 'browser', text: 'Local browser time' },
                        { value: 'utc', text: 'UTC' },
                    ];
                    this.themes = [
                        { value: '', text: 'Default' },
                        { value: 'dark', text: 'Dark' },
                        { value: 'light', text: 'Light' },
                    ];
                }
                PrefsControlCtrl.prototype.$onInit = function () {
                    var _this = this;
                    return this.backendSrv.get("/api/" + this.mode + "/preferences").then(function (prefs) {
                        _this.prefs = prefs;
                        _this.oldTheme = prefs.theme;
                    });
                };
                PrefsControlCtrl.prototype.updatePrefs = function () {
                    var _this = this;
                    if (!this.prefsForm.$valid) {
                        return;
                    }
                    var cmd = {
                        theme: this.prefs.theme,
                        timezone: this.prefs.timezone,
                        homeDashboardId: this.prefs.homeDashboardId
                    };
                    this.backendSrv.put("/api/" + this.mode + "/preferences", cmd).then(function () {
                        window.location.href = config_1.default.appSubUrl + _this.$location.path();
                    });
                };
                return PrefsControlCtrl;
            })();
            exports_1("PrefsControlCtrl", PrefsControlCtrl);
            template = "\n<form name=\"ctrl.prefsForm\" class=\"section gf-form-group\">\n  <h3 class=\"page-heading\">Preferences</h3>\n\n  <div class=\"gf-form\">\n    <span class=\"gf-form-label width-9\">UI Theme</span>\n    <div class=\"gf-form-select-wrapper max-width-20\">\n      <select class=\"gf-form-input\" ng-model=\"ctrl.prefs.theme\" ng-options=\"f.value as f.text for f in ctrl.themes\"></select>\n    </div>\n  </div>\n\n  <div class=\"gf-form\">\n    <span class=\"gf-form-label width-9\">Home Dashboard</span>\n    <dashboard-selector class=\"gf-form-select-wrapper max-width-20 gf-form-select-wrapper--has-help-icon\"\n                        model=\"ctrl.prefs.homeDashboardId\">\n    </dashboard-selector>\n  </div>\n\n  <div class=\"gf-form\">\n    <label class=\"gf-form-label width-9\">Timezone</label>\n    <div class=\"gf-form-select-wrapper max-width-20\">\n      <select class=\"gf-form-input\" ng-model=\"ctrl.prefs.timezone\" ng-options=\"f.value as f.text for f in ctrl.timezones\"></select>\n    </div>\n  </div>\n\n  <div class=\"gf-form-button-row\">\n    <button type=\"submit\" class=\"btn btn-success\" ng-click=\"ctrl.updatePrefs()\">Update</button>\n  </div>\n</form>\n";
            core_module_1.default.directive('prefsControl', prefsControlDirective);
        }
    }
});
//# sourceMappingURL=prefs_control.js.map