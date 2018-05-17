angular.module('zmon2App').directive('entityFilterContainer', ['$compile', '$log', 'EntityFilterTypesService', 'FeedbackMessageService',
    function ($compile, $log, EntityFilterTypesService, FeedbackMessageService) {
        return {
            restrict: 'E',
            scope: {
                checkEntities: '=',
                formEntityFilters: '=',
                emptyJson: '=?',
                exclude: '=?'
            },
            templateUrl: 'templates/entityFilterContainer.html',
            link: function (scope, element, attrs, controller) {

                scope.inEditMode = false;
                scope.selectedType = 'GLOBAL';
                scope.globalIsUsed = false;
                scope.availableEntityFilterTypes = [ 'GLOBAL' ];
                scope.entityFilter = { type: scope.selectedType };

                // When an entity filter is missing a type, a default type can be taken from the Check
                var getTypeFromCheck = function() {
                    var type = '';
                    _.each(scope.checkEntities, function(checkEntity) {
                        if (checkEntity.type) {
                            type = checkEntity.type;
                        }
                    });
                    return type;
                };

                scope.addEntityFilter = function () {
                    if (scope.selectedType === 'GLOBAL') {
                        scope.entityFilter = { type: 'GLOBAL' };
                        return scope.formEntityFilters.push(scope.entityFilter);
                    }
                    scope.entityFilter = { type: scope.selectedType };
                    scope.inEditMode = true;
                };

                scope.editEntityFilter = function(i) {
                    scope.definitionInEditModeIndex = i;
                    scope.entityFilter = scope.formEntityFilters[i];

                    if (typeof scope.entityFilter.type === 'undefined') {
                        scope.entityFilter.type = getTypeFromCheck();
                    }

                    scope.selectedType = scope.entityFilter.type;
                    scope.inEditMode = true;
                };

                scope.removeEntityFilter = function (idx) {
                    scope.formEntityFilters.splice(idx, 1);
                };

                scope.$watch('formEntityFilters', function() {
                    // Remove GLOBAL type if used.
                    scope.globalIsUsed = _.map(scope.formEntityFilters, 'type').indexOf('GLOBAL') !== -1;
                    if (scope.globalIsUsed && scope.availableEntityFilterTypes[0] === 'GLOBAL') {
                        scope.availableEntityFilterTypes.splice(0, 1);
                        scope.selectedType = 'appdomain';
                        return;
                    }
                    if (!scope.globalIsUsed && scope.availableEntityFilterTypes.indexOf('GLOBAL') === -1) {
                        scope.availableEntityFilterTypes = [ 'GLOBAL' ].concat(scope.availableEntityFilterTypes);
                        scope.selectedType = 'GLOBAL';
                    }
                }, true);

                EntityFilterTypesService.getEntityTypeNames().then(function(names) {
                    console.log('got names', names);
                    scope.availableEntityFilterTypes = scope.availableEntityFilterTypes.concat(names);
                });
            }
        };
    }
]);
