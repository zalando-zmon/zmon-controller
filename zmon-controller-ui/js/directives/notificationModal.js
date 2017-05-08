angular.module('zmon2App').directive('notificationModal', [ '$uibModal', 'CommunicationService', 'FeedbackMessageService',
    function($uibModal, CommunicationService, FeedbackMessageService) {
        return {
            restrict: 'A',
            scope: {
                alert: '=',
                open: '&'
            },
            link: function(scope, elem, attrs) {

                var modalCtrl = function($scope, $uibModalInstance, alert) {
                    $scope.close = function() {
                        $uibModalInstance.dismiss();
                    };

                    $scope.markAsAcknowledged = function() {
                      $uibModalInstance.close();
                    }

                    $scope.alert = scope.alert;
                };

                var open = function() {

                    var modalInstance = $uibModal.open({
                        templateUrl: '/templates/notificationModal.html',
                        controller: modalCtrl,
                        backdrop: true,
                        windowClass: 'alert-notification-window',
                        resolve: {
                            alert: function() {
                                return scope.alert;
                            }
                        }
                    });

                    modalInstance.result.then(function() {
                        CommunicationService.alertNotificationsAck(scope.alert.alert_definition.id)
                            .then(function(){
                                FeedbackMessageService.showSuccessMessage('Alert Notification Successfully Acknowledged', 500, function() {});
                            })
                            .catch(function(err) {
                                FeedbackMessageService.showErrorMessage('Failed to acknowledge Alert Notification, Error: ' + err)
                            })
                    });
                };

                elem.on('click', open);
            }
        };
    }
]);
