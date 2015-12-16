angular.module('zmon2App').controller('DashboardDefinitionCtrl', ['$scope', 'localStorageService','$route', '$routeParams', '$location', '$modal','MainAlertService', 'CommunicationService', 'FeedbackMessageService', 'APP_CONST', 'UserInfoService',
    function($scope, localStorageService, $route,$routeParams, $location, $modal,MainAlertService, CommunicationService, FeedbackMessageService, APP_CONST, UserInfoService) {

        var STORAGE_KEY = 'dashboardId';

        $scope.defaultDashboardId = localStorageService.get(STORAGE_KEY);
        $scope.noFavourite = $location.search().noFavourite;

        $scope.DefinitionsCtrl = this;

        $scope.$parent.activePage = 'dashboards';
        $scope.dashboardDefinitions = [];
        $scope.userInfo = UserInfoService.get();

        this.fetchDashboardDefinitions = function() {
            CommunicationService.getAllDashboards().then(
                function(data) {
                    $scope.dashboardDefinitions = data;
                }
            );
        };

        // Non-refreshing; one-time listing
        MainAlertService.removeDataRefresh();
        this.fetchDashboardDefinitions();

        $scope.setDefaultDashboard = function(dashboardID) {
            localStorageService.add(STORAGE_KEY, dashboardID);
        };


        $scope.showDeleteDashboardModal = function($id,$name) {

            // Delete dashboard modal
            var deleteDashboardModalInstance = $modal.open({
                templateUrl: '/templates/deleteDashboardModal.html',
                controller: deleteDashboardModalCtrl,
                backdrop: false,
                resolve: {
                    dashboardName: function() {
                        return $name;
                    }
                }

            });

            deleteDashboardModalInstance.result.then(
                function() {
                    CommunicationService.deleteDashboard($id).then(function() {
                        $route.reload();
                    });
                });
        };


        var deleteDashboardModalCtrl = function($scope, $modalInstance,dashboardName) {

            $scope.dashboardName = dashboardName;

            $scope.delete = function() {
                $modalInstance.close();
            };

            $scope.cancel = function() {
                $modalInstance.dismiss();
            };
        };
    }

]);
