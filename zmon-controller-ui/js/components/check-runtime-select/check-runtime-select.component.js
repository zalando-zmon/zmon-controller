angular.module('zmon2App').component('checkRuntimeSelect', {
    bindings: {
        name: '@',
        readOnly: '@',
        runtime: '='
    },
    templateUrl: 'js/components/check-runtime-select/check-runtime-select.template.html',
    controller: function ($q, CommunicationService) {
        var ctrl = this;
        var init;
        var initialRuntime;

        init = CommunicationService.getCheckRuntimeConfig().then(function(config) {
            ctrl.enabled = config.enabled;
            if (!ctrl.enabled) {
                return $q.reject();
            }

            ctrl.name = ctrl.name || 'runtime';
            ctrl.readOnly = ctrl.readOnly || false;
            ctrl.choices = config.runtime_labels;
        });

        ctrl.onChange = function() {
            console.log('Changed');
        };

        // ctrl.$doCheck = function() {
        //     console.log('Run do check');
        //     if (initialRuntime) {
        //         return;
        //     }
        //
        //     initialRuntime = _.copy(ctrl.runtime);
        //     console.log('Initial runtime was: ' + initialRuntime);
        // };
        // var ctrl = this;
        // var initPromise;

        // var setDefaultChoice = function(config) {
        //     var newValue = _.clone(ctrl.default) || config.default_runtime;
        //     ctrl.choice = newValue;
        //     if (newValue === config.default_runtime) {
        //         ctrl.choices = _.pick(ctrl.choices, config.default_runtime);
        //     }
        //     console.log('Choice is now: ' + ctrl.choice);
        //
        //     ctrl.doUpdate();
        // };
        //
        // ctrl.$onInit = function() {
        //     initPromise = CommunicationService.getCheckRuntimeConfig().then(function(config) {
        //         ctrl.enabled = config.enabled;
        //         if (!ctrl.enabled) {
        //             return $q.reject();
        //         }
        //
        //         ctrl.name = ctrl.name || 'runtime';
        //         ctrl.readOnly = ctrl.readOnly || false;
        //         ctrl.warn = false;
        //         ctrl.choices = config.runtime_labels;
        //         setDefaultChoice(config);
        //
        //         return config;
        //     });
        // };
    }
});