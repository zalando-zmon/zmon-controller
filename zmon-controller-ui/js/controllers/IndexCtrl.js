angular.module('zmon2App').controller('IndexCtrl', ['$scope', '$window', '$location', '$rootScope', 'localStorageService', 'MainAlertService', '$interval', 'APP_CONST', 'LoadingIndicatorService', 'UserInfoService',
    function($scope, $window, $location, $rootScope, localStorageService, MainAlertService, $interval, APP_CONST, LoadingIndicatorService, UserInfoService) {
        $scope.IndexCtrl = this;
        $scope.IndexCtrl.activePage = null; // will be set be corresponding controller i.e. from the child $scope
        $scope.statusTooltip = "Loading status...";
        $scope.checksPerSecond = 0;
        $scope.serviceStatus = {};
        $scope.checkInvocationsCache = {};
        $scope.userTeamsOnPopover = '(no team)';
        $scope.globalSearchVisible = false;
        $scope.tvMode = false;

        console.log(hello.world);

        // Consent Dialog; data injected from controller
        $scope.title = consent.title;
        $scope.content = consent.content;

        $rootScope.globalSearchQuery = '';

        var $wrapper = $("#message-manager-wrapper")

        $scope.userInfo = UserInfoService.get();

        // limit list of teams on popover to 5
        if ($scope.userInfo.teams) {
            var teams = $scope.userInfo.teams.split(',');
            $scope.userTeamsOnPopover = teams.slice(-5).join(', ') + (teams.length > 5 ? '...' : '');
        }

        this.getLoadingIndicatorState = function() {
            return LoadingIndicatorService.getState();
        };

        $scope.pauseDataRefresh = function() {
            MainAlertService.pauseDataRefresh();
        };
        $scope.resumeDataRefresh = function() {
            MainAlertService.resumeDataRefresh();
        };

        $scope.$on('$routeChangeStart', function() {
            localStorageService.set('returnTo', '/#' + $location.path());
        });

        // global search key bindings
        $scope.keypress = function(e) {
            // run only if no other element than <body> are selected
            if (document.activeElement.nodeName !== "BODY") {
                return;
            }

            // exit if meta key is pressed
            if (e.ctrlKey || e.altKey || e.metaKey) {
                return;
            }

            // Catch only keys 0-9 and a-Z
            var k = (typeof e.which == "number") ? e.which : e.keyCode;
            if ((k >= 48 && k <= 57) || (k >= 65 && k <= 90) || (k >= 97 && k <= 122)) {
                e.stopPropagation();
                e.preventDefault();
                $scope.globalSearchVisible = true;
                $rootScope.globalSearchQuery = '' + String.fromCharCode(e.which);
            }
        };

        $scope.keydown = function(e) {
            // close on Esc
            if (e.keyCode === 27) {
                $scope.globalSearchVisible = false;
            }
        };

        // Start periodic refresh of the app's overall status only (Q-size etc.)
        // Not related to the periodic data refresh that the page content might by having (e.g. dashboard, alert details etc.)
        // The periodoc data refresh of the pages that have it are defined in MainAlertService.status's 'hasDataRefresh' and 'isDataRefreshing'
        $scope.alertsOutdated = false;

        // Refresh status helper.
        var refreshStatus = function () {
            var currentStatus = MainAlertService.getStatus();
            var statusHtml = "<div class='app-status-tooltip'>";

            $scope.statusTooltip = "";
            $scope.alertsOutdated = new Date()/1000 - $scope.alertsLastUpdate > 30 ? true : false;

            // Sort queues and workers.
            currentStatus.workers = _.sortBy(currentStatus.workers, "name");
            currentStatus.queues = _.sortBy(currentStatus.queues, "name");

            // Get workers detailed status.
            workerHtml = "";
            var schedulerHtml = "";
            var workerHtml = "";
            var dataHtml ="";

            _.each(currentStatus.workers, function(worker) {
                if (worker.name.startsWith('s-')) {
                    schedulerHtml += "<div>";
                    schedulerHtml += "<span class='name'>" + worker.name.substr(2) + "</span>";
                    schedulerHtml += "<span class='calls'>" + worker.checksPerSecond.toFixed(2) + "/s</span>";
                    schedulerHtml += "<span class='last'> " + worker.lastExecutionTime + "</span>";
                    schedulerHtml += "</div>";
                }
                else if (worker.name.startsWith('d-')) {
                    dataHtml += "<div>";
                    dataHtml += "<span class='name'>" + worker.name.substr(2) + "</span>";
                    dataHtml += "<span class='calls'>" + worker.checksPerSecond.toFixed(2) + "/s</span>";
                    dataHtml += "<span class='last'> " + worker.lastExecutionTime + "</span>";
                    dataHtml += "</div>";
                }
                else {
                    workerHtml += "<div>";
                    workerHtml += "<span class='name'>" + worker.name + "</span>";
                    workerHtml += "<span class='calls'>" + worker.checksPerSecond.toFixed(2) + "/s</span>";
                    workerHtml += "<span class='last'> " + worker.lastExecutionTime + "</span>";
                    workerHtml += "</div>";
                }
            });

            if (workerHtml != "") {
                statusHtml += "<div class='workers'><h6>Workers</h6>";
                statusHtml += workerHtml;
                statusHtml += "</div>";
            }

            if (schedulerHtml != "") {
                statusHtml += "<div class='workers'><h6>Schedulers</h6>";
                statusHtml += schedulerHtml;
                statusHtml += "</div>";
            }

            if (dataHtml != "") {
                statusHtml += "<div class='workers'><h6>Data Services</h6>";
                statusHtml += dataHtml;
                statusHtml += "</div>";
            }

            // Get queues detailed status.
            statusHtml += "<div class='queues'><h6>Queues</h6>";
            _.each(currentStatus.queues, function(queue) {
                statusHtml += "<div>";
                statusHtml += "<span class='name'>" + queue.name + "</span>";
                statusHtml += "<span class='size'>size " + queue.size + "</span>";
                statusHtml += "</div>";
            });
            statusHtml += "</div>";

            statusHtml += "</div>";

            $scope.statusTooltip = statusHtml;
            $scope.serviceStatus = currentStatus;
            $scope.checksPerSecond = currentStatus.checksPerSecond;
        };

        // Start refreshing workers / queue status.
        $interval(refreshStatus, 15000);
        refreshStatus();

        // redirect to end url after successful authentication
        var signinRedirTo = localStorageService.get('signinRedirTo');
        if (signinRedirTo) {
            localStorageService.set('signinRedirTo', null);
            return $location.url(signinRedirTo);
        }

        // Fix message bar to window top if scroll position moved below navbar
        angular.element($window).bind("scroll", function() {
            if (this.pageYOffset >= 50) {
                $wrapper.addClass('fix');
            } else {
                $wrapper.removeClass('fix')
            }
            $scope.$apply();
        })
    }]);
