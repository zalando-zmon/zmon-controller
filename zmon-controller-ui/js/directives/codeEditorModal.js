angular.module('zmon2App').directive('codeEditorModal', [ '$uibModal', function($uibModal, APP_CONST) {
    return {
        restrict: 'A',
        scope: {
            title: '@',
            code: '='
        },
        link: function(scope, elem, attrs) {

            var modalCtrl = function($scope, $uibModalInstance, code, title) {
                $scope.code = code;
                $scope.title = title;

                $scope.apply = function() {
                    $uibModalInstance.close({
                        code: $scope.code
                    });
                };

                $scope.cancel = function() {
                    $uibModalInstance.dismiss();
                };
            };

            var open = function() {
                var modalInstance = $uibModal.open({
                    templateUrl: '/templates/codeEditorModal.html',
                    controller: modalCtrl,
                    backdrop: false,
                    windowClass: 'code-editor-modal-window',
                    resolve: {
                        code: function() {
                            return scope.code;
                        },
                        title: function() {
                            return scope.title;
                        }
                    }
                });

                modalInstance.result.then(function(result) {
                    scope.code = result.code;
                });
            };

            scope.$watch('code', function(code) {
                scope.code = code;
            });

            elem.on('click', open);
        }
    };
}]);
