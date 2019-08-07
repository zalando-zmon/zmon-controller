angular.module('zmon2App').component('checkRuntimeSelect', {
    bindings: {
        name: '@',
        allowedChoices: '@',
        readOnly: '@',
        default: '<',
        onUpdate: '&'
    },
    templateUrl: 'js/components/check-runtime-select/check-runtime-select.template.html',
    controller: function ($q, CommunicationService) {
        var ctrl = this;
        var initPromise;

        var setDefaultChoice = function(config) {
            ctrl.choice = _.find(ctrl.choices || [], function (choice) {
                return choice.name === ctrl.default;
            }) || config.default_runtime;
            ctrl.onSelect();
        };

        ctrl.$onInit = function() {
            initPromise = CommunicationService.getCheckRuntimeConfig().then(function(config) {
                ctrl.enabled = config.enabled;
                if (!ctrl.enabled) {
                    return $q.reject();
                }

                ctrl.name = ctrl.name || 'runtime';
                ctrl.readOnly = ctrl.readOnly || false;
                ctrl.choices = {
                    create: config.allowed_runtimes_for_create,
                    update: config.allowed_runtimes_for_update
                }[ctrl.allowedChoices];

                setDefaultChoice(config);

                return config;
            });
        };

        ctrl.$onChanges = function(changes) {
            if (changes.default && !changes.default.isFirstChange()) {
                initPromise.then(setDefaultChoice);
            }
        };

        ctrl.onSelect = function() {
            ctrl.onUpdate({$event: {runtime: ctrl.choice.name}});
        };
    }
});