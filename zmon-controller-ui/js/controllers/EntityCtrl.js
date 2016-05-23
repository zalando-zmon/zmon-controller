angular.module('zmon2App').controller('EntityCtrl', ['$scope', '$window', '$routeParams', '$location', 'MainAlertService', 'CommunicationService', 'FeedbackMessageService', 'localStorageService', 'UserInfoService', 'LoadingIndicatorService', 'APP_CONST',
    function($scope, $window, $routeParams, $location, MainAlertService, CommunicationService, FeedbackMessageService, localStorageService, UserInfoService, LoadingIndicatorService, APP_CONST) {
        $scope.EntityCtrl = this;
        $scope.initialLoading = true;

        $scope.$parent.activePage = 'entities-alert-coverage'; // NOTE "entities" would destroy CSS
        $scope.entities = []
        $scope.sortType = 'id';
        $scope.sortOrder = false;
        $scope.limit = 100;

        var userInfo = UserInfoService.get();

        this.fetchAlertCoverage = function() {
            // Start loading animation
            LoadingIndicatorService.start();

            var entityFilter = [];

            var parts = ($scope.entityFilter || '').split(/\s+/);
            var filt = {}
            _.each(parts, function(part) {
                var keyVal = part.split(":");
                if (keyVal.length == 2) {
                filt[keyVal[0]] = keyVal[1];
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
                        entitiesById[entity.id] = {'id': entity.id, 'alerts': []};
                    }
                    entitiesById[entity.id].alerts = entitiesById[entity.id].alerts.concat(group.alerts);
                    _.each(group.alerts, function(alert) {
                    if (typeof alertsById[alert.id] === 'undefined') {
                    alert.entities = {};
                    alertsById[alert.id] = alert;

                    }
                    });
                });

            });
            _.each(entitiesById, function(v, k) {
                entities.push(v)
            });

            CommunicationService.getAlertsById(_.keys(alertsById)).then(function(data) {
            _.each(data, function(alert) {
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
    }
]);
