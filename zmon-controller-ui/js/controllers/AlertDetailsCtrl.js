angular.module('zmon2App').controller('AlertDetailsCtrl', [ '$location', '$routeParams', '$scope', 'timespanFilter', 'CommunicationService', 'DowntimesService', 'localStorageService', 'MainAlertService', 'UserInfoService', 'APP_CONST', function($location, $routeParams, $scope, timespanFilter, CommunicationService, DowntimesService, localStorageService, MainAlertService, UserInfoService, APP_CONST) {

    // Set in parent scope which page is active for the menu styling
    $scope.$parent.activePage = 'alert-details'; // is not a menu option, but still set

    $scope.alertId = $routeParams.alertId;
    $scope.alert = null;
    $scope.check = null;
    $scope.downtimes = [];

    $scope.downtimeEntities = [];

    $scope.commentsCount = 0;

    $scope.selection = {
        activeAlerts: true,
        alertsInDowntime: !!$location.search().downtimes,
        checkResults: false,
    };

    $scope.sortType = 'result.start_time'; // sort list of alerts by alert start timestamp
    $scope.sortOrder = true; // newest active entities first;
    $scope.sortTypeDowntimes = 'entity';
    $scope.sortOrderDowntimes = false;
    $scope.sortTypeChildren = 'name';
    $scope.sortOrderChildren = false;

    $scope.userInfo = UserInfoService.get();

    // Entity Filter. Defined as object to $watch by reference on 'str' since input field is inside ui-bootstrap's tabset.
    $scope.alertDetailsSearch = {
        str: localStorageService.get('alertDetailsSearchStr') || ''
    };

    var setLinkToTrialRun = function () {
        var params = {
            name: $scope.alert.name,
            description: $scope.alert.description,
            check_command: $scope.check.command,
            alert_condition: $scope.alert.condition,
            entities: $scope.entitiesFilter,
            entities_exclude: $scope.entitiesExcludeFilter,
            interval: $scope.check.interval,
            period: $scope.alert.period,
            parameters: $scope.alert.parameters || []
        };
        $scope.alertJson = window.encodeURIComponent(JSON.stringify(params));
    };

    var filterEntitiesWithAlert = function(results) {
        var entitiesWithAlert = _.map($scope.alert.details.entities, 'entity', []);
        return _.map(_.filter(results, function(entityRes) {
                return (entitiesWithAlert.indexOf(entityRes.entity) === -1);
            }), function(entityRes) {
                return {
                    'entity': entityRes.entity,
                    'result': entityRes.results[0],
                    'isCheckResult': true
                };
            });
    };

    var fetchEntityData = function(entities) {
        var entitiesId = _.map(_.map(entities, 'entity'), function(id) {
            return { 'id': id };
        }, []);
        if (!entitiesId.length) {
            return;
        }
        CommunicationService.getEntityMetaData(entitiesId).then(function(response) {
            _.each(entities, function(next) {
                _.each(response, function(meta) {
                    if (meta.id === next.entity) {
                        next.entityMeta = meta;
                    }
                });
            });
        });
    };

    var fetchData = function(cb) {
        CommunicationService.getAlertDefinition($scope.alertId).then(function(alert) {
            $scope.alert = alert;
            CommunicationService.getCheckDefinition($scope.alert.check_definition_id).then(function(check) {
                $scope.check = check;
                CommunicationService.getAlertDetails($scope.alert.id).then(function (details) {
                    $scope.alert.details = details;
                    fetchEntityData($scope.alert.details.entities);
                    CommunicationService.getCheckResultsForAlert($scope.alert.id, 1).then(function(results) {
                        $scope.checkResults = filterEntitiesWithAlert(results);
                        fetchEntityData($scope.checkResults);
                        cb();
                    });
                });
            });
            CommunicationService.getAlertDefinitionChildren($scope.alert.id).then(function(children) {
                $scope.alert.children = children;
            });
            CommunicationService.getDowntimes($scope.alert.id).then(function(downtimes) {
                $scope.downtimes = downtimes;
            });
            CommunicationService.getAlertComments($scope.alert.id, 11, 0).then(function(comments) {
                $scope.commentsCount = comments.length;
            });
            if ($scope.alert.parent_id) {
                CommunicationService.getAlertDefinition($scope.alert.parent_id).then(function(parent) {
                    $scope.alert.parent = parent;
                });
            }
        });
    };

    var getEntities = function(alertEntities, checkEntities) {
        var entities = [];

        if (_.size(alertEntities) < 1) {
            entities = checkEntities;
        } else if (_.size(checkEntities) < 1) {
            entities = alertEntities;
        } else {
            _.each(checkEntities, function (c) {
                _.each(alertEntities, function (a) {
                    var e = _.extend({}, c, a);
                    if (e.type && e.type !== '') {
                        entities.push(e);
                    }
                });
            });
        }

        // Remove entity filter duplicates!
        entities = _.uniq(entities, false, function(eFilter) {
            return JSON.stringify(eFilter, null, 0);
        });

        return entities;
    };

    var setAlertStates = function() {
        if ($scope.alert.status !== 'ACTIVE') {
            return;
        }
        $scope.alertsInDowntime = [];
        $scope.activeAlerts = [];
        _.each($scope.alert.details.entities, function(alert) {
            if (alert.result.downtimes && alert.result.downtimes.length) {
                // Add it to alertsInDowntime if any of its downtimes is active now; otherwise add it to activeAlerts
                if (DowntimesService.isAnyDowntimeNow(alert.result.downtimes)) {
                    alert.isAlertInDowntime = true;
                    $scope.alertsInDowntime.push(alert);
                } else {
                    alert.isActiveAlert = true;
                    $scope.activeAlerts.push(alert);
                }
            } else {
                // alert has no downtimes; by definition goes to activeAlerts
                alert.isActiveAlert = true;
                $scope.activeAlerts.push(alert);
            }
        });
    };

    var getSelectedAlerts = function() {
        return _.reduce(['activeAlerts', 'alertsInDowntime', 'checkResults'], function(result, a) {
            return $scope.selection[a] && $scope[a] && $scope[a].length ? result.concat($scope[a]) : result;
        }, []);
    };

    var getSelectedDowntimeEntities = function() {
        return _.filter($scope.downtimeEntities, function(entity) {
            return _.map($scope.allAlerts, 'entity').indexOf(entity) !== -1;
        }, []);
    };

    var refresh = function() {
        fetchData(function() {
            setLinkToTrialRun();
            $scope.entitiesFilter = getEntities($scope.alert.entities, $scope.check.entities);
            $scope.entitiesExcludeFilter = getEntities($scope.alert.entities_exclude, $scope.check.entities_exclude);
            setAlertStates();
            $scope.allAlerts = getSelectedAlerts();
            $scope.downtimeEntities = getSelectedDowntimeEntities();
        });
    };

    // Force evaluation of alert definition
    $scope.forceAlertEvaluation = function() {
        CommunicationService.forceAlertEvaluation($scope.alertId).then(function() {
            FeedbackMessageService.showSuccessMessage('Evaluation of alert successfully forced...');
        });
    };

    // Force cleanup of alert state
    $scope.forceAlertCleanup = function() {
        CommunicationService.forceAlertCleanup($scope.alertId).then(function() {
            FeedbackMessageService.showSuccessMessage('Cleanup of alert state successfully forced...');
        });
    };

    $scope.toggleAllAlertsForDowntime = function() {
        $scope.downtimeEntities = $scope.downtimeEntities.length ? [] : _.map($scope.allAlerts, 'entity');
    };

    $scope.toggleAlertForDowntime = function(id) {
        var i = $scope.downtimeEntities.indexOf(id);
        return i === -1 ? $scope.downtimeEntities.push(id) : $scope.downtimeEntities.splice(i, 1);
    };

    $scope.areAllAlertsMarkedForDowntime = function() {
        return $scope.allAlerts.length && JSON.stringify($scope.downtimeEntities.sort()) === JSON.stringify(_.map($scope.allAlerts, 'entity').sort());
    };

    $scope.timestampIsOld = function(entity) {
        var now = new Date()/1000;
        var interval = $scope.check.interval;
        if ( entity.result.ts < now - 300 && now - entity.result.ts > 3*interval) {
            return true;
        }
        return false;
    };

    $scope.$watch('selection', function(s) {
        $scope.allAlerts = getSelectedAlerts();
        $scope.downtimeEntities = getSelectedDowntimeEntities();
    }, true);

    $scope.$watch('alertDetailsSearch.str', function(str) {
        localStorageService.set('alertDetailsSearchStr', str);
    });

    // start polling data
    MainAlertService.startDataRefresh('AlertDetailsCtrl', _.bind(refresh, this), APP_CONST.ALERT_DETAILS_REFRESH_RATE, true);
}]);
