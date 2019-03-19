angular.module('zmon2App').directive('alertValueModal', [ '$uibModal', 'APP_CONST','$timeout', 'jsonSortFilter', function($uibModal, APP_CONST,$timeout, jsonSortFilter) {
    return {
        restrict: 'A',
        scope: {
            name: '=',
            value: '=',
            open: '&'
        },
        link: function(scope, elem, attrs) {
            try {
                scope.value = jsonSortFilter(scope.value);
            } catch(err) {}

            var modalCtrl = function($scope, $uibModalInstance, name, value) {
                $scope.filter = '';
                $scope.alert = {
                    name: name,
                    value: JSON.stringify(value, null, APP_CONST.INDENT),
                    ts: new Date()
                };

                $scope.close = function() {
                    $uibModalInstance.dismiss();
                };

                $scope.$watch('filter', function(filter) {
                    if (!filter) {
                        $scope.alert.value = JSON.stringify(value, null, APP_CONST.INDENT);
                        return;
                    }

                    $scope.valid = false;

                    var jp = [];
                    try {
                        jp = jsonpath.nodes(value, filter);
                    } catch (e) {
                        return;
                    }

                    // here's were the magic happens, adds parent keys to jsonpath nodes
                    var filteredValue = _.map(jp, function(n) {
                        var node = n.value;         // avoid jsonpath's root level: {'$': ... }
                        if (n.path.length > 1) {
                            node =  {};
                            node[n.path[n.path.length-1]] = n.value;
                        }
                        return node;
                    }, []);

                    if (filteredValue.length) {
                        filteredValue = filteredValue.length === 1 ? filteredValue[0] : filteredValue;
                        $scope.alert.value = JSON.stringify(filteredValue, null, APP_CONST.INDENT);
                        $scope.valid = true;
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
                            return {
                                alertErrorMessage:scope.value
                            }
                        }
                    }
                });

                modalInstance.opened.then(function() {
                    $timeout(()=>{
                        $(".modal.in .modal-dialog").addClass("error-popup")
                    },0)
                    
                });
                modalInstance.closed.then(function() {
                    $(".modal.in .modal-dialog").removeClass("error-popup")               
                });
            };

            elem.on('click', open);
        }
    };
}]);
