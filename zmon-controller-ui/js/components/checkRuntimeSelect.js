angular.module('zmon2App').component('checkRuntimeSelect', {
    bindings: {
        name: '<',
        allowedChoices: '@',
        default: '@',
        onUpdate: '&'
    },
    templateUrl: 'templates/checkRuntimeSelect.html',
    controller: function (CommunicationService) {
        var ctrl = this;

        ctrl.$onInit = function() {
            ctrl.name = ctrl.name || 'runtime';

            CommunicationService.getCheckRuntimeConfig()
                .then(function (response) {
                    ctrl.enabled = response.enabled;
                    if (!ctrl.enabled) {
                        return;
                    }

                    ctrl.choices = {
                        create: response.allowed_runtimes_for_create,
                        update: response.allowed_runtimes_for_update
                    }[ctrl.allowedChoices];
                    ctrl.choice = _.find(ctrl.choices, function (choice) {
                        return choice.name === ctrl.default;
                    }) || response.default_runtime;

                    ctrl.onSelect();
                });
        };

        ctrl.onSelect = function() {
            ctrl.onUpdate({runtime: ctrl.choice.name});
        }
    }
});