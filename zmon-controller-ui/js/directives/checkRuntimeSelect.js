angular.module('zmon2App').directive('checkRuntimeSelect', ['$q', 'CommunicationService', function($q, CommunicationService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/checkRuntimeSelect.html',
        scope: {
            name: '@',
            readOnly: '@',
            mode: '=',
            runtime: '='
        },
        link: function (scope, elem, attrs) {
            // Set static defaults
            scope.name = scope.name || 'runtime';
            scope.readOnly = scope.readOnly || false;
            scope.mode = scope.mode || 'create';
            scope.warn = false;

            var generateContextFromConfig = function(configResource) {
                return {
                    enabled: configResource.enabled,
                    allRuntimes: configResource.runtime_labels,
                    defaultRuntime: configResource.default_runtime
                };
            };

            var disableDirectiveOrContinue = function(context) {
                scope.enabled = context.enabled;
                if (!scope.enabled) return $q.reject();

                return context;
            };

            var setInitialRuntime = function(context) {
                var defer = $q.defer();

                if (scope.mode === 'create') {
                    context.initialRuntime = context.defaultRuntime;
                    scope.runtime = _.clone(context.defaultRuntime);
                    defer.resolve(context);
                }
                else if (scope.mode === 'edit') {
                    var clearWatch = scope.$watch('runtime', function (newValue) {
                        if (!newValue) return;

                        context.initialRuntime = newValue;
                        defer.resolve(context);
                        clearWatch();
                    });
                }

                return defer.promise;
            };

            var setChoices = function(context) {
                if (context.initialRuntime === context.defaultRuntime) {
                    scope.choices = _.pick(context.allRuntimes, context.defaultRuntime);
                }
                else {
                    scope.choices = context.allRuntimes;
                }

                return context;
            };

            var setupRuntimeWatch = function(context) {
                scope.$watch('runtime', function(newValue) {
                    scope.warn = !scope.readOnly && (newValue !== context.defaultRuntime);
                });
            };

            CommunicationService.getCheckRuntimeConfig()
                .then(generateContextFromConfig)
                .then(disableDirectiveOrContinue)
                .then(setInitialRuntime)
                .then(setChoices)
                .then(setupRuntimeWatch);
        }
    }
}]);