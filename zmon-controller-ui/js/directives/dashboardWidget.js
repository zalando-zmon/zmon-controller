angular.module('zmon2App').directive('dashboardWidget', ['CommunicationService', 'MainAlertService', 'FeedbackMessageService', 'DowntimesService', '$sce', '$timeout', 'APP_CONST',
    function(CommunicationService, MainAlertService, FeedbackMessageService, DowntimesService, $sce, $timeout, APP_CONST) {
        return {
            restrict: 'E',
            templateUrl: 'templates/dashboardWidget.html',
            replace: true,
            scope: {
                config: '=config',
                data: '=data'
            },
            controller: function($scope, $element, $attrs, $transclude, $timeout) {
                if (!$scope.config.options) {
                    $scope.config.options = {};
                }

                var reloadIframe = function() {
                    $timeout(function() {
                        var iframe = document.getElementById('iframe-widget');
                        if (iframe && iframe.src) {
                            reloadIframe();
                        }
                    }, $scope.config.refresh);
                };

                var initIframe = function() {
                    $scope.config.trustedSrc = $sce.trustAsResourceUrl($scope.config.src);
                    $scope.config.css = {
                        'width': $scope.config.style.width,
                        'height': $scope.config.style.height,
                        '-webkit-transform': "scale("+ $scope.config.style.scale +")",
                        '-o-transform': "scale("+ $scope.config.style.scale +")",
                        '-moz-transform': "scale("+ $scope.config.style.scale + ")",
                        '-ms-zoom': $scope.config.style.scale
                    };
                }

                // trust url as resource for iframes
                if ($scope.config.type === 'iframe') {
                    initIframe();
                    // reload iframe periodically
                    if ($scope.config.refresh) {
                        reloadIframe();
                    }
                }

                var checkDefinitionId = $scope.config.checkDefinitionId;
                var alertIds = $scope.config.options.alertIds || $scope.config.alertIds;

                // entity is 'undefined' for multi-entity line charts e.g. Jan's redis queue size chart; in this case the getCheckResults() here is mandatory
                var entity = $scope.config.entityId;

                // Construct uniqId as combination of widgetType-checkId-entityId
                // This will identify this widget to the MainAlertService.startDataRefresh()
                if (checkDefinitionId > 0) {
                    $scope.uniqId = $scope.config.type + '-' + checkDefinitionId + '-' + entity;
                } else if (alertIds) {
                    $scope.uniqId = $scope.config.type + '-' + alertIds + '-' + entity;
                } else {
                    $scope.uniqId = $scope.config.type + '-' + Date.now();
                }

                // Check if "alertStyles" is properly defined on the widget configuration, following the
                // schema alertsyles: {"CLASS_NAME: [ARRAY_ALERT_IDS]}
                var setAlertStyles = function(response) {
                    var alertStyles = $scope.config.alertStyles;

                    if ($.isPlainObject(alertStyles)) {
                        var activeAlertIds;
                        var isActive = false;

                        // No response? Get predefined alert ids from scope.
                        if (response === undefined) {
                            activeAlertIds = $scope.activeAlertIds;
                            activeAlertIds = _.map(activeAlertIds, "alert_definition");
                            activeAlertIds = _.map(activeAlertIds, "id");
                        } else {
                            activeAlertIds = response[0].active_alert_ids;
                        }

                        $.each(alertStyles, function(key, value) {
                            // Use array intersection to verify if alert ID for the current
                            // class name matches any of the active alert IDs.
                            if (_.intersection(activeAlertIds, value).length > 0) {
                                $element.addClass(key);
                                isActive = true;
                            } else {
                                $element.removeClass(key);
                            }
                        });

                        // Add "active" clas if any styling was set.
                        if (isActive) {
                            $element.addClass("active");
                        } else {
                            $element.removeClass("active");
                        }
                    }
                };

                var selectSeries = function(chartData) {
                    if (!($scope.config.series instanceof Array)) {
                        return chartData;
                    }
                    var result = [];
                    _.each(chartData, function(serie) {
                        if ($scope.config.series.indexOf(serie.label) !== -1) {
                            result.push(serie);
                        }
                    });
                    return result;
                };

                var formatKairosData = function(data) {
                    var r = [];
                    _.each(data.queries, function(query) {
                        if (query.results && query.results.length) {
                            _.each(query.results, function(result) {
                                r.push(result.values);
                            });
                        }
                    });
                    return r;
                };

                var getFormatedValue = function(format, value) {
                    // extract format value if in {:} enclosing
                    var match = format.match(/\{\:(\.[0-9]f)\}/);

                    if (match && match.length) {
                        format = match[1];
                    }

                    return d3.format(format)(value);
                }

                var setWidgetData = function(response) {

                    // Make sure repsonse is valid, and if not log to console.
                    if (_.isEmpty(response)) {
                        // console.warn("Response for widget " + $scope.uniqId + " is empty!");
                        return;
                    }

                    setAlertStyles();
                    $scope.lastUpdate = new Date() / 1000;

                    try {
                        switch ($scope.config.type) {
                            case 'value':
                                $scope.values = _.map(response[0].results, 'value');

                                // If jsonPath is specified, use it to get specific values from results.
                                if ($scope.config.jsonPath) {
                                    $scope.values = jsonPath($scope.values, "$." + $scope.config.jsonPath);
                                }

                                var valuesContainNumbers = $scope.values
                                                            .map(_.isNumber)
                                                            .some(_.identity);

                                $scope.lastValue = $scope.values[0];
                                $scope.maxValue = valuesContainNumbers ? _.max($scope.values) : $scope.lastValue;

                                if (!$scope.style) {
                                    $scope.style = {};
                                }

                                if ($scope.config.options.fontSize) {
                                    $scope.style["font-size"]= $scope.config.options.fontSize;
                                } else {
                                    delete $scope.style["font-size"];
                                }

                                if ($scope.config.options.color) {
                                    $scope.style["color"] = $scope.config.options.color;
                                } else {
                                    delete $scope.style["color"];
                                }

                                if ($scope.config.options.format) {
                                    $scope.maxValue = getFormatedValue($scope.config.options.format, $scope.maxValue);
                                } else if(_.isNumber($scope.maxValue)) {
                                    $scope.maxValue = ($scope.maxValue || 0).toFixed(0) / 1
                                }

                                break;
                            case 'chart':
                                var chartData = MainAlertService.transformResultsToChartData(response);

                                // If jsonPath is specified, use it to get specific response
                                if ($scope.config.jsonPath) {
                                    response = jsonPath(response, "$." + $scope.config.jsonPath);
                                }

                                if ($scope.config.options.metrics instanceof Array) {
                                    chartData = formatKairosData(response);
                                    $scope.chartData = $scope.config.series ? selectSeries(chartData) : chartData;
                                    break;
                                }

                                $scope.hasNonPlottableValues = chartData.hasNonPlottableValues;
                                if (entity !== undefined) {
                                    $scope.chartData = $scope.config.series ? selectSeries(chartData[entity]) : chartData[entity];
                                } else {
                                    // See "Case 3" in MainAlertService.transformResultsToChartData()
                                    $scope.chartData = $scope.config.series ? selectSeries(chartData) : chartData;
                                }
                                break;
                            case 'gauge':
                                if (response.length > 0) {
                                    $scope.values = _.map(response[0].results, 'value');
                                    // If jsonPath is specified, use it to get specific values from results.
                                    if ($scope.config.jsonPath) {
                                        $scope.values = jsonPath($scope.values, "$." + $scope.config.jsonPath);
                                    }
                                    $scope.lastValue = $scope.values[0];
                                    $scope.maxValue = _.max($scope.values);

                                    if ($scope.config.options.format) {
                                        $scope.lastValue = parseFloat(getFormatedValue($scope.config.options.format, $scope.lastValue));
                                    }
                                }
                                break;
                            case 'trend':
                                var values = _.map(response[0].results, 'value');
                                // If jsonPath is specified, use it to get specific values from results.
                                if ($scope.config.jsonPath) {
                                    values = jsonPath(values, "$." + $scope.config.jsonPath);
                                }
                                $scope.mean = _.reduce(values, function (memo, val) {
                                    return memo + val;
                                }, 0) / response[0].results.length;
                                $scope.current = response[0].results[0].value;
                                break;
                            default:
                                FeedbackMessageService.showErrorMessage('invalid widget type: ' + $scope.config.type);
                        }
                    } catch (ex) {
                        console.error("Error processing widget!", ex);
                    }
                };


                var refreshWidgetData = function() {

                    $scope.isOutdated =  new Date() / 1000 - $scope.lastUpdate > 30 ? true : false;
                    alertIds = $scope.config.options.alertIds || $scope.config.alertIds || [];

                    // include as alert Ids all alertStyle ids (i.e. "red": [3, 5, 10]);
                    _.each($scope.config.alertStyles, function(ids) {
                        alertIds = _.uniq(alertIds.concat(ids));
                    });

                    var limit = null;
                    if ($scope.config.type === 'value') {
                        limit = 1;
                    }

                    // Get data if alertId is specified
                    if (alertIds !== undefined && alertIds !== null && alertIds !== "") {
                        var activeAlertIds = [];
                        CommunicationService.getAlertsById(alertIds).then(function(data) {
                            _.each(data, function(alert) {
                                if (!DowntimesService.hasAllEntitiesInDowntime(alert)) {
                                    activeAlertIds.push(alert);
                                }
                            });
                            $scope.activeAlertIds = activeAlertIds;
                            setAlertStyles();
                        });
                    }

                    // Get data if checkId is specified
                    if (checkDefinitionId > 0) {
                        return CommunicationService.getCheckResults(checkDefinitionId, entity, limit).then(
                            function(response) {
                                setWidgetData(response);
                            }
                        );
                    }

                    // Get data from KairosDB if Metrics options are specified
                    if ($scope.config.options.metrics instanceof Array) {
                        var metric = $scope.config.options.metrics[0];
                        if (!metric.name) {
                            return;
                        }
                        return CommunicationService.getKairosResults($scope.config.options).then(
                            function(response) {
                                setWidgetData(response);
                            }
                        );
                    }
                };

                // Watch for configuration changes on the widget and refetch data
                $scope.$watch('config', function(options) {

                    if (options.type === 'iframe') {
                        var urlValid = /(http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
                        if (urlValid.test(options.src)) {
                            return initIframe();
                        }
                        return;
                    }

                    checkDefinitionId = options.checkDefinitionId;
                    entity = options.entityId;
                    refreshWidgetData();
                }, true);

                $element.on('$destroy', function() {
                    MainAlertService.removeDataRefreshById($scope.uniqId);
                });

                this.startDataRefresh = function() {
                    try {
                        // Last param signifies it is not a fresh page load; this is because widget loading is part of the dashboardCtrl loading
                        MainAlertService.startDataRefresh($scope.uniqId, _.bind(refreshWidgetData, this), APP_CONST.DASHBOARD_WIDGETS_REFRESH_RATE, false);
                    } catch (ex) {
                        console.error(new Date(), "ERROR DashboardWidget: ", ex, " / $scope.config: ", $scope.config);
                    }
                };

                this.startDataRefresh();
            }
        };
    }
]);
