angular.module('zmon2App').component('checkRuntimeSelect', {
    bindings: {
        name: '@',
        allowedChoices: '@',
        readOnly: '@',
        default: '<',
        onUpdate: '&'
    },
    templateUrl: 'templates/checkRuntimeSelect.html',
    controller: function ($q, CommunicationService) {
        var ctrl = this;
        var initPromise;

        var setDefaultChoice = function() {
            initPromise.then(function (config) {
                ctrl.choice = _.find(ctrl.choices || [], function (choice) {
                    return choice.name === ctrl.default;
                }) || config.default_runtime;
            });
        };

        ctrl.$onInit = function() {
            initPromise = CommunicationService.getCheckRuntimeConfig().then(function(config) {
                ctrl.enabled = config.enabled;
                if (!ctrl.enabled) {
                    return;
                }

                ctrl.name = ctrl.name || 'runtime';
                ctrl.readOnly = ctrl.readOnly || false;
                ctrl.choices = {
                    create: config.allowed_runtimes_for_create,
                    update: config.allowed_runtimes_for_update
                }[ctrl.allowedChoices];

                return config;
            });
            setDefaultChoice();
        };

        ctrl.$onChanges = function(changes) {
            if (changes.default && !changes.default.isFirstChange()) {
                setDefaultChoice();
            }
        };

        ctrl.onSelect = function() {
            ctrl.onUpdate({$event: {runtime: ctrl.choice.name}});
        };
    }
});