angular.module('zmon2App').controller('DashboardConfigurationCtrl', ['$scope', '$window', '$routeParams', '$location', 'MainAlertService', 'CommunicationService', 'FeedbackMessageService',
    function($scope, $window, $routeParams, $location, MainAlertService, CommunicationService, FeedbackMessageService) {

        var STORAGE_KEY = 'dashboardId';
        $scope.$parent.activePage = 'dashboard-configuration';
        $scope.invalidFormat = false;
        $scope.teamsJson = '[]';
        $scope.dashboardName = '';
        $scope.dashboardViewMode = 'FULL';
        $scope.dashboardEditOption = 'PRIVATE';
        $scope.debug = null;
        $scope.focusedElement = null;
        $scope.cloneable = false;
        $scope.editable = false;
        $scope.editOptionEditable = false;
        $scope.allTags = [];
        $scope.widgets = [];
        $scope.widgetsJson = '[]';
        $scope.widgetConfigFormVisible = true;

        $scope.widgetTypes = [ {
            "type": "Check Chart"
        }, {
            "type": "Kairos Chart",
        }, {
            "type": "Gauge",
        }, {
            "type": "Value",
        }, {
            "type": "Trend",
        }, {
            "type": "Iframe",
        }];

        $scope.dashboard = {
            id: null,
            name: '',
            created_by: null,
            last_modified_by: null,
            tags: null,
            widget_configuration: [],
            alert_teams: null
        };

        $scope.loadDashboard = function(id) {
            CommunicationService.getDashboard(id).then(function(data) {

                $scope.widgets = JSON.parse(data.widget_configuration);
                $scope.teams = data.alert_teams;
                $scope.widgetsJson = JSON.stringify($scope.widgets, null, '    ');
                $scope.teamsJson = JSON.stringify($scope.teams || [], null, '    ');
                $scope.cloneable = data.cloneable;
                $scope.editable = data.editable;
                $scope.editOptionEditable = data.edit_option_editable;

                // If no value comes form the server, set default view to "full".
                $scope.dashboardViewMode = data.view_mode || "FULL";
                $scope.dashboardEditOption = data.edit_option;

                if ($scope.mode !== 'clone') {
                    $scope.dashboardName = data.name;
                }

                $scope.dashboard = data;
            });
        };

        if ($routeParams.dashboardId !== null && $routeParams.dashboardId > 0) {
            $scope.mode = 'edit';
            $scope.loadDashboard($routeParams.dashboardId);
        } else if ($routeParams.cloneFromDashboardId) {
            $scope.mode = 'clone';
            $scope.loadDashboard($routeParams.cloneFromDashboardId);
        } else {
            $scope.widgets = [];
            $scope.widgetsJson = '[]';
            $scope.teamsJson = '[]';
            $scope.mode = 'add';
        }

        $scope.cancel = function() {
            $scope.ddForm.submitted = false;
            $location.path('/dashboards');
        };

        $scope.save = function() {
            if ($scope.ddForm.$valid) {
                try {
                    $scope.dashboard.widget_configuration = angular.toJson($scope.widgets);
                    $scope.dashboard.alert_teams = JSON.parse($scope.teamsJson);
                    $scope.dashboard.name = $scope.dashboardName;
                    $scope.dashboard.view_mode = $scope.dashboardViewMode;
                    $scope.dashboard.edit_option = $scope.dashboardEditOption;

                    if ($scope.dashboard.alert_teams.length === 0) {
                        delete $scope.dashboard.alert_teams;
                    }

                    if ($scope.mode == 'clone') {
                        delete $scope.dashboard.id;
                    }

                    CommunicationService.updateDashboard($scope.dashboard).then(function(data) {
                        $scope.debug = data;
                        $window.history.back();
                    });
                } catch (e) {
                    $scope.invalidFormat = true;
                    FeedbackMessageService.showErrorMessage('JSON format is incorrect' + e);
                    return;
                }
            } else {
                $scope.ddForm.submitted = true;
                $scope.focusedElement = null;
            }
        };

        // Get all available tags
        CommunicationService.getAllTags().then(
            function(data) {
             $scope.allTags = _.map(data, 'text');
            }
        );

        // Non-refreshing; one-time listing
        MainAlertService.removeDataRefresh();
    }
]);
