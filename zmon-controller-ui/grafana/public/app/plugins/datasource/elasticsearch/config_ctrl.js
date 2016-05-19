///<reference path="../../../headers/common.d.ts" />
System.register(['lodash'], function(exports_1) {
    var lodash_1;
    var ElasticConfigCtrl;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            }],
        execute: function() {
            ElasticConfigCtrl = (function () {
                /** @ngInject */
                function ElasticConfigCtrl($scope) {
                    this.indexPatternTypes = [
                        { name: 'No pattern', value: undefined },
                        { name: 'Hourly', value: 'Hourly', example: '[logstash-]YYYY.MM.DD.HH' },
                        { name: 'Daily', value: 'Daily', example: '[logstash-]YYYY.MM.DD' },
                        { name: 'Weekly', value: 'Weekly', example: '[logstash-]GGGG.WW' },
                        { name: 'Monthly', value: 'Monthly', example: '[logstash-]YYYY.MM' },
                        { name: 'Yearly', value: 'Yearly', example: '[logstash-]YYYY' },
                    ];
                    this.esVersions = [
                        { name: '1.x', value: 1 },
                        { name: '2.x', value: 2 },
                    ];
                    this.current.jsonData.timeField = this.current.jsonData.timeField || '@timestamp';
                }
                ElasticConfigCtrl.prototype.indexPatternTypeChanged = function () {
                    var def = lodash_1.default.findWhere(this.indexPatternTypes, { value: this.current.jsonData.interval });
                    this.current.database = def.example || 'es-index-name';
                };
                ElasticConfigCtrl.templateUrl = 'public/app/plugins/datasource/elasticsearch/partials/config.html';
                return ElasticConfigCtrl;
            })();
            exports_1("ElasticConfigCtrl", ElasticConfigCtrl);
        }
    }
});
//# sourceMappingURL=config_ctrl.js.map