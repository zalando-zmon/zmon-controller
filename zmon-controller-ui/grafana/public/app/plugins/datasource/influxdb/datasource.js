///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', 'app/core/utils/datemath', './influx_series', './influx_query', './response_parser'], function(exports_1) {
    var lodash_1, dateMath, influx_series_1, influx_query_1, response_parser_1;
    var InfluxDatasource;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (dateMath_1) {
                dateMath = dateMath_1;
            },
            function (influx_series_1_1) {
                influx_series_1 = influx_series_1_1;
            },
            function (influx_query_1_1) {
                influx_query_1 = influx_query_1_1;
            },
            function (response_parser_1_1) {
                response_parser_1 = response_parser_1_1;
            }],
        execute: function() {
            InfluxDatasource = (function () {
                /** @ngInject */
                function InfluxDatasource(instanceSettings, $q, backendSrv, templateSrv) {
                    this.$q = $q;
                    this.backendSrv = backendSrv;
                    this.templateSrv = templateSrv;
                    this.type = 'influxdb';
                    this.urls = lodash_1.default.map(instanceSettings.url.split(','), function (url) {
                        return url.trim();
                    });
                    this.username = instanceSettings.username;
                    this.password = instanceSettings.password;
                    this.name = instanceSettings.name;
                    this.database = instanceSettings.database;
                    this.basicAuth = instanceSettings.basicAuth;
                    this.interval = (instanceSettings.jsonData || {}).timeInterval;
                    this.supportAnnotations = true;
                    this.supportMetrics = true;
                    this.responseParser = new response_parser_1.default();
                }
                InfluxDatasource.prototype.query = function (options) {
                    var _this = this;
                    var timeFilter = this.getTimeFilter(options);
                    var queryTargets = [];
                    var i, y;
                    var allQueries = lodash_1.default.map(options.targets, function (target) {
                        if (target.hide) {
                            return "";
                        }
                        queryTargets.push(target);
                        // build query
                        var queryModel = new influx_query_1.default(target, _this.templateSrv, options.scopedVars);
                        var query = queryModel.render(true);
                        query = query.replace(/\$interval/g, (target.interval || options.interval));
                        return query;
                    }).reduce(function (acc, current) {
                        if (current !== "") {
                            acc += ";" + current;
                        }
                        return acc;
                    });
                    // replace grafana variables
                    allQueries = allQueries.replace(/\$timeFilter/g, timeFilter);
                    // replace templated variables
                    allQueries = this.templateSrv.replace(allQueries, options.scopedVars);
                    return this._seriesQuery(allQueries).then(function (data) {
                        if (!data || !data.results) {
                            return [];
                        }
                        var seriesList = [];
                        for (i = 0; i < data.results.length; i++) {
                            var result = data.results[i];
                            if (!result || !result.series) {
                                continue;
                            }
                            var target = queryTargets[i];
                            var alias = target.alias;
                            if (alias) {
                                alias = _this.templateSrv.replace(target.alias, options.scopedVars);
                            }
                            var influxSeries = new influx_series_1.default({ series: data.results[i].series, alias: alias });
                            switch (target.resultFormat) {
                                case 'table': {
                                    seriesList.push(influxSeries.getTable());
                                    break;
                                }
                                default: {
                                    var timeSeries = influxSeries.getTimeSeries();
                                    for (y = 0; y < timeSeries.length; y++) {
                                        seriesList.push(timeSeries[y]);
                                    }
                                    break;
                                }
                            }
                        }
                        return { data: seriesList };
                    });
                };
                ;
                InfluxDatasource.prototype.annotationQuery = function (options) {
                    if (!options.annotation.query) {
                        return this.$q.reject({ message: 'Query missing in annotation definition' });
                    }
                    var timeFilter = this.getTimeFilter({ rangeRaw: options.rangeRaw });
                    var query = options.annotation.query.replace('$timeFilter', timeFilter);
                    query = this.templateSrv.replace(query, null, 'regex');
                    return this._seriesQuery(query).then(function (data) {
                        if (!data || !data.results || !data.results[0]) {
                            throw { message: 'No results in response from InfluxDB' };
                        }
                        return new influx_series_1.default({ series: data.results[0].series, annotation: options.annotation }).getAnnotations();
                    });
                };
                ;
                InfluxDatasource.prototype.metricFindQuery = function (query) {
                    var interpolated;
                    try {
                        interpolated = this.templateSrv.replace(query, null, 'regex');
                    }
                    catch (err) {
                        return this.$q.reject(err);
                    }
                    return this._seriesQuery(interpolated)
                        .then(lodash_1.default.curry(this.responseParser.parse)(query));
                };
                ;
                InfluxDatasource.prototype._seriesQuery = function (query) {
                    return this._influxRequest('GET', '/query', { q: query, epoch: 'ms' });
                };
                InfluxDatasource.prototype.serializeParams = function (params) {
                    if (!params) {
                        return '';
                    }
                    return lodash_1.default.reduce(params, function (memo, value, key) {
                        if (value === null || value === undefined) {
                            return memo;
                        }
                        memo.push(encodeURIComponent(key) + '=' + encodeURIComponent(value));
                        return memo;
                    }, []).join("&");
                };
                InfluxDatasource.prototype.testDatasource = function () {
                    return this.metricFindQuery('SHOW MEASUREMENTS LIMIT 1').then(function () {
                        return { status: "success", message: "Data source is working", title: "Success" };
                    });
                };
                InfluxDatasource.prototype._influxRequest = function (method, url, data) {
                    var self = this;
                    var currentUrl = self.urls.shift();
                    self.urls.push(currentUrl);
                    var params = {
                        u: self.username,
                        p: self.password,
                    };
                    if (self.database) {
                        params.db = self.database;
                    }
                    if (method === 'GET') {
                        lodash_1.default.extend(params, data);
                        data = null;
                    }
                    var options = {
                        method: method,
                        url: currentUrl + url,
                        params: params,
                        data: data,
                        precision: "ms",
                        inspect: { type: 'influxdb' },
                        paramSerializer: this.serializeParams,
                    };
                    options.headers = options.headers || {};
                    if (self.basicAuth) {
                        options.headers.Authorization = self.basicAuth;
                    }
                    return this.backendSrv.datasourceRequest(options).then(function (result) {
                        return result.data;
                    }, function (err) {
                        if (err.status !== 0 || err.status >= 300) {
                            if (err.data && err.data.error) {
                                throw { message: 'InfluxDB Error Response: ' + err.data.error, data: err.data, config: err.config };
                            }
                            else {
                                throw { message: 'InfluxDB Error: ' + err.message, data: err.data, config: err.config };
                            }
                        }
                    });
                };
                ;
                InfluxDatasource.prototype.getTimeFilter = function (options) {
                    var from = this.getInfluxTime(options.rangeRaw.from, false);
                    var until = this.getInfluxTime(options.rangeRaw.to, true);
                    var fromIsAbsolute = from[from.length - 1] === 's';
                    if (until === 'now()' && !fromIsAbsolute) {
                        return 'time > ' + from;
                    }
                    return 'time > ' + from + ' and time < ' + until;
                };
                InfluxDatasource.prototype.getInfluxTime = function (date, roundUp) {
                    if (lodash_1.default.isString(date)) {
                        if (date === 'now') {
                            return 'now()';
                        }
                        var parts = /^now-(\d+)([d|h|m|s])$/.exec(date);
                        if (parts) {
                            var amount = parseInt(parts[1]);
                            var unit = parts[2];
                            return 'now() - ' + amount + unit;
                        }
                        date = dateMath.parse(date, roundUp);
                    }
                    return (date.valueOf() / 1000).toFixed(0) + 's';
                };
                return InfluxDatasource;
            })();
            exports_1("default", InfluxDatasource);
        }
    }
});
//# sourceMappingURL=datasource.js.map