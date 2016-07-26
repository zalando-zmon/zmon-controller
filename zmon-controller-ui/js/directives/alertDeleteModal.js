angular.module('zmon2App').directive('alertDelete', [ '$location', '$uibModal', 'CommunicationService',
    function($location, $uibModal, CommunicationService) {
        return {
            restrict: 'A',
            scope: {
                alert: '=',
            },
            link: function(scope, elem, attrs) {

                var modalCtrl = function($scope, $uibModalInstance, alert) {
                    $scope.alert = alert;
                    $scope.delete = function() {
                        $uibModalInstance.close();
                    };
                    $scope.cancel = function() {
                        $uibModalInstance.dismiss();
                    };
                };

                var open = function() {
                    var modalInstance = $uibModal.open({
                        templateUrl: '/templates/alertDeleteModal.html',
                        controller: modalCtrl,
                        backdrop: false,
                        resolve: {
                            alert: function() {
                                return scope.alert;
                            }
                        }
                    });

                    modalInstance.result.then(function() {
                        CommunicationService.deleteAlertDefinition(scope.alert.id).then(function() {
                            $location.path('/alert-definitions');
                        });
                    });
                };

                elem.on('click', open);
            }
        };
    }
]);

