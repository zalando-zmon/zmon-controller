/**
 * BootConfig constant provides central information for the system. Feel free to add more fields here, and
 * override them in FrontendBootDataService on the backend.
 *
 * Because it's an angular constant (as opposed to service or factory), it can be used in .config()
 *
 * IMPORTANT: angular.merge does not work on complicated objects like Blob, MediaStream, CanvasGradient and
 * its own angular scopes. See https://docs.angularjs.org/api/ng/function/angular.merge#known-issues
 */

angular.module('zmon2App').constant('BootConfig',
    angular.merge({}, {
        check: {
            minInterval: {
                normal: 5,
                whitelisted: 5,
                whitelistedChecks: []
            }
        },
        alert: {
            detailsRefreshRate: 30000,
            infiniteScroll: {
                visibleEntitiesIncrement: 50, // increment by this the # of visible items of infinite-scroll
                maxLength: 500
            }
        },
        entity: {
        },
        python: {
            identSpaces: 4
        },
        dashboard: {
            refreshRate: 30000, //in ms
            widgetsRefreshRate: 30000, //in ms
            maxEntitiesDisplayed: 3,
            maxEntitiesWithCharts: 3
        },
        feedback: {
            msgShowTime: 5000
        }
    }, window.zmonBootData || {})
);
