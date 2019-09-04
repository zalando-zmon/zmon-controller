angular.module('zmon2App').directive('checkRuntimeSelect', ['$q', 'CommunicationService', function($q, CommunicationService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/checkRuntimeSelect.html',
        scope: {
            name: '@',
            readOnly: '@',
            checkId: '=',
            runtime: '='
        },
        link: function (scope, elem, attrs) {
            // Set static defaults
            scope.name = scope.name || 'runtime';
            scope.readOnly = scope.readOnly || false;
            scope.warn = false;

            var generateContextFromConfig = function(configResource) {
                return {
                    enabled: configResource.enabled,
                    allRuntimes: configResource.runtime_labels,
                    defaultRuntime: configResource.default_runtime,
                    migrationGuideUrl: configResource.migration_guide_url
                };
            };

            var disableDirectiveOrContinue = function(context) {
                scope.enabled = context.enabled;
                if (!scope.enabled) return $q.reject();

                return context;
            };

            var setMigrationGuideUrl = function(context) {
                scope.migrationGuideUrl = context.migrationGuideUrl;

                return context;
            };

            var setInitialRuntime = function(context) {
                if (!scope.checkId) {
                    context.initialRuntime = context.defaultRuntime;
                    scope.runtime = _.clone(context.defaultRuntime);

                    return context;
                }

                return CommunicationService.getAllChanges({check_definition_id: scope.checkId, action: 'INSERT'})
                    .then(function(changesResources) {
                        context.initialRuntime = _.clone(_.get(changesResources, '0.attributes.cd_runtime', 'PYTHON_2'));

                        return context;
                    });
            };

            var setChoices = function(context) {
                if (context.initialRuntime === 'PYTHON_3') {
                    scope.choices = _.pick(context.allRuntimes, 'PYTHON_3');
                }
                else {
                    scope.choices = context.allRuntimes;
                }

                return context;
            };

            var setupRuntimeWatch = function() {
                scope.$watch('runtime', function(newValue) {
                    scope.warn = !scope.readOnly && (newValue === 'PYTHON_2');
                });
            };

            CommunicationService.getCheckRuntimeConfig()
                .then(generateContextFromConfig)
                .then(disableDirectiveOrContinue)
                .then(setMigrationGuideUrl)
                .then(setInitialRuntime)
                .then(setChoices)
                .then(setupRuntimeWatch);
        }
    }
}]);