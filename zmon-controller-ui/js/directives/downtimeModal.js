angular.module('zmon2App').directive('downtimeModal', [ '$uibModal', '$timeout', 'CommunicationService', 'FeedbackMessageService', 
    function($uibModal, $timeout, CommunicationService, FeedbackMessageService) {
        return {
            restrict: 'A',
            scope: {
                alertId: '=',
                downtimeEntities: '='
            },
            link: function(scope, elem, attrs) {

                var modalCtrl = function($scope, $uibModalInstance, downtimeAlertId, downtimeEntities) {
                    $scope.downtimeAlertId = downtimeAlertId;
                    $scope.downtimeEntities = downtimeEntities;
                    $scope.scheduleMode = false;
                    $scope.minDate = new Date();
                    $scope.maxDate = new Date($scope.minDate.getFullYear() + 1, $scope.minDate.getMonth(), $scope.minDate.getDate() - 1);
                    $scope.dateFormat = 'dd-MMMM-yyyy';
                    $scope.dateOptions = {
                        'year-format': "'yy'",
                        'starting-day': 1,
                        'show-weeks': true
                    };
                    $scope.models = {
                        duration: new Date(2013, 0, 0, 0, 0), // using Date object, but will only be using the HH:MM part as duration
                        startDate: new Date(),
                        startTime: new Date(),
                        endDate: new Date(),
                        endTime: new Date(new Date().getTime() + 30 * 60 * 1000), // 30' in the future from now
                        comment: null,
                        startDatepickerOpened: false,
                        endDatepickerOpened: false
                    };

                    $scope.ok = function() {
                        var startTime = new Date();
                        var endTime = $scope.calcDowntimeEndtime();

                        if ($scope.scheduleMode) {
                            var sd = $scope.models.startDate;
                            var st = $scope.models.startTime;
                            var ed = $scope.models.endDate;
                            var et = $scope.models.endTime;
                            startTime = new Date(sd.getFullYear(), sd.getMonth(), sd.getDate(), st.getHours(), st.getMinutes());
                            endTime = new Date(ed.getFullYear(), ed.getMonth(), ed.getDate(), et.getHours(), et.getMinutes());
                        }

                        if (startTime.getTime() > endTime.getTime()) {
                            return FeedbackMessageService.showErrorMessage('Start date must precede end date!');
                        }

                        $uibModalInstance.close({
                            "alert_definition_id": scope.alertId,
                            "startTime": startTime,
                            "endTime": endTime,
                            "comment": $scope.models.comment,
                            "entity_ids": downtimeEntities
                        });
                    };

                    $scope.openDatepicker = function($event, which) {
                        $event.preventDefault();
                        $event.stopPropagation();
                        if (which === 'start') {
                            $scope.models.startDatepickerOpened = !$scope.models.startDatepickerOpened;
                            $scope.models.endDatepickerOpened = false;
                        } else if (which === 'end') {
                            $scope.models.endDatepickerOpened = !$scope.models.endDatepickerOpened;
                            $scope.models.startDatepickerOpened = false;
                        }
                    };

                    // Applicable only for "Duration" type downtimes
                    $scope.calcDowntimeEndtime = function() {
                        var durationInMs = $scope.models.duration.getHours() * 3600000 + $scope.models.duration.getMinutes() * 60000;
                        var nowPlusDuration = new Date(new Date().getTime() + durationInMs);
                        return nowPlusDuration;
                    };

                    $scope.cancel = function() {
                        $uibModalInstance.dismiss();
                    };

                    $scope.removeEntity = function(exclEntity) {
                        $scope.downtimeEntities.splice($scope.downtimeEntities.indexOf(exclEntity), 1);

                        if ($scope.downtimeEntities.length === 0) {
                            $scope.cancel();
                        }
                    };
                };

                var open = function() {

                    var modalInstance = $uibModal.open({
                        templateUrl: '/templates/downtimeModal.html',
                        controller: modalCtrl,
                        backdrop: false,
                        windowClass: 'downtime-modal',
                        resolve: {
                            downtimeAlertId: function() {
                                return scope.alertId;
                            },
                            downtimeEntities: function() {
                                return scope.downtimeEntities;
                            }
                        }
                    });

                    modalInstance.result.then(function(downtime) {
                        CommunicationService.scheduleDowntime(downtime).then(function(downtimeUUIDs) {
                            var message = 'Success setting ' + downtimeUUIDs.length;
                                message += (downtimeUUIDs.length > 1 ? ' downtimes' : ' downtime');
                            FeedbackMessageService.showSuccessMessage(message);
                        });
                    });
                };

                elem.on('click', open);
            }
        };
    }
]);
