angular.module('zmon2App').directive('widgetConfigContainer', ['$compile', '$log', 'MainAlertService',
    function ($compile, $log, MainAlertService) {
        return {
            restrict: 'E',
            scope: {
                widgets: '=',
                widgetsJson: '=',
                widgetTypes: '=',
                invalidJson: '=',
                isVisible: '=',
                emptyJson: '=?',
                exclude: '=?'
            },
            templateUrl: 'templates/widgetConfigContainer.html',
            link: function (scope, element, attrs, controller) {

                scope.selectedWidgetType = scope.widgetTypes[0];
                scope.emptyJson = scope.emptyJson || (scope.emptyJson = false);

                scope.alertStyleTypes = [
                  'blink', 'shake', 'red', 'orange', 'yellow', 'green', 'blue'
                ];

                // Add a new chart by inserting at the beginning of the widgets array an empty chart object.
                var addChart = function() {
                    var chart = {
                        type: 'chart',
                        options: {
                            series: {
                                stack: false
                            },
                            lines: {
                                show: true
                            },
                            legend: {
                                show: true,
                                position: 'ne',
                                backgroundOpacity: 0.1
                            },
                            cache_time: 0,
                            start_relative: {
                                value: '30',
                                unit: 'minutes'
                            }
                        }
                    };

                    if (scope.selectedWidgetType.type === "Kairos Chart") {
                        chart.options.metrics = [{
                            tags: {},
                            group_by: []
                        }];
                    }

                    return chart;
                };

                var addGauge = function() {
                    return {
                        type: 'gauge'
                    };
                };

                var addValue = function() {
                    return {
                        type: 'value'
                    };
                };

                var addTrend = function() {
                    return {
                        type: 'trend'
                    };
                };

                var addIframe = function() {
                    return {
                        type: 'iframe',
                        style: {
                            width: '100%',
                            height: '100%',
                            scale: 1,
                        },
                        refresh: 60000,
                    };
                };

                // Add a new widget
                scope.addWidget = function() {
                    switch (scope.selectedWidgetType.type) {
                        case 'Kairos Chart':
                        case 'Check Chart':
                            scope.widgets = [addChart()].concat(scope.widgets);
                            break;
                        case 'Gauge':
                            scope.widgets = [addGauge()].concat(scope.widgets);
                            break;
                        case 'Value':
                            scope.widgets = [addValue()].concat(scope.widgets);
                            break;
                        case 'Trend':
                            scope.widgets = [addTrend()].concat(scope.widgets);
                            break;
                        case 'Iframe':
                            scope.widgets = [addIframe()].concat(scope.widgets);
                            break;
                    };
                };

                // Removes from the results set the filter definition at given index
                scope.removeWidget = function (idx) {
                    scope.widgets.splice(idx, 1);
                };

                scope.$watch('isVisible', function(formIsVisible) {
                    if (scope.invalidJson) {
                        return;
                    }
                    if (formIsVisible) {
                        try {
                            scope.widgets = JSON.parse(scope.widgetsJson);
                            return;
                        } catch (e) {}
                    }
                    scope.widgetsJson = angular.toJson(scope.widgets, true);
                });

                scope.$watch('widgetsJson', function() {
                    if (scope.widgetsJson) {
                        try {
                            scope.widgets = JSON.parse(scope.widgetsJson);
                        } catch (e) {
                        }
                    }
                }, true);

            }
        };
    }
]);
