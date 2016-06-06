angular.module('zmon2App').controller('AlertDetailsCtrl', ['$scope', '$location', 'timespanFilter', '$routeParams', '$modal', 'MainAlertService', 'CommunicationService', 'DowntimesService', 'FeedbackMessageService', 'localStorageService', 'UserInfoService', 'APP_CONST',
        function($scope, $location, timespanFilter, $routeParams, $modal, MainAlertService, CommunicationService, DowntimesService, FeedbackMessageService, localStorageService, UserInfoService, APP_CONST) {

        // Set in parent scope which page is active for the menu styling
        $scope.$parent.activePage = 'alert-details'; // is not a menu option, but still set
        $scope.entitiesFilter = [];
        $scope.entitiesExcludeFilter = [];
        $scope.alertDefinitionId = $routeParams.alertId;
        $scope.alertDefinition = null;
        $scope.alertDetails = null; // all alerts (active + currently in downtime)
        $scope.activeAlerts = null; // 1st tab, 1st button filter content with alerts currently active
        $scope.alertsInDowntime = null; // 1st tab, 2nd button filter content with alerts currently in downtime
        $scope.checkResults = null; // 1st tab, 3rd button filter content with OKs [checks results i.e. w/o alert]
        $scope.allAlertsAndChecks = null; // a concatenation of the (non-null) arrays activeAlerts, alertsInDowntime and checkResults
        $scope.allDowntimes = null; // 2nd tab content with all downtimes (for alerts + OK's, current and future)
        $scope.allHistory = null; // 3rd tab content with all history entries
        $scope.sortType = 'result.ts'; // sort list of alerts by timestamp
        $scope.sortOrder = false; // start with reverse sort order (-result.ts);
        $scope.sortTypeDowntimes = 'entity';
        $scope.sortOrderDowntimes = false;
        $scope.sortTypeChildren = 'name';
        $scope.sortOrderChildren = false;
        $scope.showActiveAlerts = true; // 1st tab, 1st button
        $scope.showAlertsInDowntime = false; // 1st tab, 2nd button
        $scope.showCheckResults = false; // 1st tab, 3rd button
        $scope.alertDetailsSearch = { str: ''}; // entity filter

        $scope.checkDefinition = null;
        $scope.addDowntimeEntities = [];
        $scope.deleteDowntimeUUIDs = [];
        // History button active
        $scope.activeHistoryButton = {
            "1": false,
            "7": false,
            "14": false,
            "-1": false
        };
        $scope.historyFromInEpochSeconds = null;
        $scope.currentDate = new Date();

        $scope.alertComments = [];

        $scope.alertJson = '';

        $scope.userInfo = UserInfoService.get();

        // Querystring is '?downtimes' when user comes from dashboard clicking on the flag icon of an alert to see its downtimes
        if ($location.search().downtimes) {
            $scope.showAlertsInDowntime = true;
        }

        $scope.$watch('[activeAlerts, alertsInDowntime, checkResults]', function() {
            $scope.allAlertsAndChecks = _.reduce([$scope.activeAlerts, $scope.alertsInDowntime, $scope.checkResults], function(result, nextDataArray) {
                if (nextDataArray && nextDataArray.length !== 0) {
                    return result.concat(nextDataArray);
                }
                return result;
            }, []);
        }, true);

        $scope.$watch('alertDetailsSearch.str', function(str) {
            localStorageService.set('alertDetailsSearchStr', str);
        });

        $scope.timeAgo = function(epochPastTs) {
            var timeIntervalSinceLastUpdate = MainAlertService.millisecondsApart(epochPastTs, MainAlertService.getLastUpdate());
            return timespanFilter(timeIntervalSinceLastUpdate);
        };

        var setLinkToTrialRun = function () {
            var params = {
                name: $scope.alertDefinition.name,
                description: $scope.alertDefinition.description,
                check_command: $scope.checkDefinition.command,
                alert_condition: $scope.alertDefinition.condition,
                entities: $scope.entitiesFilter,
                entities_exclude: $scope.entitiesExcludeFilter,
                interval: $scope.checkDefinition.interval,
                period: $scope.alertDefinition.period,
                parameters: $scope.alertDefinition.parameters || []
            };
            $scope.alertJson = window.encodeURIComponent(JSON.stringify(params));
        };

        $scope.refreshAlertDetails = function() {
            var now = new Date().getTime() / 1000;
            CommunicationService.getAlertDefinition($scope.alertDefinitionId).then(function(response) {
                    $scope.alertDefinition = response;
                    $scope.currentDate = new Date();

                    // Fetch the information needed to fill the "Details" panel for both ACTIVE & INACTIVE alerts
                    CommunicationService.getCheckDefinition($scope.alertDefinition.check_definition_id).then(
                        function(response) {
                            $scope.checkDefinition = response;
                            $scope.entitiesFilter = [];
                            $scope.entitiesExcludeFilter = [];

                            // Multiply and merge check entities filter with alert entities filter.
                            if (_.size($scope.checkDefinition.entities) < 1) {
                                $scope.entitiesFilter = $scope.alertDefinition.entities;
                            } else if (_.size($scope.alertDefinition.entities) < 1) {
                                $scope.entitiesFilter = $scope.checkDefinition.entities;
                            } else {
                                _.each($scope.checkDefinition.entities, function (cEntity) {
                                    _.each($scope.alertDefinition.entities, function (aEntity) {
                                        var mergedEntity = _.extend({}, cEntity, aEntity);
                                        $scope.entitiesFilter.push(mergedEntity);
                                    });
                                });
                            }

                            // Remove entity filter duplicates!
                            $scope.entitiesFilter = _.uniq($scope.entitiesFilter, false, function(eFilter) {
                                return JSON.stringify(eFilter, null, 0);
                            });

                            // Remove entity filters with no "type".
                            $scope.entitiesFilter = _.reject($scope.entitiesFilter, function(eFilter) {
                                return (eFilter.type === undefined || eFilter.type === null || eFilter.type === "");
                            });

                            // Multiply and merge check entities EXCLUDE filter with alert entities EXCLUDE filter.
                            if (_.size($scope.checkDefinition.entities_exclude) < 1) {
                                $scope.entitiesExcludeFilter = $scope.alertDefinition.entities_exclude;
                            } else if (_.size($scope.alertDefinition.entities_exclude) < 1) {
                                $scope.entitiesExcludeFilter = $scope.checkDefinition.entities_exclude;
                            } else {
                                _.each($scope.checkDefinition.entities_exclude, function (cEntity) {
                                    _.each($scope.alertDefinition.entities_exclude, function (aEntity) {
                                        var mergedEntity = _.extend({}, cEntity, aEntity);
                                        $scope.entitiesExcludeFilter.push(mergedEntity);
                                    });
                                });
                            }

                            // Remove entity EXCLUDE filter duplicates!
                            $scope.entitiesExcludeFilter = _.uniq($scope.entitiesExcludeFilter, false, function(eFilter) {
                                return JSON.stringify(eFilter, null, 0);
                            });

                            // Remove entity EXCLUDE filters with no "type".
                            $scope.entitiesExcludeFilter = _.reject($scope.entitiesExcludeFilter, function(eFilter) {
                                return (eFilter.type === undefined || eFilter.type === null || eFilter.type === "");
                            });

                            // Get any children alerts that inherit from this alert
                            CommunicationService.getAlertDefinitionChildren($scope.alertDefinition.id).then(function(response) {
                                $scope.alertDefinitionChildren = response;
                            });

                            // Get Parent Alert Definition if a paret_id is preset
                            if ($scope.alertDefinition.parent_id) {
                                CommunicationService.getAlertDefinition($scope.alertDefinition.parent_id).then(function(response) {
                                    $scope.parentAlertDefinition = response;
                                });
                            }

                            if ($scope.alertDefinition.status === 'ACTIVE') {
                                CommunicationService.getAlertDetails($scope.alertDefinitionId).then(function(response) {
                                    $scope.alertDetails = response;

                                    // Split into 2 sets: active alerts and alerts currently in downtime additionally flagging each item accordingly
                                    // so we can discriminate them in the allAlertAndChecks array
                                    $scope.activeAlerts = [];
                                    $scope.alertsInDowntime = [];

                                    _.each($scope.alertDetails.entities, function(nextAlert) {
                                        if (nextAlert.result.downtimes && nextAlert.result.downtimes.length) {
                                            // Add it to alertsInDowntime if any of its downtimes is active now; otherwise add it to activeAlerts
                                            if (DowntimesService.isAnyDowntimeNow(nextAlert.result.downtimes)) {
                                                nextAlert.isAlertInDowntime = true;
                                                $scope.alertsInDowntime.push(nextAlert);
                                            } else {
                                                nextAlert.isActiveAlert = true;
                                                $scope.activeAlerts.push(nextAlert);
                                            }
                                        } else {
                                            // alert has no downtimes; by definition goes to activeAlerts
                                            nextAlert.isActiveAlert = true;
                                            $scope.activeAlerts.push(nextAlert);
                                        }
                                    });

                                    $scope.namesOfEntitiesWithAlert = _.reduce($scope.alertDetails.entities, function(prev, curr) {
                                        prev[curr.entity] = true;
                                        return prev;
                                    }, {});

                                    CommunicationService.getCheckResultsForAlert($scope.alertDefinitionId, 1).then(
                                        function(response) {
                                            $scope.checkResults = _.map(_.filter(response, function(entityRes) {
                                                return !(entityRes.entity in $scope.namesOfEntitiesWithAlert);
                                            }), function(entityRes) {
                                                return {
                                                    'entity': entityRes.entity,
                                                    'result': entityRes.results[0],
                                                    'isCheckResult': true
                                                };
                                            });
                                        }

                                    );

                                    CommunicationService.getDowntimes($scope.alertDefinitionId).then(
                                        function(response) {
                                            $scope.allDowntimes = response;
                                        }
                                    );
                                });
                            }

                            // AlertDefinition, checkDefinition and entities filters are set. A link to TrialRun can
                            // be now generated and set on $scope.alertJson.
                            setLinkToTrialRun();
                        }
                    );
                }

            );
        };

        var deleteAlertDefinitionModalCtrl = function($scope, $modalInstance, alertDefinition) {
            $scope.alertDefinition = alertDefinition;
            $scope.delete = function() {
                $modalInstance.close();
            };
            $scope.cancel = function() {
                $modalInstance.dismiss();
            };
        };

        $scope.showDeleteAlertDefinitionModal = function() {

            // Delete alert definition modal
            var deleteAlertDefinitionModalInstance = $modal.open({
                templateUrl: '/templates/deleteAlertDefinitionModal.html',
                controller: deleteAlertDefinitionModalCtrl,
                backdrop: false,
                resolve: {
                    alertDefinition: function() {
                        return $scope.alertDefinition;
                    }
                }
            });

            deleteAlertDefinitionModalInstance.result.then(
                function() {
                    CommunicationService.deleteAlertDefinition($scope.alertDefinition.id).then(function() {
                        $location.path('/alert-definitions');
                    });
                });
        };

        // Downtime modal window's controller
        var downtimeModalCtrl = function($scope, $modalInstance, downtimeAlertId, downtimeEntities) {
            $scope.downtimeAlertId = downtimeAlertId;
            $scope.downtimeEntities = downtimeEntities;
            $scope.isDurationTabActive = true;
            $scope.minDate = new Date();
            $scope.maxDate = new Date($scope.minDate.getFullYear() + 1, $scope.minDate.getMonth(), $scope.minDate.getDate() - 1);
            $scope.dateFormat = 'dd-MMMM-yyyy';
            $scope.dateOptions = {
                'year-format': "'yy'",
                'starting-day': 1,
                'show-weeks': true
            };
            $scope.models = {
                // Tab 1: downtime duration starting now
                downtimeDuration: new Date(2013, 0, 0, 0, 0), // using Date object, but will only be using the HH:MM part as duration
                downStartDate: new Date(),
                downStartTime: new Date(),
                downEndDate: new Date(),
                downEndTime: new Date(new Date().getTime() + 30 * 60 * 1000), // 30' in the future from now
                downtimeComment: null,
                startDatepickerOpened: false,
                endDatepickerOpened: false
            };

            $scope.setDurationTabActive = function(isDurationTabActive) {
                $scope.isDurationTabActive = isDurationTabActive;
            };

            $scope.ok = function() {
                // Depending on which tab was active, we return corresponding data
                if ($scope.isDurationTabActive) {

                    $modalInstance.close({
                        "downStartTime": new Date(),
                        "downEndTime": $scope.calcDowntimeEndtime(),
                        "comment": $scope.models.downtimeComment
                    });
                } else {
                    var downStartTime = new Date($scope.models.downStartDate.getFullYear(), $scope.models.downStartDate.getMonth(), $scope.models.downStartDate.getDate(), $scope.models.downStartTime.getHours(), $scope.models.downStartTime.getMinutes());
                    var downEndTime = new Date($scope.models.downEndDate.getFullYear(), $scope.models.downEndDate.getMonth(), $scope.models.downEndDate.getDate(), $scope.models.downEndTime.getHours(), $scope.models.downEndTime.getMinutes());
                    // Check end time is after start time
                    if (downStartTime.getTime() > downEndTime.getTime()) {
                        FeedbackMessageService.showErrorMessage('Start date must precede end date!');
                    } else {
                        $modalInstance.close({
                            "downStartTime": downStartTime,
                            "downEndTime": downEndTime,
                            "comment": $scope.models.downtimeComment
                        });
                    }
                }
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
                var durationInMs = $scope.models.downtimeDuration.getHours() * 3600000 + $scope.models.downtimeDuration.getMinutes() * 60000;
                var nowPlusDuration = new Date(new Date().getTime() + durationInMs);
                return nowPlusDuration;
            };

            $scope.cancel = function() {
                $modalInstance.dismiss();
            };

            /**
             * When user clicks the [x] of an entity in the modal entity list to exclude it from the downtime
             */
            $scope.removeEntity = function(exclEntity) {
                $scope.downtimeEntities.splice($scope.downtimeEntities.indexOf(exclEntity), 1);
                if ($scope.downtimeEntities.length === 0) {
                    $scope.cancel();
                }
            };
        };

        $scope.showDowntimeModal = function(alertId) {
            var downtimeModalInstance = $modal.open({
                templateUrl: '/templates/downtimeModal.html',
                controller: downtimeModalCtrl,
                backdrop: false,
                resolve: {
                    downtimeAlertId: function() {
                        return alertId;
                    },
                    downtimeEntities: function() {
                        return $scope.addDowntimeEntities;
                    }
                }
            });

            downtimeModalInstance.result.then(
                // Modal's OK pressed; returned downtimeData has format: {"downStartTime": <date Object>, "downEndTime": <date Object>, "comment": <string>}
                // POST a new downtime
                function(downtimeData) {
                    // In this case the entity-alert relationship is N-1
                    var entitiesPerAlert = [{
                        "alert_definition_id": $scope.alertDefinitionId,
                        "entity_ids": $scope.addDowntimeEntities
                    }];
                    CommunicationService.scheduleDowntime(downtimeData.comment, downtimeData.downStartTime, downtimeData.downEndTime, entitiesPerAlert).then(
                        function(downtimeUUIDs) {
                            FeedbackMessageService.showSuccessMessage('Success setting ' + $scope.addDowntimeEntities.length + ($scope.addDowntimeEntities.length > 1 ? ' downtimes' : ' downtime'));
                            $scope.addDowntimeEntities = [];
                        }

                    );
                });
        };

        /**
         * Delete multiple downtimes (Downtimes tab)
         */
        $scope.deleteMultiDowntimes = function() {
            var that = $scope;
            CommunicationService.deleteDowntime($scope.deleteDowntimeUUIDs).then(
                function(data) {
                    FeedbackMessageService.showSuccessMessage('Downtimes deleted', 3000);
                    that.deleteDowntimeUUIDs = [];
                }, function(httpStatus) {
                    that.deleteDowntimeUUIDs = [];
                }
            );
        };

        /**
         * Returns t/f (Downtimes tab). Again displayed don't mean visible.
         */
        $scope.areAllDowntimesChecked = function() {
            var totalDisplayedDowntimes = 0;
            if ($scope.allDowntimes && $scope.allDowntimes.length) {
                totalDisplayedDowntimes = $scope.allDowntimes.length;
                if ($scope.deleteDowntimeUUIDs.length === totalDisplayedDowntimes) {
                    return true;
                }
            }
            return false;
        };

        /**
         * Triggered when individual delete downtime checkbox is checked/unchecked (Downtimes tab)
         */
        $scope.toggleSingleDeleteDowntime = function(downtimeUUID) {
            var idx = $scope.deleteDowntimeUUIDs.indexOf(downtimeUUID);
            if (idx > -1) {
                // It's already in deleteDowntimeUUIDs; remove it
                $scope.deleteDowntimeUUIDs.splice(idx, 1);
            } else {
                // Not in deleteDowntimeUUIDs; add it
                $scope.deleteDowntimeUUIDs.push(downtimeUUID);
            }
        };

        /**
         * (Downtimes tab) Triggered when overall delete downtime checkbox of header is checked/unchecked to set/unset all delete downtime checkboxes
         */
        $scope.toggleAllDeleteDowntimes = function() {
            if (!$scope.areAllDowntimesChecked()) {
                $scope.deleteDowntimeUUIDs = [];
                // Delete downtimes checkboxes are partially checked; proceed to check all of them
                _.each(_.map($scope.allDowntimes, 'id'), function(nextUUID) {
                    $scope.deleteDowntimeUUIDs.push(nextUUID);
                });
            } else {
                $scope.deleteDowntimeUUIDs = [];
            }
        };

        /**
         * (Alerts tab) Triggered when individual entity checkbox for downtime is checked/unchecked
         */
        $scope.toggleEntityAddDowntime = function(entityId) {
            var idx = $scope.addDowntimeEntities.indexOf(entityId);
            if (idx > -1) {
                // It's already in addDowntimeEntities; remove it
                $scope.addDowntimeEntities.splice(idx, 1);
            } else {
                // Not in addDowntimeEntities; add it
                $scope.addDowntimeEntities.push(entityId);
            }
        };

        /**
         * (Alerts tab) Triggered when overall downtime checkbox of header is checked/unchecked to set/unset all entity checkboxes to add downtime
         * What is included by "all", depends on which groups are selected to be displayed at the moment
         */
        $scope.toggleAllEntitiesAddDowntime = function() {
            if (!$scope.areAllDisplayedAlertsChecked()) {
                $scope.addDowntimeEntities = [];
                // Check all
                if ($scope.showActiveAlerts) {
                    _.each(_.map($scope.activeAlerts, 'entity'), function(entity) {
                        $scope.addDowntimeEntities.push(entity);
                    });
                }
                if ($scope.showAlertsInDowntime) {
                    _.each(_.map($scope.alertsInDowntime, 'entity'), function(entity) {
                        $scope.addDowntimeEntities.push(entity);
                    });
                }
                if ($scope.showCheckResults) {
                    _.each(_.map($scope.checkResults, 'entity'), function(entity) {
                        $scope.addDowntimeEntities.push(entity);
                    });
                }
            } else {
                // Uncheck all
                $scope.addDowntimeEntities = [];
            }
        };

        /**
         * Trivial function, but necessary because the directive for infinite-scroll requires the attribute "getTotalNumDisplayedItems" to be a function ref
         * Return # of items to be displayed and not necessarilly visible on screen
         */
        $scope.getTotalNumDowntimes = function() {
            return $scope.allDowntimes ? $scope.allDowntimes.length : 0;
        };

        /**
         * (Alerts tab) Returns true if all alerts from groups selected to be displayed are checked
         */
        $scope.areAllDisplayedAlertsChecked = function() {
            return $scope.addDowntimeEntities.length === $scope.getTotalNumDisplayedAlerts();
        };

        /**
         * Returns number of entities from groups (activeAlerts, alertsInDowntime, checkResults) selected to be displayed
         * Displayed doesn't mean immediately visible on screen due to infinite scroll; it means belonging to a group that is selected to be displayed and not hidden
         */
        $scope.getTotalNumDisplayedAlerts = function() {
            var numDisplayed = 0;
            if ($scope.showActiveAlerts && $scope.activeAlerts) {
                numDisplayed += $scope.activeAlerts.length;
            }
            if ($scope.showAlertsInDowntime && $scope.alertsInDowntime) {
                numDisplayed += $scope.alertsInDowntime.length;
            }
            if ($scope.showCheckResults && $scope.checkResults) {
                numDisplayed += $scope.checkResults.length;
            }
            return numDisplayed;
        };

        /**
         * Toggles the state of showActiveAlerts, showAlertsInDowntime and showCheckResults. Passed param defines which one gets toggled
         * When a group is deselected from being displayed, any references to its entities are remove from addDowntimeEntities
         * Passed param is 'activeAlerts', 'alertsInDowntime' or 'checkResults'
         */
        $scope.toggleShowEntities = function(entityType) {
            if (entityType === 'activeAlerts') {
                $scope.showActiveAlerts = !$scope.showActiveAlerts;
                if ($scope.showActiveAlerts === false) {
                    // Active alerts no longer displayed; remove from addDowntimeEntities any references to them
                    _.each(_.map($scope.activeAlerts, 'entity'), function(entity) {
                        var idx = $scope.addDowntimeEntities.indexOf(entity);
                        if (idx > -1) {
                            $scope.addDowntimeEntities.splice(idx, 1);
                        }
                    });
                }
            } else if (entityType === 'alertsInDowntime') {
                $scope.showAlertsInDowntime = !$scope.showAlertsInDowntime;
                if ($scope.showAlertsInDowntime === false) {
                    // Alerts in downtime no longer displayed; remove from addDowntimeEntities any references to them
                    _.each(_.map($scope.alertsInDowntime, 'entity'), function(entity) {
                        var idx = $scope.addDowntimeEntities.indexOf(entity);
                        if (idx > -1) {
                            $scope.addDowntimeEntities.splice(idx, 1);
                        }
                    });
                }
            } else {
                $scope.showCheckResults = !$scope.showCheckResults;
                if ($scope.showCheckResults === false) {
                    // Check results no longer displayed; remove from addDowntimeEntities any references to them
                    _.each(_.map($scope.checkResults, 'entity'), function(entity) {
                        var idx = $scope.addDowntimeEntities.indexOf(entity);
                        if (idx > -1) {
                            $scope.addDowntimeEntities.splice(idx, 1);
                        }
                    });
                }
            }
        };

        $scope.startAlertDetailsRefresh = function() {
            MainAlertService.startDataRefresh('AlertDetailsCtrl', _.bind($scope.refreshAlertDetails, this), APP_CONST.ALERT_DETAILS_REFRESH_RATE, true);
        };


        $scope.getComments = function(alertId, limit, offset, cb) {
            CommunicationService.getAlertComments(alertId, limit, offset).then(function(comments) {
                if (cb) cb(comments);
            });
        };

        $scope.saveComment = function(data, cb) {
            CommunicationService.insertAlertComment(data).then(function(comment) {
                if (cb) cb(comment);
            });
        };

        $scope.deleteComment = function(commentId, cb) {
            CommunicationService.deleteAlertComment(commentId).then(function(status) {
                if (cb) cb(commentId);
            });
        };

        $scope.getComments($scope.alertDefinitionId, 6, 0, function(comments) {
            $scope.alertComments = comments;
        });

        /**
         * Refreshes history data with lastNDays worth of events; the range is [now - lastNDays, now]
         * Stores fetched data in allHistory array
         */
        $scope.fetchHistoryLastNDays = function(lastNDays) {
            // Set corresponding flags for highlighting of history buttons accordingly
            _.each($scope.activeHistoryButton, function(nextVal, nextKey) {
                if (lastNDays === parseInt(nextKey, 10)) {
                    $scope.activeHistoryButton[nextKey] = true;
                } else {
                    $scope.activeHistoryButton[nextKey] = false;
                }
            });

            // Note: we maintain the from timestamp in seconds but javascript time calculations are done in milliseconds
            $scope.historyFromInEpochSeconds = parseInt(((new Date().getTime()) - (lastNDays * 24 * 60 * 60 * 1000)) / 1000, 10);
            CommunicationService.getAlertHistory($scope.alertDefinitionId, $scope.historyFromInEpochSeconds).then(
                function(response) {
                    $scope.allHistory = response;
                }
            );
        };

        /**
         * Fetches 1 week's worth of data; the range is [historyFromInEpochSeconds - 7 days, historyFromInEpochSeconds, ]
         * Concats fetched data to the existing allHistory array
         */
        $scope.fetchOneMoreWeekOfHistory = function() {
            // Set corresponding flags for highlighting of history buttons accordingly
            _.each($scope.activeHistoryButton, function(nextVal, nextKey) {
                $scope.activeHistoryButton[nextKey] = false;
            });
            $scope.activeHistoryButton['-1'] = true;
            // Decrement current historyFromInEpochSeconds by 1 more week (here all times are in seconds)
            var historyToInEpochSeconds = $scope.historyFromInEpochSeconds;
            $scope.historyFromInEpochSeconds = $scope.historyFromInEpochSeconds - (7 * 24 * 60 * 60);
            CommunicationService.getAlertHistory($scope.alertDefinitionId, $scope.historyFromInEpochSeconds, historyToInEpochSeconds).then(
                function(response) {
                    // Append the additional week's
                    $scope.allHistory = $scope.allHistory.concat(response);
                }
            );
        };

        // Force evaluation of alert definition
        $scope.forceAlertEvaluation = function() {
            CommunicationService.forceAlertEvaluation($scope.alertDefinitionId)
                .then(function() {
                    FeedbackMessageService.showSuccessMessage('Evaluation of alert successfully forced...');
                }
            );
        };

        // Force cleanup of alert state
        $scope.forceAlertCleanup = function() {
            CommunicationService.forceAlertCleanup($scope.alertDefinitionId)
                .then(function() {
                    FeedbackMessageService.showSuccessMessage('Cleanup of alert state successfully forced...');
                }
            );
        };

        $scope.timestampIsOld = function(entity) {
            var now = new Date()/1000;
            var interval = $scope.checkDefinition.interval;
            if ( entity.result.ts < now - 300 && now - entity.result.ts > 3*interval) {
                return true;
            }
            return false;
        };

        /**
         * Returns the content to be displayed for this type of history entry
         * "historyType" is one of:   ALERT_{STARTED|ENDED}, ALERT_ENTITY_{STARTED|ENDED}, DOWNTIME_{STARTED|ENDED}, DOWNTIME_SCHEDULED, TRIAL_RUN_SCHEDULED,
         *                          NEW_ALERT_COMMENT, CHECK_DEFINITION_{CREATED|UPDATED}, ALERT_DEFINITION_{CREATED|UPDATED}
         * "aattributes" is
         */
        $scope.getHistoryContent = function(historyType, attributes) {
            switch (historyType) {
                case 'ALERT_STARTED':
                case 'ALERT_ENDED':
                case 'ALERT_ENTITY_STARTED':
                case 'ALERT_ENTITY_ENDED':
                    return "";
                case 'DOWNTIME_STARTED':
                case 'DOWNTIME_ENDED':
                case 'DOWNTIME_SCHEDULED':
                case 'DOWNTIME_REMOVED':
                    return attributes.comment;
                case 'ALERT_COMMENT_CREATED':
                case 'ALERT_COMMENT_REMOVED':
                    return attributes.comment;
                case 'CHECK_DEFINITION_CREATED':
                case 'CHECK_DEFINITION_UPDATED':
                    return JSON.stringify(attributes);
                case 'ALERT_DEFINITION_CREATED':
                case 'ALERT_DEFINITION_UPDATED':
                    return JSON.stringify(attributes);
                default:
                    return "N/A";
            }
        };

        $scope.HSLaFromHistoryEventTypeId = function(eventTypeId) {
            return ((eventTypeId * 6151 % 1000 / 1000.0) * 360);
        };

        if (localStorageService.get('alertDetailsSearchStr')) {
            var str = localStorageService.get('alertDetailsSearchStr');
            $scope.alertDetailsSearch.str = str;
        }

        $scope.startAlertDetailsRefresh();
    }
]);
