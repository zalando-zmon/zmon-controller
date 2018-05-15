///<reference path="../../../headers/common.d.ts" />
System.register(['app/core/core_module', 'app/core/app_events'], function(exports_1) {
    var core_module_1, app_events_1;
    var WizardSrv, SelectOptionStep, WizardFlow;
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            },
            function (app_events_1_1) {
                app_events_1 = app_events_1_1;
            }],
        execute: function() {
            WizardSrv = (function () {
                /** @ngInject */
                function WizardSrv() {
                }
                return WizardSrv;
            })();
            exports_1("WizardSrv", WizardSrv);
            SelectOptionStep = (function () {
                function SelectOptionStep() {
                    this.type = 'select';
                }
                SelectOptionStep.prototype.process = function () {
                    return new Promise(function (fulfill, reject) {
                    });
                };
                return SelectOptionStep;
            })();
            exports_1("SelectOptionStep", SelectOptionStep);
            WizardFlow = (function () {
                function WizardFlow(name) {
                    this.name = name;
                    this.steps = [];
                }
                WizardFlow.prototype.addStep = function (step) {
                    this.steps.push(step);
                };
                WizardFlow.prototype.next = function (index) {
                    var _this = this;
                    var step = this.steps[0];
                    return step.process().then(function () {
                        if (_this.steps.length === index + 1) {
                            return;
                        }
                        return _this.next(index + 1);
                    });
                };
                WizardFlow.prototype.start = function () {
                    app_events_1.default.emit('show-modal', {
                        src: 'public/app/core/components/wizard/wizard.html',
                        model: this
                    });
                    return this.next(0);
                };
                return WizardFlow;
            })();
            exports_1("WizardFlow", WizardFlow);
            core_module_1.default.service('wizardSrv', WizardSrv);
        }
    }
});
//# sourceMappingURL=wizard.js.map