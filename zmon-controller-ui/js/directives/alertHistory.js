angular.module('zmon2App').directive('alertHistory', [ 'CommunicationService', function(CommunicationService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/alertHistory.html',
        scope: {
            alertId: '='
        },
        link: function(scope, elem, attrs) {
            scope.history = null;
            scope.historyFromInEpochSeconds = null;

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

            scope.HSLaFromHistoryEventTypeId = function(eventTypeId) {
                return ((eventTypeId * 6151 % 1000 / 1000.0) * 360);
            };

            scope.fetchHistoryLastNDays(1);
        }
    };
}]);
