angular.module('zmon2App').directive('tvLogoutModal', [ '$uibModal', 'UserInfoService',
    function($uibModal, UserInfoService) {
        return {
            restrict: 'A',
            scope: {
                tvMode: '='
            },
            link: function(scope, elem, attrs) {

                var modalCtrl = function($scope, $uibModalInstance) {
                    $scope.continue = function() {
                        $uibModalInstance.close();
                    };
                    $scope.cancel = function() {
                        $uibModalInstance.dismiss();
                    };
                };

                var open = function() {
                    var modalInstance = $uibModal.open({
                        templateUrl: '/templates/tvLogOutModal.html',
                        controller: modalCtrl,
                        backdrop: false,
                        resolve: {}
                    });

                    modalInstance.result.then(function() {
                        window.location.href = '/tv/switch'
                        window.open(UserInfoService.get()["logout-url"])
                    }).catch(function() {
                        window.location.href = '/tv/switch'
                    });
                };

                scope.$watch('tvMode', function() {
                    if (scope.tvMode) {
                        open();
                    }
                });
            }
        };
    }
]);
