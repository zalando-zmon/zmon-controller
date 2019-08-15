angular.module('zmon2App').controller('ChangesCtrl', ['$scope', '$location', '$route', 'APP_CONST', 'CommunicationService', 'MainAlertService', 'UserInfoService', 'FeedbackMessageService',
    function($scope, $location, $route, APP_CONST, CommunicationService, MainAlertService, UserInfoService, FeedbackMessageService) {

    MainAlertService.removeDataRefresh();
    $scope.$parent.activePage = 'changes';

    $scope.limit = APP_CONST.INFINITE_SCROLL_VISIBLE_ENTITIES_INCREMENT;
    $scope.definition = {};
    $scope.changes = [];
    $scope.activeChangesButton = 7;

    var q = $location.search();

    if (q.alert_definition_id) {
        $scope.alert_definition_id = q.alert_definition_id;
    }

    // Preventing execution of code if check or alert definition are not defined or values are invalid
    if((!q.check_definition_id || isNaN(q.check_definition_id)) && (!q.alert_definition_id || isNaN(q.alert_definition_id))) {
        var search = Object.keys(q).map(function(key) { return key + '=' + q[key]; }).join('&');
        FeedbackMessageService.showErrorMessage("The requested [GET changes?" + search + "] is invalid. You'll be redirected to the previous page.", 3000, function() {
            window.history.back();
        });

        return;
    }

    // Fn. which storing the check definition data into global $scope.definition
    var checkHandler = function(def) {
        $scope.definition = def;
    };

    // Fn. which appending check definition into alert definition and then storing the alert definition data into global $scope.definition
    var alertHandler = function(def) {
        $scope.definition = def;

        CommunicationService.getCheckDefinition(def.check_definition_id).then(function(cd) {
            $scope.definition.check_definition = cd;
        });
    };

    // Converting original changed_attributes obj into array which is more suitable for searching
    var convertChanges = function(changes) {
        changes.forEach(function(change) {
            if (change.changed_attributes) {
                change.changed_attributes_list = Object.keys(change.changed_attributes).map(function(key) {
                    return { attribute: key, value: change.changed_attributes[key] };
                });
            }
        });

        return changes;
    };

    $scope.mode = q.check_definition_id ? 'check' : 'alert';

    // Building default query
    q.limit = q.limit || 20 * $scope.limit;
    q.from = q.from || parseInt( (Date.now() / 1000) - ($scope.activeChangesButton * 60 * 60 * 24) , 10);

    // Loading the check/alert definition
    if($scope.mode === 'check') {
        CommunicationService.getCheckDefinition(q.check_definition_id).then(checkHandler);
    } else {
        CommunicationService.getAlertDefinition(q.alert_definition_id).then(alertHandler);
    }

    // Loading the history of changes
    CommunicationService.getAllChanges(q).then(function(changes) {
        $scope.changes = convertChanges(changes);
    });

    // Updating changes data after applying the days filter
    $scope.fetchChanges = function(days) {
        $scope.activeChangesButton = days;
        q.from = parseInt( (Date.now() / 1000) - (days * 60 * 60 * 24) , 10);
        CommunicationService.getAllChanges(q).then(function(changes) {
            $scope.changes = convertChanges(changes);
        });
    };

    // Return user-friendly HTML diff based on a plaintext unified diff
    $scope.getHtmlDiff = function(unifiedDiff) {
        return Diff2Html.getPrettyHtml(unifiedDiff, {
            rawTemplates: {
                'generic-wrapper': '{{{content}}}',
                'line-by-line-numbers': '<div class="line-num2">{{newNumber}}</div>',
                'line-by-line-file-diff': '    <div class="d2h-file-diff">' +
                                          '        <div class="d2h-code-wrapper">' +
                                          '            <table class="d2h-diff-table">' +
                                          '                <tbody class="d2h-diff-tbody">' +
                                          '                {{{diffs}}}' +
                                          '                </tbody>' +
                                          '            </table>' +
                                          '        </div>' +
                                          '    </div>'
            }
        });
    };

    // Counting the size of history changes
    $scope.total = function() {
        return $scope.changes.length;
    };

    // Escaping value (e.g. empty_str => "", null => 'null')
    $scope.escapeValue = function(value) {
        var tagsToReplace = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;'
        };

        function replaceTag(tag) {
            return tagsToReplace[tag] || tag;
        }

        if (typeof value === 'undefined') {
            return value;
        } else if (value === null) {
            return 'null';
        } else {
            return (value.length > 0) ? value.replace(/[&<>]/g, replaceTag) : '""';
        }
    };

    // Calculating the background color of event labels
    $scope.hslFromEventType = function(id) {
        return "hsla(" + ((id * 6151 % 1000 / 1000.0) * 360) + ", 50%, 50%, 1);";
    };

    $scope.restoreCheckDefinition = function(checkDefinitionHistoryId) {
        CommunicationService.restoreCheckDefinition(checkDefinitionHistoryId)
            .then(function(isRestored) {
                if (isRestored) {
                    FeedbackMessageService.showSuccessMessage("Check definition has been successfully restored.", 1000, $route.reload);
                }
                else {
                    FeedbackMessageService.showErrorMessage("Check definition could not be restored.")
                }
            })
    };

    $scope.isDiffEmpty = function(unifiedDiff) {
        return unifiedDiff.split("\n").length < 3;
    };
}]);
