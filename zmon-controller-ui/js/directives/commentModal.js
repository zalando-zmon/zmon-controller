angular.module('zmon2App').directive('commentModal', ['$uibModal', '$timeout', 'CommunicationService', 'MainAlertService', 
    function($uibModal, $timeout, CommunicationService, MainAlertService) {
        return {
            restrict: 'A',
            scope: {
                alertId: '=',
                count: '=',
                entity: '='
            },
            link: function(scope, elem, attributes) {

                var scrollToBottom = function() {
                    $timeout(function() {
                        $('.modal-body').scrollTop($('.modal-body').prop('scrollHeight'));
                    });
                };

                var modalCtrl = function($scope, $uibModalInstance, alertId) {
                    $scope.comments = [];
                    $scope.allLoaded = false;
                    $scope.limit = 11;
                    $scope.offset = 0;
                    $scope.comment = {
                        comment: '',
                        alert_definition_id: alertId,
                        entity_id: scope.entity
                    };

                    var fetchComments = function(cb) {
                        CommunicationService.getAlertComments(alertId, $scope.limit, $scope.offset).then(function(comments) {
                            $scope.comments = $scope.comments.concat(comments);
                            $scope.allLoaded = comments.length < $scope.limit;
                            scope.count = $scope.comments.length;
                            if (cb) { cb(); }
                        });
                    };

                    $scope.addComment = function() {
                        if ($scope.form.$valid) {
                            CommunicationService.insertAlertComment($scope.comment).then(function(comment) {
                                $scope.comments = [comment].concat($scope.comments);
                                scope.count = $scope.comments.length;
                                $scope.comment.comment = '';
                                $scope.offset++;
                                scrollToBottom();
                            });
                        }
                    };

                    $scope.removeComment = function(id) {
                        CommunicationService.deleteAlertComment(id).then(function(status) {
                            $scope.offset--;
                            $scope.comments = $scope.comments.filter(function(comment) {
                                return comment.id !== id;
                            });
                            scope.count = $scope.comments.length;
                        });
                    };

                    $scope.loadMoreComments = function() {
                        $scope.offset += $scope.limit;
                        fetchComments();
                    };

                    $scope.close = function() {
                        $uibModalInstance.close({
                            comments: $scope.comments
                        });
                    };

                    fetchComments(function() {
                        scrollToBottom();
                    });
                };

                var open = function(alertId) {
                    var modalInstance = $uibModal.open({
                        templateUrl: '/templates/commentsModal.html',
                        controller: modalCtrl,
                        backdrop: false,
                        windowClass: 'comments-modal',
                        resolve: {
                            alertId: function() {
                                return scope.alertId;
                            }
                        }
                    });

                    modalInstance.result.then(function(result) {
                        scope.comments = result.comments;
                        scope.count = result.comments.length;
                    });
                };

                elem.on('click', open);
            }
        };
    }
]);
