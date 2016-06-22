angular.module('zmon2App').directive('alertValueModal', [ '$uibModal', 'APP_CONST', function($uibModal, APP_CONST) {
    return {
        restrict: 'A',
        scope: {
            name: '=',
            value: '=',
            open: '&'
        },
        link: function(scope, elem, attrs) {

            var modalCtrl = function($scope, $uibModalInstance, name, value) {
                $scope.alert = {
                    name: name,
                    value: '{}',
                    ts: new Date()
                };

                $scope.filter = '$';
                $scope.filteredValue = null;

                $scope.close = function() {
                    $uibModalInstance.dismiss();
                };

                $scope.$watch('filter', function(filter) {
                    var jp = jsonPath(value, filter);
                    jp = jp ? jp[0] : jp;
                    $scope.filteredValue = JSON.stringify(jp, null, APP_CONST.INDENT);
                    if ($scope.filteredValue !== 'false' && $scope.filter !== '') {
                        $scope.alert.value = $scope.filteredValue;
                    }
                });
            };

            var open = function() {
                var modalInstance = $uibModal.open({
                    templateUrl: '/templates/alertValueModal.html',
                    controller: modalCtrl,
                    backdrop: false,
                    windowClass: 'alert-value-modal-window',
                    resolve: {
                        name: function() {
                            return scope.name;
                        },
                        value: function() {
                            return scope.value;
                        }
                    }
                });
            };

            elem.on('click', open);
        }
    };
}]);
