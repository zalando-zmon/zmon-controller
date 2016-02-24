angular.module('zmon2App').controller('CheckDefinitionEditCtrl', ['$scope', '$routeParams', '$location', 'MainAlertService', 'CommunicationService', 'FeedbackMessageService', 'UserInfoService', 'APP_CONST',
    function($scope, $routeParams, $location, MainAlertService, CommunicationService, FeedbackMessageService, UserInfoService, APP_CONST) {

        MainAlertService.removeDataRefresh();

        $scope.$parent.activePage = 'check-definitions';
        $scope.invalidFormat = false;
        $scope.entityFilter = {};
        $scope.entityExcludeFilter = {};
        $scope.checkParameters = [];
        $scope.paramTypes = ['string', 'int', 'boolean'];
        $scope.allTags = [];
        $scope.defaultEntitiesFilter = [];
        $scope.defaultEntitiesExcludeFilter = [];
        $scope.defaultNotifications = [];

        // for route '/check-definitions/edit/:checkId' [edit existing check]
        $scope.checkId = $routeParams.checkId;

        $scope.check = {};

        $scope.mode = 'edit';

        $scope.save = function() {
            if ($scope.cdForm.$valid) {
                // SAVE
            }
        };

        $scope.cancel = function() {
            $scope.cdForm.submitted = false;
            if ($scope.mode === 'edit') {
                $location.path('/check-definitions/view/' + scope.checkId);
            } else if ($scope.mode === 'clone') {
                $location.path('/check-definitions/view/' + scope.cloneFromCheckDefinitionId);
            } else {
                $location.path('/check-definitions/');
            }
        };

        // Get a check definition from the backend
        var getCheckDefinition = function() {
            CommunicationService.getCheckDefinition($scope.checkId).then(
                function(response) {
                    $scope.check = response;
                }
            );
        };

        if ($scope.checkId) {
            getCheckDefinition($scope.checkId);
        };


        // from TrialRun
        $scope.paramTypes = ['string', 'int', 'boolean', 'date'];
        $scope.parameterTypeOptions = [
            {
                'value': 'str',
                'label': 'String'
            },
            {   'value': 'int',
                'label': 'Integer'
            },
            {   'value': 'float',
                'label': 'Float'
            },
            {
                'value': 'bool',
                'label': 'Boolean'
            },
            {
                'value': 'date',
                'label': 'Date'
            }
        ];

        $scope.parameterTypeBooleanOptions = [
            {
                'value': true,
                'label': 'True'
            },
            {
                'value': false,
                'label': 'False'
            }
        ];

        // Entities filter data
        $scope.entityFilter = {
            formEntityFilters: [],
            textEntityFilters: []
        };

        $scope.entityExcludeFilter = {
            formEntityFilters: [],
            textEntityFilters: []
        };

        // Entity filter types initialized by default with GLOBAL (which is not provided by backend as separate type) and the rest comes from backend
        $scope.entityFilter.types = [
            {
                "type": "GLOBAL"
            }
        ];

        $scope.entityExcludeFilter.types = [
            {
                "type": "GLOBAL"
            }
        ];

        // Flag to toggle UI on whether user types JSON text or uses form to define the entity filters
        $scope.entityFilterInputMethod = 'text';
        $scope.entityExcludeFilterInputMethod = 'text';

        $scope.INDENT = '    ';

        // Add a new parameter with cleared values and type string by default
        $scope.addParameter = function() {
            $scope.checkParameters.push({type: 'str'});
        };

        // Remove a parameter from the parameters json object
        $scope.removeParameter = function(name) {
            var index = null;
            _.each($scope.checkParameters, function(param, i) {
                if (param.name === name) {
                    index = i;
                };
            });
            if (index != null) {
                $scope.checkParameters.splice(index, 1);
            }
        };

        // Get current parameters in form and generate a properly formatted
        // json to send to the backend.
        var formParametersObject = function() {
            var parameters = {};
            _.each($scope.oParams, function(p) {
                _.each($scope.checkParameters, function(param) {
                    if (param.name === p) {
                        var val = param.value;

                        if (param.type === 'int') {
                            val = parseInt(param.value);
                        }
                        if (param.type === 'float') {
                            val = parseFloat(param.value);
                        }

                        parameters[param.name] = {
                            "value": val,
                            "comment": param.comment,
                            "type": param.type
                        }
                    }
                });
            });
            return parameters;
        };

        CommunicationService.getEntityProperties().then(
            function (data) {
                for (var p in data) {
                    if (data.hasOwnProperty(p)) {
                        var nextFilterType = {};
                        nextFilterType.type = p;
                        angular.extend(nextFilterType, data[p]);
                        $scope.entityFilter.types.push(nextFilterType);
                        $scope.entityExcludeFilter.types.push(nextFilterType);
                    }
                }

                // Sort entity filter types.
                $scope.entityFilter.types = _.sortBy($scope.entityFilter.types, "type");
                $scope.entityExcludeFilter.types = _.sortBy($scope.entityExcludeFilter.types, "type");
            }
        );

        $scope.$watch('entityFilter.formEntityFilters', function (newVal, oldVal) {
            if ($scope.entityFilterInputMethod === 'form') {
                // Process a copy so we safely remove $$hashKey property which we don't want to be transfered to entityFilter.textEntityFilters
                var formEntityFiltersClone = angular.copy($scope.entityFilter.formEntityFilters);
                for (var p in formEntityFiltersClone) {
                    if (formEntityFiltersClone.hasOwnProperty(p) && p === '$$hashKey') {
                        delete formEntityFiltersClone[p];
                    }
                }
                $scope.entityFilter.textEntityFilters = JSON.stringify(formEntityFiltersClone, null, $scope.INDENT);
                $scope.check.entities = angular.copy(formEntityFiltersClone);
            }
        }, true);

        // Same as above, for excluded entities.
        $scope.$watch('entityExcludeFilter.formEntityFilters', function (newVal, oldVal) {
            if ($scope.entityExcludeFilterInputMethod === 'form') {
                // Process a copy so we safely remove $$hashKey property which we don't want to be transfered to entityExcludeFilter.textEntityFilters
                var formEntityFiltersClone = angular.copy($scope.entityExcludeFilter.formEntityFilters);
                for (var p in formEntityFiltersClone) {
                    if (formEntityFiltersClone.hasOwnProperty(p) && p === '$$hashKey') {
                        delete formEntityFiltersClone[p];
                    }
                }
                $scope.entityExcludeFilter.textEntityFilters = JSON.stringify(formEntityFiltersClone, null, $scope.INDENT);
                $scope.check.entities_exclude = angular.copy(formEntityFiltersClone);
            }
        }, true);

        // If entity filter input method is 'text', reflect changes of entityFilter.textEntityFilters on entityFilter.formEntityFilters
        $scope.$watch('entityFilter.textEntityFilters', function (newVal, oldVal) {
            if ($scope.entityFilterInputMethod === 'text') {
                try {
                    $scope.check.entities = JSON.parse($scope.entityFilter.textEntityFilters);
                    $scope.entityFilter.formEntityFilters = JSON.parse($scope.entityFilter.textEntityFilters);
                    $scope.invalidFormat = false;
                } catch (ex) {
                    $scope.invalidFormat = true;
                }
            }
        }, true);

        // Same as above, for excluded entities.
        $scope.$watch('entityExcludeFilter.textEntityFilters', function (newVal, oldVal) {
            if ($scope.entityExcludeFilterInputMethod === 'text') {
                try {
                    $scope.check.entities_exclude = JSON.parse($scope.entityExcludeFilter.textEntityFilters);
                    $scope.entityExcludeFilter.formEntityFilters = JSON.parse($scope.entityExcludeFilter.textEntityFilters);
                    $scope.invalidFormat = false;
                } catch (ex) {
                    $scope.invalidFormat = true;
                }
            }
        }, true);

       $scope.$watch('parameters', function() {
           $scope.check.parameters = formParametersObject($scope.parameters);
       }, true);
    }
]);

