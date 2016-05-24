angular.module('zmon2App').controller('EntityCtrl', ['$scope', '$window', '$routeParams', '$location', 'timespanFilter', 'MainAlertService', 'CommunicationService', 'localStorageService', 'LoadingIndicatorService', 'APP_CONST',
    function($scope, $window, $routeParams, $location, timespanFilter, MainAlertService, CommunicationService, localStorageService, LoadingIndicatorService, APP_CONST) {
        $scope.EntityCtrl = this;
        $scope.initialLoading = true;

        $scope.$parent.activePage = 'entities-alert-coverage'; // NOTE "entities" would destroy CSS
        $scope.entities = []
        $scope.sortType = 'id';
        $scope.sortOrder = false;
        $scope.limit = 100;
        $scope.filterSuggestions = [];

        $scope.timeAgo = function(epochPastTs) {
            var timeIntervalSinceLastUpdate = MainAlertService.millisecondsApart(epochPastTs, MainAlertService.getLastUpdate());
            return timespanFilter(timeIntervalSinceLastUpdate);
        };
        $scope.formatCaptures = function(captures) {
            var s = '';
            _.each(captures, function(v, k) {
                s += k + ': ' + v;
            });
            return s;
        };

        this.fetchEntityProperties = function() {
            CommunicationService.getEntityProperties().then(function(data) {
                var sugg = {};
                _.each(data, function(props, entityType) {
                    _.each(props, function(values, name) {
                        _.each(values, function(val) {
                            sugg[name + ':' + val] = null;
                        });
                    });
                });
                sugg = _.keys(sugg);
                sugg.sort();
                $scope.filterSuggestions = sugg;
            });
        };


        this.fetchAlertCoverage = function() {
            // Start loading animation
            LoadingIndicatorService.start();

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
                        entitiesById[entity.id].alerts = entitiesById[entity.id].alerts.concat(group.alerts);
                        _.each(group.alerts, function(alert) {
                            if (typeof alertsById[alert.id] === 'undefined') {
                                alert.entities = {};
                                alert.definition = null;
                                alertsById[alert.id] = alert;
                            }
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
                            alertsById[alert.alert_definition.id].entities[entity.entity] = entity;
                        });
                    });
                });

                $scope.entities = entities;
                // Stop loading indicator!
                LoadingIndicatorService.stop();
            });
        };

        // Non-refreshing; one-time listing
        MainAlertService.removeDataRefresh();

        this.fetchEntityProperties();

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
