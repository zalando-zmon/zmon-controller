angular.module('zmon2App').controller('CloudEndpointsCtrl', ['$scope', '$timeout', '$interval', '$location', 'CommunicationService', 'LoadingIndicatorService', 'APP_CONST',
    function CloudEndpointsCtrl($scope, $timeout, $interval, $location, CommunicationService, LoadingIndicatorService, APP_CONST) {

        // endpoints and charts
        $scope.endpoints = [];
        $scope.endpointsByRuntime = [];
        $scope.globalAppData = {};
        $scope.globalAppDataList = {};

        // interval id and duration of 1min
        var fetchInterval = null;
        var INTERVAL = 60000;

        // KairosDB query objects
        var q_single_app_aws = function(appId, metric, tags, grpOp) {
            return {
                "metrics": [
                    {
                        "tags": {
                            "application_id": [ appId ],
                            "metric":  metric.split(',')
                        },
                        "name": 'zmon.check.2132',
                        "group_by": [
                            {
                                "name": "tag",
                                "tags": tags.split(',')
                            }
                        ],
                        "aggregators": [
                            {
                                "name": grpOp,
                                "align_sampling": true,
                                "sampling": {
                                    "value": "1",
                                    "unit": "minutes"
                                }
                            }
                        ]
                    }
                ],
                "cache_time": 0,
                "start_relative": {
                    "value": "30",
                    "unit": "minutes"
                }
            }
        };

        var q_applications_rt = {
            "metrics": [
                {
                    "tags": {
                        "metric": [ "99th" ]
                    },
                    "name": "zmon.check.2115",
                    "group_by": [
                        {
                            "name": "tag",
                            "tags": [ "sg", "port" ]
                        }
                    ],
                    "aggregators": [
                        {
                            "name": "max",
                            "align_sampling": true,
                            "sampling": {
                                "value": "1",
                                "unit": "minutes"
                            }
                        }
                    ]
                }
            ],
            "cache_time": 0,
            "start_relative": {
                "value": "5",
                "unit": "minutes"
            }
        };

        var q_elb_metrics = {
            "metrics": [
                {
                    "tags": {
                        "metric":["requests_per_sec"]
                    },
                    "name": "zmon.check.2026",
                    "group_by": [
                        {
                            "name": "tag",
                            "tags": [
                                "key",
                                "stack_name"
                            ]
                        }],
                    "aggregators": [
                        {
                            "name": "sum",
                            "align_sampling": true,
                            "sampling": {
                                "value": "1",
                                "unit": "minutes"
                            }
                        }
                    ]
                }
            ],
            "cache_time": 0,
            "start_relative": {
                "value": "5",
                "unit": "minutes"
            }
        };

        var portMap = {
            '3424':'zmon-data-service',
            '3429':'eventlog-data-service',
            '3409':'eventlog-data-service-release',
            '21234':'iam-services',
            '26666':'iam-teams',
            '28888':'iam-tokesn',
            '29452':'iam-users',
            '29876':'iam-pegasus'
        };

        var maxTs = function(ts) {
            if (ts === undefined) {
                return;
            }

            var max = 0
            ts.forEach(function(v) {
                max = Math.max(max,v[1])
            })
            return Math.round(max);
        }

        var populateHost = function(listOfTs) {
            var metricTs = null;

            listOfTs.forEach(function(ts) {
                if ("metrics/GET" == ts.ep && "200" == ts.status && ts.metric == "mRate") {
                  metricTs = ts.v
                }
            })

            if (metricTs==null) {
                return console.log("Could not find metrics/GET endpoint for time series base")
            }

            listOfTs.forEach(function(series) {
                var vs = series.v
                var j = 0
                for(var i=0; i < metricTs.length; i++) {
                    var ts = metricTs[i][0]
                    if (j >= vs.length) {
                        series.nv.push([ts, 0])
                    }
                    else if (ts < vs[j][0]) {
                        series.nv.push([ts, 0])
                    }
                    else {
                        series.nv.push([ts, vs[j][1]])
                        j++;
                    }
                }
            })
        }

        /*
        This function uses the "metrics/GET"->"count" time series as a base line to populate missing data points
        The stack charts needs aligend data points to render properly. Luckyly the "metrics/GET" end point
        should always be available and thus provide us with all valid timestamps and the max length.

        */
        var populateData = function(data, appId) {
            var mapByEntity = {}

            var endpointKeys = Object.keys(data)
            endpointKeys.forEach(function(ep) {
                var statusKeys = Object.keys(data[ep])
                statusKeys.forEach(function(status){
                    var metricKeys = Object.keys(data[ep][status])
                    metricKeys.forEach(function(metric){
                        var mo = {
                            entity: appId,
                            ep: ep,
                            status: status,
                            metric: metric,
                            v: data[ep][status][metric],
                            nv : []
                        }

                        if (!(mo.entity in mapByEntity)) {
                            mapByEntity[mo.entity] = []
                        }

                        mapByEntity[mo.entity].push(mo)
                    })
                })
            })

            var dps = {};

            populateHost(mapByEntity[appId])
            dps[appId]={}
            var entity=dps[appId]

            mapByEntity[appId].forEach(function(dp) {
                if (!(dp.ep in entity)) {
                    entity[dp.ep] = {"_url": dp.ep}
                }
                var ep = entity[dp.ep]

                if (!(dp.status in ep)) {
                    ep[dp.status]={}
                }
                var status=ep[dp.status]

                if (!(dp.metric in status)) {
                    status[dp.metric] = dp.nv
                }
            })

            return dps
        }

        var sumOverStatus = function(statusMap, metric, statusFilter) {
            l = []
            var keys = Object.keys(statusMap).filter(function(k){return k!='_url'}).filter(statusFilter)
            if (keys.length==0) {
                return l
            }

            len = statusMap[keys[0]][metric].length
            for(i=0;i<len;i++) {
                t = statusMap[keys[0]][metric][i][0]
                var sum = 0
                keys.forEach(function f(s) { sum+= statusMap[s][metric][i][1] })
                l.push([t,sum])
            }
            return l
        }

        var extractData = function(data, metric, statusFilter) {
            r = []
            data.forEach(function(d) {
                vs = sumOverStatus(d, metric, statusFilter)
                if (vs.length==0) return;
                o = {"key": d["_url"], values: vs}
                r.push(o)
            })

            return r
        }

        var sumRate = function(ep) {
            var sum = 0;
            _.each(ep, function(e, k) {
                if ("_url" === k || "$$hashKey" === k) return;
                sum += e["mRate"].slice(-1)[0][1];
            });
            return sum;
        }

        var sumRunTime = function(ep) {
            var sum = 0;
            _.each(ep, function(e, k) {
                if ("_url" === k || "$$hashKey" === k) return;
                sum += e["median"].slice(-1)[0][1] * e["mRate"].slice(-1)[0][1];
            })
            return sum
        }

        var s200_99 = function(ep) {
            if (!("200" in ep)) {
                return "-"
            }
            return Math.round(ep["200"]["99th"].slice(-1)[0][1]);
        }

        var s200_min = function(ep) {
            if (!("200" in ep)) {
                return "-"
            }
            return Math.round(ep["200"]["min"].slice(-1)[0][1]);
        }

        var s200_max = function(ep) {
            if (!("200" in ep)) {
                return "-"
            }
            return Math.round(ep["200"]["max"].slice(-1)[0][1]);
        }

        var getMax = function(data, df) {
            var max = 0;
            _.each(data, function(d) {
                v = df(d)
                if (v > max) max = v;
            });
            return max;
        }

        var sortEps = function(a,b) {
            var sumA = 0;
            var sumB = 0;

            Object.keys(a).forEach(function(k){
                if ("_url" == k) return;
                if (a[k]["mRate"].length==0) return;

                var v = a[k]["mRate"].slice(-1)[0];
                if (v.length > 0) {
                    sumA += v[1]
                }
            });

            Object.keys(b).forEach(function(k){
                if ("_url" == k) return;
                if (b[k]["mRate"].length==0) return;

                var v = b[k]["mRate"].slice(-1)[0];
                if (v.length > 0) {
                    sumB += v[1]
                }
            });

            return sumB - sumA;
        };

        var sortEpsRt = function(a,b) {
            var sumA = 0;
            var sumB = 0;

            Object.keys(a).forEach(function(k){
                if ("_url" == k) return;
                if (a[k]["mRate"].length==0) return;
                if (a[k]["median"].length==0) return;

                var v_r = a[k]["mRate"].slice(-1)[0];
                var v_t = a[k]["median"].slice(-1)[0];
                if(v_r.length > 0 && v_t.length > 0) {
                    sumA += v_r[1]*v_t[1];
                }
            });

            Object.keys(b).forEach(function(k){
                if ("_url" == k) return;
                if (b[k]["mRate"].length==0) return;
                if (b[k]["median"].length==0) return;

                var v_r = b[k]["mRate"].slice(-1)[0];
                var v_t = b[k]["median"].slice(-1)[0];
                if(v_r.length > 0 && v_t.length > 0) {
                    sumB += v_r[1]*v_t[1];
                }
            });

            return sumB - sumA;
        };

        // kairos data for one application (or entity) has been successfully fetched and needs processing
        var receivedSingleApp = function (data) {

            var appData = {}

            if ('application_id' in data.queries[0].results[0].tags)
                var appId = data.queries[0].results[0].tags['application_id'][0]
            else {
                var port = data.queries[0].results[0].tags['port'][0]
                var appId = portMap[port]
            }

            data.queries[0].results.forEach(function (r){
                parts = r.tags["key"][0].split(".")
                url = parts.slice(0, parts.length-2).join("/").replace('root/', '')

                status = parts.slice(-2, -1)
                metric = parts.slice(-1)

                if (metric=='mRate') {
                    value = r.values.map(function(x){return [x[0], 60*x[1]]})
                }
                else {
                    value = r.values
                }

                if (!(url in appData)) {
                    appData[url]= {}
                }

                if (!(status in appData[url])) {
                    appData[url][status]={}
                }

                appData[url][status][metric[0]] = value
            })

            appData = populateData(appData, appId)

            var appAsList = []
            var eps = Object.keys(appData[appId])
            eps.forEach(function(ep) {
                appAsList.push(appData[appId][ep])
            })

            $scope.globalAppData[appId] = appData[appId]
            $scope.globalAppDataList[appId] = appAsList

            return appAsList
        }

        var renderLineChart = function(divId, data, withLegend) {
            nv.addGraph(function() {
            var chart = nv.models.lineChart()
                          .margin({right: 100})
                          .x(function(d) { return d[0] })   //We can modify the data accessor functions...
                          .y(function(d) { return d[1] })   //...in case your data is formatted differently.
                          .useInteractiveGuideline(true)    //Tooltips which show all data points. Very nice!
                          .rightAlignYAxis(true)      //Let's move the y-axis to the right side.
                          .clipEdge(true)
                          .showLegend(false);

            if (withLegend) chart.showLegend(true)

            //Format x-axis labels with custom function.
            chart.xAxis
                .tickFormat(function(d) { 
                  return d3.time.format('%H:%M')(new Date(d))
            });

            chart.yAxis
                .tickFormat(d3.format(',.2f'));

            d3.select('#'+divId+'')
              .datum(data)
              .call(chart);

            nv.utils.windowResize(chart.update);

            return chart;
          });
        }

        var renderStackedChart = function(divId, data, withLegend) {
            nv.addGraph(function() {
            var chart = nv.models.stackedAreaChart()
                          .margin({right: 100})
                          .x(function(d) { return d[0] })   //We can modify the data accessor functions...
                          .y(function(d) { return d[1] })   //...in case your data is formatted differently.
                          .useInteractiveGuideline(true)    //Tooltips which show all data points. Very nice!
                          .rightAlignYAxis(true)      //Let's move the y-axis to the right side.               
                          .showControls(false)       //Allow user to choose 'Stacked', 'Stream', 'Expanded' mode.
                          .clipEdge(true)
                          .showLegend(false);

            if (withLegend) chart.showLegend(true)

            //Format x-axis labels with custom function.
            chart.xAxis
                .tickFormat(function(d) { 
                  return d3.time.format('%H:%M')(new Date(d))
            });

            chart.yAxis
                .tickFormat(d3.format(',.2f'));

            d3.select('#'+divId+'')
              .datum(data)
              .call(chart);

            nv.utils.windowResize(chart.update);

            return chart;

          });
        }

        // populates list of endpoints for a preselected app
        var setEndpoints = function(data, df) {
            var endpoints = [];
            var max = getMax(data, df);

            _.each(data, function(d) {
                var rate = df(d);

                endpoints.push({
                    url: d["_url"].replace('.root'),
                    rate: rate.toFixed(3),
                    min: s200_min(d),
                    max: s200_max(d),
                    p99: s200_99(d),
                    width: (100*(rate/max)).toFixed(2)
                });
            });

            return endpoints;
        }


        var showEndpoint = function(url, appId) {
            $scope.setEndpoint(url);

            var data = $scope.globalAppData[appId];

            if (!(url in data)) {
                url = url+'/root'
                if (!(url in data)) {
                    console.log('Could not find endpoint')
                    return false
                }
            }

            epData = data[url]

            // [{key:200, values: []}]
            var status = Object.keys(epData).filter(function(k){return k!='_url'})
            var ratesByStatus200 = []
            var ratesByStatus = []
            var responseTime200 = []
            var responseTimeOther= []

            status.forEach(function(k) {

                if (k=='200') {
                    ratesByStatus200.push({key: k, values: epData[k]["mRate"]})
                    responseTime200.push({key: k+"-median", values: epData[k]["median"]})
                    responseTime200.push({key: k+"-99th", values: epData[k]["99th"]})
                }
                else {
                    responseTimeOther.push({key: k+"-99th", values: epData[k]["99th"]})
                    ratesByStatus.push({key: k, values: epData[k]["mRate"]})
                }
            })

            renderLineChart('chart-dt-rate200', ratesByStatus200, false)
            renderStackedChart('chart-dt-rateother', ratesByStatus, true)
            renderStackedChart('chart-dt-rt200', responseTime200, true)
            renderStackedChart('chart-dt-rtother', responseTimeOther, true)
        }

        var renderCharts = function(appId) {

            var app = $scope.applications[appId];

            var pointsRate = app.data.sort(sortEps).slice()
            var pointsRuntime = app.data.sort(sortEpsRt).slice()

            $scope.endpoints = setEndpoints(pointsRate, sumRate);
            $scope.endpointsByRuntime = setEndpoints(pointsRuntime, sumRunTime);

            var totalRates = extractData(pointsRate, "mRate", function f(x){return true})
            var totalErrorRates = extractData(pointsRate, "mRate", function f(x){return x!='200'})

            if (!$scope.selectedEndpoint) {
                renderStackedChart('chart_rate', totalRates.reverse())
                renderStackedChart('chart_error', totalErrorRates.reverse())
            } else {
                showEndpoint($scope.selectedEndpoint, $scope.selectedApplication)
            }

            LoadingIndicatorService.stop();
            $scope.loading = false;

            $location.search('app', appId);
        }

        var showApp = function(appId, cb) {

            $scope.selectedApplication = appId;
            LoadingIndicatorService.start();
            $scope.loading = true;

            startFetchAppData(appId, renderCharts);
        };

        var startFetchAppData = function(appId, cb) {
            if (!fetchInterval) { 
                fetchInterval = $interval(function() {
                    fetchAppData(appId, cb)
                }, INTERVAL);
                fetchAppData(appId, cb);
            }
        };

        var stopFetchInterval = function() {
            $interval.cancel(fetchInterval);
            fetchInterval = null;
        }

        var fetchAppData = function(appId, cb) {
            CommunicationService.getCloudViewEndpoints({"application_id": appId})
                .then(function(d1) {
                    var app = $scope.$parent.applications[appId];
                    app.data = receivedSingleApp(d1);
                    cb(appId);
                });
        }

        // set view state from URL
        var setStateFromUrl = function() {
            if ($location.search().app) {
                $scope.selectedTeam = $location.search().team;
                showApp($location.search().app)
            } 
        }

        $scope.$watch('selectedEndpoint', function(endpoint) {
            if (endpoint) {
                showEndpoint(endpoint, $scope.selectedApplication);
            } else if ($scope.selectedApplication) {
                renderCharts($scope.selectedApplication);
            }
        });

        $scope.$parent.$watch('selectedApplication', function(app) {
            if (app) {
                return showApp(app);
            }
            stopFetchInterval();
        });

        $scope.$on('$routeUpdate', function() {
            setStateFromUrl();
        });



    }])
