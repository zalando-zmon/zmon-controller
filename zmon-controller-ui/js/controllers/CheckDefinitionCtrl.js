angular.module('zmon2App').controller('CheckDefinitionCtrl', ['$scope', '$routeParams', '$location', 'MainAlertService', 'CommunicationService', 'FeedbackMessageService', 'APP_CONST', 'UserInfoService', 'LoadingIndicatorService', 'localStorageService', 'BootConfig',
    function($scope, $routeParams, $location, MainAlertService, CommunicationService, FeedbackMessageService, APP_CONST, UserInfoService, LoadingIndicatorService, localStorageService, BootConfig) {
        $scope.DefinitionsCtrl = this;
        $scope.initialLoading = true;

        $scope.$parent.activePage = 'check-definitions';
        $scope.checkDefinitions = [];
        $scope.checkDefinitionId = parseInt($routeParams.checkDefinitionId);
        $scope.teamFilter = null;
        $scope.userInfo = UserInfoService.get();
        $scope.checkJson = '';
        $scope.sortType = 'name';
        $scope.sortOrder = false;
        $scope.limit = 100;

        $scope.getMinCheckInterval = function(checkId) {
            if (BootConfig.check.minInterval.whitelistedChecks.indexOf(parseInt(checkId)) >= 0) {
                return BootConfig.check.minInterval.whitelisted;
            }
            return BootConfig.check.minInterval.normal;
        }


        var userInfo = UserInfoService.get();

        var setLinkToTrialRun = function () {
            if (typeof $scope.checkDefinition === 'undefined') return;
            var params = {
                name: $scope.checkDefinition.name,
                description: $scope.checkDefinition.description,
                owning_team: $scope.checkDefinition.owning_team,
                check_command: $scope.checkDefinition.command,
                entities: $scope.checkDefinition.entities,
                interval: $scope.checkDefinition.interval,
                technical_details: $scope.checkDefinition.technical_details
            };
            $scope.checkJson = window.encodeURIComponent(JSON.stringify(params));
        };

        this.fetchCheckDefinitions = function() {

            // Start loading animation
            LoadingIndicatorService.start();

            // Get all teams from backend to generate filter by team menu.
            CommunicationService.getAllTeams().then(
                function(data) {
                    $scope.checkTeams = data;

                    // remove saved team from local storage if it doesnt exist anymore
                    if ($scope.checkTeams.indexOf(localStorageService.get('teamFilter')) === -1) {
                        localStorageService.remove('teamFilter');
                    }
                }
            );

            if ($scope.checkDefinitionId) {
                CommunicationService.getCheckDefinition($scope.checkDefinitionId).then(function(data) {
                    $scope.checkDefinition = data;
                    CommunicationService.getAlertDefinitions(null, data.id).then(function(data) {
                        $scope.alertDefinitions = data;
                        setLinkToTrialRun();
                    });

                    // Stop loading indicator!
                    LoadingIndicatorService.stop();
                    $scope.initialLoading = false;

                });
            } else {
                CommunicationService.getCheckDefinitions($scope.teamFilter).then(function(data) {

                    $scope.checkDefinitions = data;

                    // Stop loading indicator!
                    LoadingIndicatorService.stop();
                    $scope.initialLoading = false;

                    setLinkToTrialRun();
                });
            }
        };

        // Set team filter and re-fetch check defs.
        $scope.setTeamFilter = function(team) {
            $scope.teamFilter = team ? team.split(',')[0] : null;
            $scope.DefinitionsCtrl.fetchCheckDefinitions();
            $location.search('tf', $scope.teamFilter ? $scope.teamFilter : 'all').replace();
            localStorageService.set('teamFilter', $scope.teamFilter);
            localStorageService.set('returnTo', '/#' + $location.url());
        };

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

        // Non-refreshing; one-time listing
        MainAlertService.removeDataRefresh();
        this.fetchCheckDefinitions();

        // Init page state depending on URL's query string components
        if (!_.isEmpty($location.search().cf)) {
            $scope.checkFilter = $location.search().cf;
        }

        $scope.incLimit = function() {
            $scope.limit += 20;
        };

        $scope.$watch('checkFilter', function(newVal) {
            $location.search('cf', _.isEmpty(newVal) ? null : newVal).replace();
            localStorageService.set('returnTo', '/#' + $location.url());
        });
    }
]);
