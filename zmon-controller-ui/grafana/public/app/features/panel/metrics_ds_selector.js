///<reference path="../../headers/common.d.ts" />
System.register(['angular', 'lodash'], function(exports_1) {
    var angular_1, lodash_1;
    var module, template, MetricsDsSelectorCtrl;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            }],
        execute: function() {
            module = angular_1.default.module('grafana.directives');
            template = "\n<div class=\"gf-form-group\">\n  <div class=\"gf-form-inline\">\n    <div class=\"gf-form\">\n      <label class=\"gf-form-label\">\n        <i class=\"icon-gf icon-gf-datasources\"></i>\n      </label>\n      <label class=\"gf-form-label\">\n        Panel data source\n      </label>\n\n      <metric-segment segment=\"ctrl.dsSegment\" style-mode=\"select\"\n                      get-options=\"ctrl.getOptions()\"\n                      on-change=\"ctrl.datasourceChanged()\"></metric-segment>\n    </div>\n\n    <div class=\"gf-form gf-form--offset-1\">\n      <button class=\"btn btn-inverse gf-form-btn\" ng-click=\"ctrl.addDataQuery()\" ng-hide=\"ctrl.current.meta.mixed\">\n        <i class=\"fa fa-plus\"></i>&nbsp;\n        Add query\n      </button>\n\n      <div class=\"dropdown\" ng-if=\"ctrl.current.meta.mixed\">\n        <button class=\"btn btn-inverse dropdown-toggle gf-form-btn\" data-toggle=\"dropdown\">\n          Add Query&nbsp;<span class=\"fa fa-caret-down\"></span>\n        </button>\n\n        <ul class=\"dropdown-menu\" role=\"menu\">\n          <li ng-repeat=\"datasource in ctrl.datasources\" role=\"menuitem\" ng-hide=\"datasource.meta.builtIn\">\n            <a ng-click=\"ctrl.addDataQuery(datasource);\">{{datasource.name}}</a>\n          </li>\n        </ul>\n      </div>\n    </div>\n  </div>\n</div>\n";
            MetricsDsSelectorCtrl = (function () {
                /** @ngInject */
                function MetricsDsSelectorCtrl(uiSegmentSrv, datasourceSrv) {
                    this.uiSegmentSrv = uiSegmentSrv;
                    this.datasources = datasourceSrv.getMetricSources();
                    var dsValue = this.panelCtrl.panel.datasource || null;
                    for (var _i = 0, _a = this.datasources; _i < _a.length; _i++) {
                        var ds = _a[_i];
                        if (ds.value === dsValue) {
                            this.current = ds;
                        }
                    }
                    if (!this.current) {
                        this.current = { name: dsValue + ' not found', value: null };
                    }
                    this.dsSegment = uiSegmentSrv.newSegment(this.current.name);
                }
                MetricsDsSelectorCtrl.prototype.getOptions = function () {
                    var _this = this;
                    return Promise.resolve(this.datasources.map(function (value) {
                        return _this.uiSegmentSrv.newSegment(value.name);
                    }));
                };
                MetricsDsSelectorCtrl.prototype.datasourceChanged = function () {
                    var ds = lodash_1.default.findWhere(this.datasources, { name: this.dsSegment.value });
                    if (ds) {
                        this.current = ds;
                        this.panelCtrl.setDatasource(ds);
                    }
                };
                MetricsDsSelectorCtrl.prototype.addDataQuery = function (datasource) {
                    var target = { isNew: true };
                    if (datasource) {
                        target.datasource = datasource.name;
                    }
                    this.panelCtrl.panel.targets.push(target);
                };
                return MetricsDsSelectorCtrl;
            })();
            exports_1("MetricsDsSelectorCtrl", MetricsDsSelectorCtrl);
            module.directive('metricsDsSelector', function () {
                return {
                    restrict: 'E',
                    template: template,
                    controller: MetricsDsSelectorCtrl,
                    bindToController: true,
                    controllerAs: 'ctrl',
                    transclude: true,
                    scope: {
                        panelCtrl: "="
                    }
                };
            });
        }
    }
});
//# sourceMappingURL=metrics_ds_selector.js.map