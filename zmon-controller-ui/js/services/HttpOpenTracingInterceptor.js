angular.module('zmon2App').factory('HttpOpenTracingInterceptorService', ['$q', 'OpenTracingService',
    function($q, OpenTracingService) {
        return {
            request: function(config) {
                var headers = {};
                var span = config._span = OpenTracingService.globalTracer().startSpan('xhr/' + config.url.split('/').pop());
                OpenTracingService.globalTracer().inject(span.context(), OpenTracingService.FORMAT_HTTP_HEADERS, headers);
                Object.keys(headers).forEach(function (h) {
                    config.headers[h] = headers[h];
                });
                return config;
            },
            response: function (response) {
                response.config._span.finish();
                return response;
            },
            responseError: function (rejection) {
                if (rejection.config && rejection.config._span) {
                    var span = rejection.config._span;
                    span.logEvent('error', rejection.error);
                    span.setTag('error', true);
                    span.finish();
                }
                return $q.reject(rejection);
            }
        };
    }
]);


/* Configure app to use our HTTP interceptor to handle errors.
 * In case of reponse error, collaborates with FeedbackMessageService
 * to provide relevant info.
 */
angular.module('zmon2App').config(['$httpProvider',
    function($httpProvider) {
        $httpProvider.interceptors.push('HttpOpenTracingInterceptorService');
    }
]);
