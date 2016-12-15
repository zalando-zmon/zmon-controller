angular.module('zmon2App').controller('EntityCtrl', ['$scope', '$window', '$routeParams', '$location', 'timespanFilter', 'MainAlertService', 'CommunicationService', 'localStorageService', 'UserInfoService', 'LoadingIndicatorService', 'APP_CONST',
    function($scope, $window, $routeParams, $location, timespanFilter, MainAlertService, CommunicationService, localStorageService, UserInfoService, LoadingIndicatorService, APP_CONST) {
        $scope.EntityCtrl = this;
        $scope.initialLoading = true;

        $scope.$parent.activePage = 'entities-page'; // NOTE "entities" would destroy CSS
        $scope.entities = [];
        $scope.teamFilter = null;
        $scope.sortType = 'id';
        $scope.sortOrder = false;
        $scope.limit = 100;
        $scope.filterSuggestions = [];

        var userInfo = UserInfoService.get();

        $scope.timeAgo = function(epochPastTs) {
            var timeIntervalSinceLastUpdate = MainAlertService.millisecondsApart(epochPastTs, MainAlertService.getLastUpdate());
            return timespanFilter(timeIntervalSinceLastUpdate);
        };

        $scope.numberNiceFormat = function(v) {
            var ndx = Math.floor( Math.log(v) / Math.log(1000) );
            var suffix = ["", "k", "M", "G", "T", "P"];
            return  (v / Math.pow(1000, ndx)).toFixed(1) + suffix[ndx];
        };

        $scope.getDisplayValue = function(r) {
            if (!r.result) return (r.status == 'inactive' ? 'OK' : '?');
            var v = null;
            if (Object.keys(r.result.captures).length == 1) {
                var t = r.result.captures[Object.keys(r.result.captures)[0]];
                if (typeof t == "number") {
                    v = t;
                }
            }

            if(null == v && typeof r.result.value == "number") {
                v = r.result.value;
            }

            if (null == v && Object.keys(r.result.value).length == 1) {
                v = r.result.value[Object.keys(r.result.value)[0]];
            }

            if (null != v && typeof v == "number") {
                if (Math.abs(v) <= 1) {
                    return (v * 100).toFixed(0) + "%";
                }
                else {
                    return $scope.numberNiceFormat(v);
                }
            }

            return $scope.timeAgo(r.result.start_time);
        };

        $scope.formatResult = function(result) {
            if (!_.isEmpty(result.captures)) {
                var s = '';
                _.each(result.captures, function(v, k) {
                    s += k + ': ' + JSON.stringify(v).slice(0, 100);
                });
                return s;
            } else {
                return 'value: ' + JSON.stringify(result.value).slice(0, 100);
            }
        };
        // Set team filter and re-fetch alerts
        $scope.setTeamFilter = function(team) {
            $scope.teamFilter = team ? team.split(',')[0] : null;
            $scope.EntityCtrl.fetchAlertCoverage();
            $location.search('tf', $scope.teamFilter ? $scope.teamFilter : 'all');
            localStorageService.set('teamFilter', $scope.teamFilter);
            localStorageService.set('returnTo', '/#' + $location.url());
        };

        this.fetchEntityProperties = function() {
            CommunicationService.getEntityProperties().then(function(data) {
                var suggestions = {};
                _.each(data, function(props, entityType) {
                    _.each(props, function(values, name) {
                        _.each(values, function(val) {
                            suggestions[name + ':' + val] = null;
                        });
                    });
                });
                $scope.filterSuggestions = _.keys(suggestions).sort();
            });
        };

        // fetch entities and their alert coverage, but filtered by given set of alert IDs
        this.fetchAlertCoverageFiltered = function(alertsToShow) {
            var entityFilter = [];

            var parts = ($scope.entityFilter || '').split(/\s+/);
            var filt = {}
            _.each(parts, function(part) {
                var keyVal = part.split(":");
                if (keyVal.length >= 2) {
                    filt[keyVal[0]] = keyVal.splice(1).join(':');
                }
            });
            if (!_.isEmpty(filt)) {
                entityFilter.push(filt);
            }

            var entities = [];
            var alertsById = {};

            CommunicationService.getAlertCoverage(entityFilter).then(function(data) {
                var entitiesById = {};
                _.each(data, function(group) {
                    _.each(group.entities, function(entity) {
                        if (typeof entitiesById[entity.id] === 'undefined') {
                            entitiesById[entity.id] = {'id': entity.id, 'type': entity.type, 'alerts': []};
                        }
                        entitiesById[entity.id].alerts = entitiesById[entity.id].alerts.concat(_.filter(group.alerts, function(a) {
                            return alertsToShow === null || alertsToShow[a.id];
                        }));
                        _.each(group.alerts, function(alert) {
                            if (typeof alertsById[alert.id] === 'undefined') {
                                alert.definition = null;
                                alertsById[alert.id] = alert;
                            }
                            if (typeof alert.entities === 'undefined') {
                                alert.entities = {};
                            }
                            alert.entities[entity.id] = {'status': 'unknown'}
                        });
                    });
                });
                _.each(entitiesById, function(v, k) {
                    entities.push(v)
                });


                // load alert state for all alert IDs (also returns alert definition)
                CommunicationService.getAlertsById(_.keys(alertsById)).then(function(data) {
                    _.each(data, function(alert) {
                        alertsById[alert.alert_definition.id].definition = alert.alert_definition;
                        _.each(alert.entities, function(entity) {
                            entity.status = entity.result.downtimes.length > 0 ? 'downtimed' : 'active';
                            alertsById[alert.alert_definition.id].entities[entity.entity] = entity;
                        });
                    });
                    // now set all remaining alerts to "inactive"
                    _.each(entities, function(row) {
                        _.each(row.alerts, function(alert) {
                            _.each(alert.entities, function(entity, entityId) {
                                if (entity.status == 'unknown') {
                                    entity.status = 'inactive';
                                }
                            });
                        });
                    });
                });

                $scope.entities = entities;
                // Stop loading indicator!
                LoadingIndicatorService.stop();
            });
        };


        this.fetchAlertCoverage = function() {
            // Start loading animation
            LoadingIndicatorService.start();

            // Get all teams from backend to generate filter by team menu.
            CommunicationService.getAllTeams().then(
                function(data) {
                    $scope.alertTeams = data;

                    // remove saved team from local storage if it doesnt exist anymore
                    if ($scope.alertTeams.indexOf(localStorageService.get('teamFilter')) === -1) {
                        localStorageService.remove('teamFilter');
                    }
                }
            );

            if ($scope.teamFilter) {
                // NOTE: this call is loading more than necessary (whole alert definitions),
                // we only collect the alert IDs
                CommunicationService.getAlertDefinitions($scope.teamFilter, null).then(function(data) {
                    // "set" of alert IDs to show (filtered by team)
                    var alertsToShow = {};
                    _.each(data, function(alert) {
                        alertsToShow[alert.id] = true;
                    });
                $scope.EntityCtrl.fetchAlertCoverageFiltered(alertsToShow);
            });
            } else {
                $scope.EntityCtrl.fetchAlertCoverageFiltered(null);
            }
        };

        // Non-refreshing; one-time listing
        MainAlertService.removeDataRefresh();

        this.fetchEntityProperties();

        // Set team filter on load from userInfo
        if (!_.isEmpty(userInfo.teams)) {
            $scope.teamFilter = userInfo.teams.split(',')[0];
        }

        // Override teamFilter if it was saved in localStorage
        if (localStorageService.get('teamFilter')) {
            $scope.teamFilter = localStorageService.get('teamFilter');
        }

        // Override teamFilter if specified on queryString
        if ($location.search().tf) {
            var tf = $location.search().tf === 'all' ? null : $location.search().tf;
            $scope.teamFilter = tf;
        }

        // Init page state depending on URL's query string components
        if (!_.isEmpty($location.search().ef)) {
            $scope.entityFilter = $location.search().ef;
        }

        $scope.incLimit = function() {
            $scope.limit += 20;
        };

        $scope.$watch('entityFilter', function(newVal) {
            $location.search('ef', _.isEmpty(newVal) ? null : newVal);
            localStorageService.set('returnTo', '/#' + $location.url());
            $scope.EntityCtrl.fetchAlertCoverage();
        });

        // we have reloadOnSearch=false, so watch for route changes
        $scope.$on('$routeUpdate', function(event) {
            $scope.entityFilter = $location.search().ef;
        });
    }
]);
