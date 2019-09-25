angular.module('zmon2App', [
    //'ngCookies',
    // 'ngResource',
    'ngSanitize',
    'ngRoute',
    'angulartics',
    'angulartics.google.analytics',
    'LocalStorageModule',
    'ui.bootstrap',
    'infinite-scroll',
    'ngclipboard',
    'ui.select',
    'hljs',
    'ngDebounce',
]).config(['$routeProvider', '$compileProvider', '$analyticsProvider',
        function($routeProvider, $compileProvider, $analyticsProvider) {
            $analyticsProvider.firstPageview(true); /*Records pages that don't use $state or $route*/
            $analyticsProvider.withAutoBase(true); /*Records full path*/

            // Whitelist "blob:" URLs for the anchor "href" to download check-definition YAML file in Trial Runs page
            $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|tel|file|blob):/);
            $routeProvider
                .when('/', {
                    templateUrl: 'views/dashboard.html',
                    controller: 'DashboardCtrl',
                    reloadOnSearch: false
                })
                .when('/alert-details/:alertId', {
                    templateUrl: 'views/alertDetails.html',
                    controller: 'AlertDetailsCtrl',
                    reloadOnSearch: false
                })
                .when('/alert-definitions/add/:checkDefinitionId', {
                    templateUrl: 'views/alertDefinitionEditForm.html',
                    controller: 'AlertDefinitionEditCtrl'
                })
                .when('/alert-definitions/edit/:alertDefinitionId', {
                    templateUrl: 'views/alertDefinitionEditForm.html',
                    controller: 'AlertDefinitionEditCtrl'
                })
                .when('/alert-definitions/clone/:cloneFromAlertDefinitionId', {
                    templateUrl: 'views/alertDefinitionEditForm.html',
                    controller: 'AlertDefinitionEditCtrl'
                })
                .when('/alert-definitions/inherit/:parentAlertDefinitionId', {
                    templateUrl: 'views/alertDefinitionEditForm.html',
                    controller: 'AlertDefinitionEditCtrl'
                })
                .when('/check-definitions', {
                    templateUrl: 'views/checkDefinitions.html',
                    controller: 'CheckDefinitionCtrl',
                    reloadOnSearch: false
                })
                .when('/check-definitions/view/:checkDefinitionId', {
                    templateUrl: 'views/checkDefinitionView.html',
                    controller: 'CheckDefinitionCtrl'
                })
                .when('/check-definitions/edit/:checkId', {
                    templateUrl: 'views/checkDefinitionEditForm.html',
                    controller: 'CheckDefinitionEditCtrl'
                })
                .when('/check-charts/:checkId', {
                    templateUrl: 'views/checkCharts.html',
                    controller: 'CheckChartsCtrl',
                    reloadOnSearch: false
                })
                .when('/alert-definitions', {
                    templateUrl: 'views/alertDefinitions.html',
                    controller: 'AlertDefinitionCtrl',
                    reloadOnSearch: false
                })
                .when('/dashboards', {
                    templateUrl: 'views/dashboardDefinitions.html',
                    controller: 'DashboardDefinitionCtrl'
                })
                .when('/dashboards/view/:dashboardId', {
                    templateUrl: 'views/dashboard.html',
                    controller: 'DashboardCtrl',
                    reloadOnSearch: false
                })
                .when('/dashboards/add/', {
                    templateUrl: 'views/dashboardConfiguration.html',
                    controller: 'DashboardConfigurationCtrl'
                })
                .when('/dashboards/edit/:dashboardId', {
                    templateUrl: 'views/dashboardConfiguration.html',
                    controller: 'DashboardConfigurationCtrl'
                })
                .when('/dashboards/clone/:cloneFromDashboardId', {
                    templateUrl: 'views/dashboardConfiguration.html',
                    controller: 'DashboardConfigurationCtrl'
                })
                .when('/trial-run', {
                    templateUrl: 'views/trialRun.html',
                    controller: 'TrialRunCtrl',
                    reloadOnSearch: false
                })
                .when('/trial-run/:checkId', {
                    templateUrl: 'views/trialRun.html',
                    controller: 'TrialRunCtrl',
                    reloadOnSearch: false
                })
                .when('/changes', {
                    templateUrl: 'views/changes.html',
                    controller: 'ChangesCtrl'
                })
                .when('/cloud', {
                    templateUrl: 'views/cloud.html',
                    controller: 'CloudCtrl',
                    reloadOnSearch: false
                })
                .when('/entities', {
                    templateUrl: 'views/entities.html',
                    controller: 'EntityCtrl',
                    reloadOnSearch: false
                })
                .otherwise({
                    redirectTo: '/'
                });
        }
    ])
// Add app constants.
.constant('APP_CONST', {
    'DASHBOARD_REFRESH_RATE': 30000, //in ms
    'DASHBOARD_WIDGETS_REFRESH_RATE': 30000, //in ms
    'ALERT_DETAILS_REFRESH_RATE': 30000,
    'INFINITE_SCROLL_VISIBLE_ENTITIES_INCREMENT': 50, // increment by this the # of visible items of infinite-scroll
    'INFINITE_SCROLL_MAX_LENGTH': 500,
    'CHECK_DEFINITIONS_REFRESH_RATE': 15000,
    'ALERT_DEFINITIONS_REFRESH_RATE': 15000,
    'FLOATING_NUM_PRECISION': 2,
    'MAX_ENTITIES_DISPLAYED': 3,
    'MAX_ENTITIES_WITH_CHARTS': 3,
    'FEEBACK_MSG_SHOW_TIME': 5000,
    'ALERT_HISTORY_BATCH_SIZE': 20,
    'INDENT': '    '
});
