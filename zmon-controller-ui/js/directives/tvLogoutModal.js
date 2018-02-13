angular.module('zmon2App').directive('tvLogoutModal', [ '$uibModal', 'UserInfoService',
    function($uibModal, UserInfoService) {
        return {
            restrict: 'A',
            link: function(scope, elem, attrs) {

                var modalCtrl = function($scope, $uibModalInstance) {
                    $scope.continue = function() {
                        $uibModalInstance.close();
                    };
                    $scope.cancel = function() {
                        $uibModalInstance.dismiss();
                    };
                };

                var redirect = function() {
                    window.location.href = '/tv/switch'
                }

                var open = function() {
                    var modalInstance = $uibModal.open({
                        templateUrl: '/templates/tvLogOutModal.html',
                        controller: modalCtrl,
                        backdrop: false,
                        resolve: {}
                    });

                    modalInstance.result.then(function() {
                        var url = UserInfoService.get()["logout-url"];
                        window.open(url)
                        redirect()
                    }).catch(function() {
                        redirect()
                    });
                };

                elem.on('click', function(e) {
                    e.preventDefault();
                    var url = UserInfoService.get()["logout-url"];
                    if (url) {
                        open();
                    } else {
                        redirect();
                    }
                });
            }
        };
    }
]);
