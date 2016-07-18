angular.module('zmon2App').directive('alertHistory', [ 'CommunicationService', function(CommunicationService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/alertHistory.html',
        scope: {
            alertId: '='
        },
        link: function(scope, elem, attrs) {
            scope.history = null;

            // History button active
            scope.activeHistoryButton = {
                "1": false,
                "7": false,
                "14": false,
                "-1": false
            };

            /**
             * Refreshes history data with lastNDays worth of events; the range is [now - lastNDays, now]
             * Stores fetched data in history array
             */
            scope.fetchHistoryLastNDays = function(lastNDays) {
                // Set corresponding flags for highlighting of history buttons accordingly
                _.each(scope.activeHistoryButton, function(nextVal, nextKey) {
                    if (lastNDays === parseInt(nextKey, 10)) {
                        scope.activeHistoryButton[nextKey] = true;
                    } else {
                        scope.activeHistoryButton[nextKey] = false;
                    }
                });

                // we maintain the from timestamp in seconds but javascript time calculations are done in milliseconds
                scope.historyFromInEpochSeconds = parseInt(((new Date().getTime()) - (lastNDays * 24 * 60 * 60 * 1000)) / 1000, 10);
                CommunicationService.getAlertHistory(scope.alertId, scope.historyFromInEpochSeconds).then(
                    function(response) {
                        scope.history = response;
                    }
                );
            };

            /**
             * Fetches 1 week's worth of data; the range is [historyFromInEpochSeconds - 7 days, historyFromInEpochSeconds, ]
             * Concats fetched data to the existing history array
             */
            scope.fetchOneMoreWeekOfHistory = function() {
                // Set corresponding flags for highlighting of history buttons accordingly
                _.each(scope.activeHistoryButton, function(nextVal, nextKey) {
                    scope.activeHistoryButton[nextKey] = false;
                });
                scope.activeHistoryButton['-1'] = true;
                // Decrement current historyFromInEpochSeconds by 1 more week (here all times are in seconds)
                var historyToInEpochSeconds = scope.historyFromInEpochSeconds;
                scope.historyFromInEpochSeconds = scope.historyFromInEpochSeconds - (7 * 24 * 60 * 60);
                CommunicationService.getAlertHistory(scope.alertId, scope.historyFromInEpochSeconds, historyToInEpochSeconds).then(
                    function(response) {
                        // Append the additional week's
                        scope.history = scope.history.concat(response);
                    }
                );
            };

            scope.historyFromInEpochSeconds = null;
            /**
             * Returns the content to be displayed for this type of history entry
             * "historyType" is one of:   ALERT_{STARTED|ENDED}, ALERT_ENTITY_{STARTED|ENDED}, DOWNTIME_{STARTED|ENDED}, DOWNTIME_SCHEDULED, TRIAL_RUN_SCHEDULED,
             *                          NEW_ALERT_COMMENT, CHECK_DEFINITION_{CREATED|UPDATED}, ALERT_DEFINITION_{CREATED|UPDATED}
             * "aattributes" is
             */
            scope.getHistoryContent = function(historyType, attributes) {
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

            scope.HSLaFromHistoryEventTypeId = function(eventTypeId) {
                return ((eventTypeId * 6151 % 1000 / 1000.0) * 360);
            };

            scope.fetchHistoryLastNDays(1);
        }
    };
}]);
