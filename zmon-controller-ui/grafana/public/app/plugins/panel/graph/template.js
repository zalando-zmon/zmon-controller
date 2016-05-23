System.register([], function(exports_1) {
    var template;
    return {
        setters:[],
        execute: function() {
            template = "\n<div class=\"graph-wrapper\" ng-class=\"{'graph-legend-rightside': ctrl.panel.legend.rightSide}\">\n  <div class=\"graph-canvas-wrapper\">\n\n    <div ng-if=\"datapointsWarning\" class=\"datapoints-warning\">\n      <span class=\"small\" ng-show=\"!datapointsCount\">\n        No datapoints <tip>No datapoints returned from metric query</tip>\n      </span>\n      <span class=\"small\" ng-show=\"datapointsOutside\">\n        Datapoints outside time range\n        <tip>Can be caused by timezone mismatch between browser and graphite server</tip>\n      </span>\n    </div>\n\n    <div grafana-graph class=\"histogram-chart\" ng-dblclick=\"ctrl.zoomOut()\">\n    </div>\n\n  </div>\n\n  <div class=\"graph-legend-wrapper\" graph-legend></div>\n  </div>\n\n<div class=\"clearfix\"></div>\n";
            exports_1("default",template);
        }
    }
});
//# sourceMappingURL=template.js.map