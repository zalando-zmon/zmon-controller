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

                // Add a new widget
                scope.addWidget = function() {
                    switch (scope.selectedWidgetType.type) {
                        case 'Kairos Chart':
                        case 'Check Chart':
                            scope.widgets.push(addChart());
                            break;
                        case 'Gauge':
                            scope.widgets.push(addGauge());
                            break;
                        case 'Value':
                            scope.widgets.push(addValue());
                            break;
                        case 'Trend':
                            scope.widgets.push(addTrend());
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
