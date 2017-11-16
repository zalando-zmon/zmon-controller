angular.module('zmon2App').controller('AlertDetailsCtrl', [ '$location', '$routeParams', '$scope', 'timespanFilter', 'CommunicationService', 'DowntimesService', 'FeedbackMessageService', 'localStorageService', 'MainAlertService', 'UserInfoService', 'APP_CONST', function($location, $routeParams, $scope, timespanFilter, CommunicationService, DowntimesService, FeedbackMessageService, localStorageService, MainAlertService, UserInfoService, APP_CONST) {

    // infinite-scroll initial limit
    $scope.limit = APP_CONST.INFINITE_SCROLL_VISIBLE_ENTITIES_INCREMENT;
    $scope.maxLimit = APP_CONST.INFINITE_SCROLL_MAX_LENGTH;

    // Set in parent scope which page is active for the menu styling
    $scope.$parent.activePage = 'alert-details'; // is not a menu option, but still set

    $scope.alertId = $routeParams.alertId;
    $scope.alert = null;
    $scope.check = null;
    $scope.entitiesNotDisplayed = 0;

    var collections = {
      activeAlerts: [],
      alertsInDowntime: [],
      checkResults: []
    };

    $scope.downtimes = [];
    $scope.downtimeEntities = [];

    $scope.commentsCount = 0;

    $scope.selection = {
        activeAlerts: true,
        alertsInDowntime: !!$location.search().downtimes || !!$location.search().showAlertsInDowntime,
        checkResults: !!$location.search().showOk || false,
    };

    $scope.sortType = 'result.start_time'; // sort list of alerts by alert start timestamp
    $scope.sortOrder = true; // newest active entities first;
    $scope.sortTypeDowntimes = 'entity';
    $scope.sortOrderDowntimes = false;
    $scope.sortTypeChildren = 'name';
    $scope.sortOrderChildren = false;

    $scope.userInfo = UserInfoService.get();

    var alertDetails = { entities: [] };

    $scope.incLimit = function() {
        if ($scope.limit >= (alertDetails.entities.length + collections.checkResults.length) || $scope.limit >= $scope.maxLimit) {
            return;
        }

        $scope.limit += APP_CONST.INFINITE_SCROLL_VISIBLE_ENTITIES_INCREMENT;
        $scope.allAlerts = getSelectedAlerts()
    };

    // Notify user when ip has been successfully copied to Clipboard.
    $scope.copyToClipboard = function(ip) {
        FeedbackMessageService.showSuccessMessage('IP Address ' + ip + ' copied to Clipboard');
    };

    // Entity Filter. Defined as object to $watch by reference on 'str' since input field is inside ui-bootstrap's tabset.
    $scope.alertDetailsSearch = {
        str: $location.search().filter || localStorageService.get('alertDetailsSearchStr') || ''
    };

    var setLinkToTrialRun = function () {
        var params = {
            name: $scope.alert.name,
            description: $scope.alert.description,
            owning_team: $scope.check.owning_team,
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
        var entitiesWithAlert = _.map(alertDetails.entities, 'entity', []);
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

    // gets meta data for new entities (where entity.metaData is undefined)
    // useful for creating aws console links
    var fetchEntityData = function(entities) {
        var entityIds = _.reduce(entities, function(result, e) {
            if (!e.entityMeta) {
                result.push( { id: e.entity })
            }
            return result;
        }, [])

        if (!entityIds || !entityIds.length) {
            return;
        }

        CommunicationService.getEntityMetaData(entityIds).then(function(response) {
            _.each(entities, function(next) {
                _.each(response, function(meta) {
                    if (meta.id === next.entity) {
                        next.entityMeta = meta;
                    }
                });
                if (!next.entityMeta) {
                    next.entityMeta = {};
                }
            });
        });
    };

    var fetchData = function(cb) {
        CommunicationService.getAlertDefinition($scope.alertId).then(function(alert) {
            $scope.alert = alert;
            CommunicationService.getCheckDefinition($scope.alert.check_definition_id).then(function(check) {
                $scope.check = check;
                if ($scope.alert.status !== 'ACTIVE') {
                    return cb();
                }
                CommunicationService.getAlertDetails($scope.alert.id).then(function (details) {
                    alertDetails = details;
                    updateCounters();
                    CommunicationService.getCheckResultsForAlert($scope.alert.id, 1).then(function(results) {
                        collections.checkResults = filterEntitiesWithAlert(results); // gets all OK entities
                        $scope.checkResultsCount = collections.checkResults.length
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
        if ($scope.alert.status !== 'ACTIVE' || alertDetails.entities.length === 0) {
            return;
        }

        collections.alertsInDowntime = [];
        collections.activeAlerts = [];

        _.each(alertDetails.entities, function(alert) {
            if (alert.result.downtimes && alert.result.downtimes.length) {
                // Add it to alertsInDowntime if any of its downtimes is active now; otherwise add it to activeAlerts
                if (DowntimesService.isAnyDowntimeNow(alert.result.downtimes)) {
                    alert.isAlertInDowntime = true;
                    collections.alertsInDowntime.push(alert);
                } else {
                    alert.isActiveAlert = true;
                    collections.activeAlerts.push(alert);
                }
            } else {
                // alert has no downtimes; by definition goes to activeAlerts
                alert.isActiveAlert = true;
                collections.activeAlerts.push(alert);
            }
        });
    };

    var updateCounters = function() {
        // apply string filter if present
        var subset = _.filter(alertDetails.entities, function(o) {
            return o.entity.indexOf($scope.alertDetailsSearch.str || o.entity) !== -1
        });

        // get entities in downtime
        var downtimes = subset.filter(function (e) {
            return e.result && e.result.downtimes
                && e.result.downtimes.length
                && DowntimesService.isAnyDowntimeNow(e.result.downtimes)});

        $scope.allDowntimeAlertsCount = downtimes.length || 0;
        $scope.allActiveAlertsCount = subset.length - $scope.allDowntimeAlertsCount;
    }

    // return an array of entities to display on the table (ng-repeat on allAlerts)
    var getSelectedAlerts = function() {
        var selected = 0;   // number of active tabs (alerts, downtime, OK)

        // Concatenates collections of entities into one collection for display
        // uses 'selected' to share scroll limit between selected collections.
        var alerts = _.reduce(['activeAlerts', 'alertsInDowntime', 'checkResults'], function(result, a) {
            if (!$scope.selection[a] || !collections[a] || !collections[a].length) {
                return result;
            }
            selected = selected+1;
            var entities = _.filter(collections[a], function(o) {
                return o.entity.indexOf($scope.alertDetailsSearch.str || o.entity) !== -1
            })
            return result.concat(entities.slice(0, $scope.limit/selected));
        }, []);

        fetchEntityData(alerts); // append metaData
        $scope.entitiesNotDisplayed = (alertDetails.entities.length + collections.checkResults.length) - $scope.allAlerts.length;
        return alerts;
    };

    var getSelectedDowntimeEntities = function() {
        return _.filter($scope.downtimeEntities, function(entity) {
            return _.map($scope.allAlerts, 'entity').indexOf(entity) !== -1;
        }, []);
    };

    var refresh = function() {
        fetchData(function() {
            $scope.entitiesFilter = getEntities($scope.alert.entities, $scope.check.entities);
            $scope.entitiesExcludeFilter = getEntities($scope.alert.entities_exclude, $scope.check.entities_exclude);
            setAlertStates();
            $scope.allAlerts = getSelectedAlerts();
            $scope.downtimeEntities = getSelectedDowntimeEntities();
            setLinkToTrialRun();
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
        $location.search('filter', str);
        localStorageService.set('alertDetailsSearchStr', str);
        if ($scope.alert) {
            $scope.limit = APP_CONST.INFINITE_SCROLL_VISIBLE_ENTITIES_INCREMENT;
            $scope.allAlerts = getSelectedAlerts()
            updateCounters();
        }
    });

    // start polling data
    MainAlertService.startDataRefresh('AlertDetailsCtrl', _.bind(refresh, this), APP_CONST.ALERT_DETAILS_REFRESH_RATE, true);
}]);
