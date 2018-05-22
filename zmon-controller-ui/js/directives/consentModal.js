angular.module('zmon2App').directive('consentModal', [ '$uibModal', 'localStorageService',
    function($uibModal, localStorageService) {
        return {
            restrict: 'E',
            scope: {
                title: '@title',
                content: '@content'
            },
            link: function(scope, elem, attrs) {

                attrs.$observe('title', function(title) {
                    scope.title = title;
                });

                attrs.$observe('content', function(content) {
                    scope.content = content;
                });

                var modalCtrl = function($scope, $uibModalInstance, title, content) {
                    $scope.title = title;
                    $scope.content = content;
                    
                    $scope.accept = function() {
                        $uibModalInstance.close();
                    };
                    $scope.decline = function() {
                        $uibModalInstance.dismiss();
                    };
                };

                var open = function() {
                    var modalInstance = $uibModal.open({
                        templateUrl: '/templates/consentModal.html',
                        controller: modalCtrl,
                        backdrop: false,
                        resolve: {
                            title: function() {
                                return scope.title
                            },
                            content: function() {
                                return scope.content
                            }
                        }
                    });

                    modalInstance.result.then(function() {
                        localStorageService.set('consent', new Date());
                    }).catch(function() {
                        window.location.href = '/logout';
                    });
                };

                if (!localStorageService.get('consent')) {
                    open();
                }
            }
        };
    }
]);
