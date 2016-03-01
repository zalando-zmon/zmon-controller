angular.module('zmon2App').controller('CheckDefinitionEditCtrl', ['$scope', '$routeParams', '$location', 'MainAlertService', 'CommunicationService', 'FeedbackMessageService', 'UserInfoService', 'APP_CONST',
    function($scope, $routeParams, $location, MainAlertService, CommunicationService, FeedbackMessageService, UserInfoService, APP_CONST) {

        MainAlertService.removeDataRefresh();

        $scope.$parent.activePage = 'check-definitions';
        $scope.invalidFormat = false;
        $scope.parameters = [];
        $scope.paramTypes = ['string', 'int', 'boolean'];
        $scope.allTags = [];
        $scope.defaultEntitiesFilter = [];
        $scope.defaultEntitiesExcludeFilter = [];
        $scope.defaultNotifications = [];

        $scope.entityFilterInputMethod = 'text';
        $scope.entityExcludeFilterInputMethod = 'text';

        // Keep account of overwritten Properties and Parameters on inherit mode
        $scope.oProps = [];
        $scope.oParams = [];

        $scope.entityFilter = {
            "types":
            [{
                "type": "GLOBAL"
            }],
            formEntityFilters: [],
            textEntityFilters: '[]'
        };

        $scope.entityExcludeFilter = {
            "types":
            [{
                "type": "GLOBAL"
            }],
            formEntityFilters: [],
            textEntityFilters: '[]'
        };

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

        $scope.INDENT = '    ';

        // for route '/check-definitions/edit/:checkId' [edit existing check]
        $scope.checkId = $routeParams.checkId;

        $scope.check = {};

        $scope.mode = 'edit';

        $scope.focusedElement = null;

        $scope.save = function() {
            if ($scope.cdForm.$valid) {
                try {

                    if ($scope.entityFilter.textEntityFilters === '') {
                        delete $scope.check.entities;
                    } else {
                        $scope.check.entities = JSON.parse($scope.entityFilter.textEntityFilters);
                    }

                    if ($scope.entityExcludeFilter.textEntityFilters === '') {
                        delete $scope.check.entities_exclude;
                    } else {
                        $scope.check.entities_exclude = JSON.parse($scope.entityExcludeFilter.textEntityFilters);
                    }

                    if ($scope.oParams.length === 0) {
                        delete $scope.check.parameters;
                    } else {
                        $scope.check.parameters = $scope.formParametersObject();
                    }

                    if (typeof $scope.check.period === 'undefined') {
                        $scope.check.period = "";
                    }

                    CommunicationService.updateCheckDefinition($scope.check).then(function(data) {
                        FeedbackMessageService.showSuccessMessage('Saved successfully; redirecting...', 500, function() {
                            $location.path('/check-details/' + data.id);
                        });
                    });
                } catch (ex) {
                    $scope.invalidFormat = true;
                    return FeedbackMessageService.showErrorMessage('JSON format is incorrect' + ex);
                }
            } else {
                $scope.cdForm.submitted = true;
                $scope.focusedElement = null;
            }
        };

        $scope.cancel = function() {
            $scope.cdForm.submitted = false;
            if ($scope.mode === 'edit') {
                $location.path('/check-definitions/view/' + $scope.checkId);
            } else if ($scope.mode === 'clone') {
                $location.path('/check-definitions/view/' + $scope.cloneFromCheckDefinitionId);
            } else {
                $location.path('/check-definitions/');
            }
        };

        // Get a check definition from the backend
        var getCheckDefinition = function() {
            CommunicationService.getCheckDefinition($scope.checkId).then(
                function(response) {
                    $scope.check = response;

                    $scope.parameters = [];
                    _.each(_.keys(response.parameters).sort(), function(name) {
                        $scope.oParams.push(name);
                        $scope.parameters.push(_.extend({'name': name}, response.parameters[name]));
                    });

                    $scope.entityFilter.formEntityFilters = response.entities || [];
                    $scope.entityFilter.textEntityFilters = JSON.stringify(response.entities, null, $scope.INDENT) || '[]';
                    $scope.entityExcludeFilter.formEntityFilters = response.entities_exclude || [];
                    $scope.entityExcludeFilter.textEntityFilters = JSON.stringify(response.entities_exclude, null, $scope.INDENT) || '[]';
                }
            );
        };

        if ($scope.checkId) {
            getCheckDefinition($scope.checkId);
        };


        // Add a new parameter with cleared values and type string by default
        $scope.addParameter = function() {
            $scope.parameters.push({type: 'str'});
        };

        // Remove a parameter from the parameters json object
        $scope.removeParameter = function(name) {
            var index = null;
            _.each($scope.parameters, function(param, i) {
                if (param.name === name) {
                    index = i;
                };
            });
            if (index != null) {
                $scope.parameters.splice(index, 1);
            }
        };

        // Get current parameters in form and generate a properly formatted
        // json to send to the backend.
        function formParametersObject() {
            var parameters = {};
            _.each($scope.parameters, function(param) {
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
            });
            return parameters;
        };

        // Validate a parameter's name to be a valid python variable name
        $scope.paramNameIsValid = function(name) {
            var re = /^[_a-zA-Z][_a-zA-Z0-9]*/;
            return re.test(name);
        };

        $scope.dateOptions = {
            formatYear: 'yy',
            startingDay: 1
        };

        $scope.format = 'dd.MM.yyyy';

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

