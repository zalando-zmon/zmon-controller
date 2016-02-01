angular.module('zmon2App').controller('CloudCtrl', ['$scope', '$interval', '$location', 'CommunicationService','MainAlertService', 'LoadingIndicatorService', 
    function($scope, $interval, $location, CommunicationService, MainAlertService, LoadingIndicatorService) {

        // Set 'Cloud' tab as active
        $scope.$parent.activePage = 'cloud';

        // Non-refreshing; one-time listing
        MainAlertService.removeDataRefresh();

        // infrastructure Teams and Applications (some times called Entities)
        $scope.teams = {};
        $scope.applications = {};

        // current selections
        $scope.selectedTeam = null;
        $scope.selectedApplication = null;
        $scope.selectedEndpoint = null;

        // flag true if tracked apps are present
        $scope.trackedApplications = false;

        // loading indicator
        LoadingIndicatorService.start();
        $scope.loading = true;

        // fetch interval id
        var fetchInterval = null;

        // 5min polling interval
        var INTERVAL = 300000;

        // returns entity name for a given appId
        var getEntityName = function(appId, teamId) {
            var entityName = null;
            _.each($scope.teams[teamId].instances, function(instance) {
                if (instance.id === appId) {
                    entityName = instance.application_id;
                }
            });
            return entityName;
        }

        // determines if an app contains error code responses (4XX, 5XX, etc)
        var hasErrorCodes = function(app) {
            var errd = false;
            _.each(jsonPath(app[0].value, '$[*][*]'), function(o) {
                _.each(_.keys(o), function(key) {
                    if (key[0] !== '2') {
                        errd = true;
                    }
                });
            });
            return errd;
        }

        // find team name from aws id last 3 digits, i.e.  701 -> stups
        var getTeamNameFromAwsId = function(awsId) {
            var name = null;
            _.each($scope.teams, function(t) {
                if (t.awsId === awsId) {
                    name = t.name;
                }
            });
            if (name) {
                return name;
            }

            return null;
        };

        // set view state from URL
        var setStateFromUrl = function() {
            if ($location.search().app) {
                $scope.selectedTeam = $location.search().team;
                $scope.selectedApplication = $location.search().app;
            } else if ($location.search().team) {
                $scope.setTeam($location.search().team);
            } else {
                $scope.setTeam();
            }
        }

        // start data fetch intervals
        var startFetchInterval = function() {
            fetchInterval = $interval(fetchCloudData, INTERVAL);
        };

        // stop data fetch intervals
        var stopFetchInterval = function() {
            $interval.cancel(fetchInterval);
            fetchInterval = null;
        };

        // gets all data from a specified return code (i.e. 2XX) in the provided json
        // returns an array with all the values
        var getResponseCodeValues = function(jp, code, tag) {
            var found = [];
            _.each(jp, function(r) {
                _.each(r, function(rc) {
                    _.each(rc, function(s, c) {
                        var regExp = new RegExp('^' + String(code)[0]);
                        if (regExp.test(c) && s[tag]) {
                            found.push(s[tag]);
                        }
                    });
                });
            });
            return found;
        };

        // analyze incoming data per response and aggregate it accordingly
        var aggregateResults = function(app) {
            var jp = jsonPath(app, "$.metrics[*][*].");

            var r200 = getResponseCodeValues(jp, 200, 'mRate');
            var r400 = getResponseCodeValues(jp, 400, 'mRate');
            var r500 = getResponseCodeValues(jp, 500, 'mRate');
            var rt2XX = getResponseCodeValues(jp, 200, '99th');
            var rtXXX = getResponseCodeValues(jp, 500, '99th');

            var r = app.results = {};

            var r = app.results = {
                r200: _.isEmpty(r200) ? 0 : Math.round(_.reduce(r200, function(m, n) { return m+n; }, 0)*60),
                r400: _.isEmpty(r400) ? 0 : Math.round(_.reduce(r400, function(m, n) { return m+n; }, 0)*60),
                r500: _.isEmpty(r500) ? 0 : Math.round(_.reduce(r500, function(m, n) { return m+n; }, 0)*60),
                rt2XX: _.isEmpty(rt2XX) ? 0 : Math.round(_.max(rt2XX)),
                rtXXX: _.isEmpty(rtXXX) ? 0 : Math.round(_.max(rtXXX)),
                einstances: app.einstances,
                instances: app.instances,
                error_style: r.epercent>1.5 ? 'app_error' : ''
            };

            app.results.epercent = (((r.r400+r.r500)/(r.r200+r.r400+r.r500))*100).toFixed(1);
            app.results.requests = (r.r200+r.r400+r.r500).toFixed(2);
        };

        // fetch application data for a given Team
        var fastFetchTeamApps = function(teamId) {
            var team = $scope.teams[teamId];
            var awsTeamId = $scope.teams[teamId].awsId;
            CommunicationService.getCheckResultsFiltered(2132, awsTeamId).then(function(metrics) {
                _.each(team.applications, function(app) {
                    app.instances = 0;
                    app.einstances = 0;
                    app.metrics = [];
                    _.each(metrics.values, function(instance, instId) {
                        var instName = getEntityName(instId, teamId);
                        if (instName && app.application_id === instName && instance[0] && typeof instance[0].value === typeof {}) {
                            app.instances += 1;
                            app.einstances = hasErrorCodes(instance) ? app.einstances + 1 : app.einstances;
                            app.metrics.push(instance[0].value);
                        }
                    });
                    aggregateResults(app);
                });

                LoadingIndicatorService.stop();
                $scope.loading = false;
            })
        }

        // set teams object with awsId as keys
        var createTeams = function(teams) {
            _.each(teams, function(team) {
                team.awsId = team.infrastructure_account.slice(-3);
                team.instances = [];
                team.applications = [];
                team.elbs = [];
                team.name = team.account_alias;
                if (team.account_alias.slice(0, 8) === 'zalando-') {
                    team.name = team.account_alias.split('zalando-')[1];
                }
                $scope.teams[team.name] = team;
            });
        }

        // create application object and add to belonging team
        var createApplications = function(applications) {
            _.each(applications, function(app) {
                var teamAwsId = app.id.split(':')[1].slice(-3);
                app.team = getTeamNameFromAwsId(teamAwsId);
                app.instances = 0;
                app.einstances = 0;
                app.metrics = [];
                $scope.applications[app.application_id] = app;
                if (app.team && $scope.teams[app.team]) {
                    $scope.teams[app.team].applications.push(app);
                }
            });
        }

        // create instances object and add to belonging team
        var createInstances = function(instances) {
            _.each(instances, function(instance) {
                var teamId = getTeamNameFromAwsId(instance.id.split(":")[1].slice(-3));
                if ($scope.teams[teamId] !== undefined) {
                    $scope.teams[teamId].instances.push(instance);
                } 
            });
        }

        // var create elbs object and add to belonging team
        var createElbs = function(elbs) {
            _.each(elbs, function(elb) {
                var teamAwsId = elb.id.split(':')[1].slice(-3);
                elb.team = getTeamNameFromAwsId(teamAwsId);
                if (elb.team && $scope.teams[elb.team]) {
                    $scope.teams[elb.team].elbs.push(elb);
                }
            });
        }

        // set app on URL and let children controller CloudEntitirsCtrl take action
        $scope.showApp = function(appId) {
            $scope.selectedApplication = appId;
            $location.search('app', appId);
        };

        // return number of ELBs for a given 'team' which are public.
        $scope.getPublicElbsByTeam = function(team) {
            var i = 0
            _.each(team.elbs, function(elb) {
                if (!('scheme' in elb) || elb.scheme!='internal') {
                    i++
                }
            });
            return i
        };

        // user select team from teams dashboard
        $scope.setTeam = function(team) {
            $scope.selectedTeam = team;
            $scope.selectedApplication = null;
            $scope.selectedEndpoint = null;

            $location.search('team', team);
            $location.search('app', null);

            if (!team && !fetchInterval) {
                startFetchInterval();
            } else if (team) {
                fastFetchTeamApps(team);
            }
        }

        // user select app from team apps list
        $scope.setApp = function(appId) {
            $scope.selectedApplication = appId;
            $scope.selectedEndpoint = null;

            $('#overview-charts svg').empty();

            $location.search('app', appId);
        }

        // user select endpoint from app list of endpoints
        $scope.setEndpoint = function(ep) {
            $scope.selectedEndpoint = ep;

            if (ep === null) {
                $scope.showApp($scope.selectedApplication);
            }
        }

        // a team has been selected by the user
        $scope.$watch('selectedTeam', function(teamName) {
            if (!teamName) { return; }

            LoadingIndicatorService.start();
            $scope.loading = true;
            $scope.trackedApplications = false;

            if (fetchInterval) {
                stopFetchInterval();
            }

            if (!$scope.selectedApplication) {
                fastFetchTeamApps(teamName);
            }
        });

        // set state from url parameters whenever they change
        $scope.$on('$routeUpdate', function() {
            setStateFromUrl();
        });

        // angular filter to get only tracked applications (have metric property)
        $scope.hasMetrics = function(app) {
            if (app.metrics.length) {
                $scope.trackedApplications = true;
                return true;
            }
            return false;
        }

        // filter for untracked applications
        $scope.notHasMetrics = function(app) {
            return !app.metrics.length;
        }

        // fetch all necessary resources
        var fetchCloudData = function() {
            CommunicationService.getCloudData('local').then(function(teams) {
                CommunicationService.getCloudData('application').then(function(applications) {
                    CommunicationService.getCloudData('instance').then(function(instances) {
                        CommunicationService.getCloudData('elb').then(function(elbs) {
                            createTeams(teams);
                            createApplications(applications);
                            createInstances(instances);
                            createElbs(elbs);

                            setStateFromUrl();

                            LoadingIndicatorService.stop();
                            $scope.loading = false;
                        });
                    });
                });
            });
        }

        // initialize fetching
        startFetchInterval();
        fetchCloudData();
    }
]);
