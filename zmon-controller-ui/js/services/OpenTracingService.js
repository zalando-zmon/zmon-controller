angular.module('zmon2App').factory('OpenTracingService', [
    function() {
        var noop = angular.noop
        var service = window.opentracing
        if (!service) {
            console.warn('Opentracing library not present');
            service = {
                globalTracer: function () {
                    return {
                        startSpan: function () {
                            return {
                                context: noop,
                                log: noop,
                                logEvent: noop,
                                setTag: noop,
                                finish: noop
                            }
                        },
                        inject: noop,
                        extract: noop
                    }
                }
            }
        }

        console.log('Using OpenTracing library:', service)
        return service;
    }
]);
