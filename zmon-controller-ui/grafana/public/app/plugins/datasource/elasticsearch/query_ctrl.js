///<reference path="../../../headers/common.d.ts" />
System.register(['./bucket_agg', './metric_agg', 'angular', 'lodash', './query_def', 'app/plugins/sdk'], function(exports_1) {
    var __extends = (this && this.__extends) || function (d, b) {
        for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
    var angular_1, lodash_1, query_def_1, sdk_1;
    var ElasticQueryCtrl;
    return {
        setters:[
            function (_1) {},
            function (_2) {},
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (query_def_1_1) {
                query_def_1 = query_def_1_1;
            },
            function (sdk_1_1) {
                sdk_1 = sdk_1_1;
            }],
        execute: function() {
            ElasticQueryCtrl = (function (_super) {
                __extends(ElasticQueryCtrl, _super);
                /** @ngInject **/
                function ElasticQueryCtrl($scope, $injector, $rootScope, $timeout, uiSegmentSrv) {
                    _super.call(this, $scope, $injector);
                    this.$rootScope = $rootScope;
                    this.$timeout = $timeout;
                    this.uiSegmentSrv = uiSegmentSrv;
                    this.esVersion = this.datasource.esVersion;
                    this.queryUpdated();
                }
                ElasticQueryCtrl.prototype.getFields = function (type) {
                    var jsonStr = angular_1.default.toJson({ find: 'fields', type: type });
                    return this.datasource.metricFindQuery(jsonStr)
                        .then(this.uiSegmentSrv.transformToSegments(false))
                        .catch(this.handleQueryError.bind(this));
                };
                ElasticQueryCtrl.prototype.queryUpdated = function () {
                    var newJson = angular_1.default.toJson(this.datasource.queryBuilder.build(this.target), true);
                    if (newJson !== this.rawQueryOld) {
                        this.rawQueryOld = newJson;
                        this.refresh();
                    }
                    this.$rootScope.appEvent('elastic-query-updated');
                };
                ElasticQueryCtrl.prototype.getCollapsedText = function () {
                    var metricAggs = this.target.metrics;
                    var bucketAggs = this.target.bucketAggs;
                    var metricAggTypes = query_def_1.default.getMetricAggTypes(this.esVersion);
                    var bucketAggTypes = query_def_1.default.bucketAggTypes;
                    var text = '';
                    if (this.target.query) {
                        text += 'Query: ' + this.target.query + ', ';
                    }
                    text += 'Metrics: ';
                    lodash_1.default.each(metricAggs, function (metric, index) {
                        var aggDef = lodash_1.default.findWhere(metricAggTypes, { value: metric.type });
                        text += aggDef.text + '(';
                        if (aggDef.requiresField) {
                            text += metric.field;
                        }
                        text += '), ';
                    });
                    lodash_1.default.each(bucketAggs, function (bucketAgg, index) {
                        if (index === 0) {
                            text += ' Group by: ';
                        }
                        var aggDef = lodash_1.default.findWhere(bucketAggTypes, { value: bucketAgg.type });
                        text += aggDef.text + '(';
                        if (aggDef.requiresField) {
                            text += bucketAgg.field;
                        }
                        text += '), ';
                    });
                    if (this.target.alias) {
                        text += 'Alias: ' + this.target.alias;
                    }
                    return text;
                };
                ElasticQueryCtrl.prototype.handleQueryError = function (err) {
                    this.error = err.message || 'Failed to issue metric query';
                    return [];
                };
                ElasticQueryCtrl.templateUrl = 'partials/query.editor.html';
                return ElasticQueryCtrl;
            })(sdk_1.QueryCtrl);
            exports_1("ElasticQueryCtrl", ElasticQueryCtrl);
        }
    }
});
//# sourceMappingURL=query_ctrl.js.map