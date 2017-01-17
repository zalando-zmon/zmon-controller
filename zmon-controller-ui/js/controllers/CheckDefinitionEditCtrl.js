angular.module('zmon2App').controller('CheckDefinitionEditCtrl', ['$scope', '$routeParams', '$location', 'MainAlertService', 'CommunicationService', 'FeedbackMessageService', 'UserInfoService', 'APP_CONST',
    function($scope, $routeParams, $location, MainAlertService, CommunicationService, FeedbackMessageService, UserInfoService, APP_CONST) {

        MainAlertService.removeDataRefresh();

        $scope.$parent.activePage = 'check-definitions';
        $scope.invalidFormat = false;
        $scope.allTags = [];
        $scope.defaultEntitiesFilter = [];
        $scope.entityFilterInputMethod = 'text';
        $scope.matchedEntitiesCount = null;
        $scope.matchedEntities = [];

        // Filter object for Matched Entities
        $scope.filter = {
            "include_filters": [[],[]],
            "exclude_filters": [[]]
        }

        var user = UserInfoService.get();
        $scope.teams = user.teams !== "" ? user.teams.split(',') : [];

        $scope.entityFilter = {
            "types":
            [{
                "type": "GLOBAL"
            }],
            formEntityFilters: [],
            textEntityFilters: '[]'
        };

        $scope.statusOptions = [
            {
                'value': 'ACTIVE',
                'label': 'ACTIVE'
            },
            {
                'value': 'INACTIVE',
                'label': 'INACTIVE'
            },
            {
                'value': 'DELETED',
                'label': 'DELETED'
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

                    CommunicationService.updateCheckDefinition($scope.check).then(function(data) {
                        FeedbackMessageService.showSuccessMessage('Saved successfully; redirecting...', 500, function() {
                            $location.path('/check-definitions/view/' + data.id);
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

        // Used by ui-select in view to return list of teams for Team field dropdown
        // and allow inserting new values
        $scope.getItems = function(prop, search) {
            var teams = _.extend([], $scope.teams);
            var options = teams.indexOf(prop) === -1 ? teams.concat(prop) : teams;
            if (search && options.indexOf(search) === -1) {
                options.unshift(search);
            }
            return options.sort();
        };

        // Get a check definition from the backend
        var getCheckDefinition = function() {
            CommunicationService.getCheckDefinition($scope.checkId).then(
                function(response) {
                    $scope.check = response;

                    if ($scope.teams.indexOf($scope.check.owning_team) === -1) {
                        $scope.teams.push($scope.check.owning_team);
                    }

                    $scope.entityFilter.formEntityFilters = response.entities || [];
                    $scope.entityFilter.textEntityFilters = JSON.stringify(response.entities, null, $scope.INDENT) || '[]';
                }
            );
        };

        var getMatchedEntities = function() {

            if ($scope.filter.include_filters[0].length === 0) {
                $scope.matchedEntitiesCount = null;
                $scope.matchedEntities = [];
                return;
            }

            CommunicationService.getMatchedEntities($scope.filter).then(function(response) {
                $scope.matchedEntitiesCount = response.count;
                $scope.matchedEntities = response.entities;
            })
        };

        if ($scope.checkId) {
            getCheckDefinition($scope.checkId);
        }

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
                    }
                }

                // Sort entity filter types.
                $scope.entityFilter.types = _.sortBy($scope.entityFilter.types, "type");
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

                $scope.filter.include_filters[0] = $scope.check.entities;
                getMatchedEntities();
            }
        }, true);

        // If entity filter input method is 'text', reflect changes of entityFilter.textEntityFilters on entityFilter.formEntityFilters
        $scope.$watch('entityFilter.textEntityFilters', function (newVal, oldVal) {
            if ($scope.entityFilterInputMethod === 'text') {
                try {
                    $scope.check.entities = JSON.parse($scope.entityFilter.textEntityFilters);
                    $scope.entityFilter.formEntityFilters = JSON.parse($scope.entityFilter.textEntityFilters);
                    $scope.invalidFormat = false;

                    $scope.filter.include_filters[0] = $scope.check.entities;
                    getMatchedEntities();
                } catch (ex) {
                    $scope.invalidFormat = true;
                }
            }
        }, true);
    }
]);

