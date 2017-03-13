var GAUGE_LAST_ID = 0;
angular.module('zmon2App').directive('gauge', function() {
    return {
        restrict: 'E',
        scope: {
            title: '@title',
            value: '=value',
            max: '=max',
            options: '=options'
        },
        link: function(scope, elem, attrs) {
            GAUGE_LAST_ID++;
            var gaugeId = GAUGE_LAST_ID;
            elem.append('<div class="gauge-wrapper" id="gauge-' + gaugeId + '"></div>');

            var g = null;
            scope._options = {min: 0, max: null, reverse: false};

            if (typeof scope.options !== 'undefined') {
                _.extend(scope._options, scope.options);
            }
            var colors = ['#78B924', '#ebb129', '#ebb129', '#dd3010'];

            if (scope._options.reverse) {
                colors.reverse();
            }

            var getFormatedValue = function(format, value) {
                if (!_.isNumber(value)) {
                    return value;
                }

                // extract format value if in {:} enclosing
                var match = format.match(/\{\:(\.[0-9]f)\}/);

                if (match && match.length) {
                    format = match[1];
                }

                return d3.format(format)(value);
            }

            var refreshGauge = function() {
                scope.value = scope.value/1 || 0;
                scope.max = scope.max/1 || 100;

                // Till we iron out all error possibilities with stacktrace, let's use a try catch.
                try {
                    if (g === null) {
                        g = new JustGage({
                            id: "gauge-" + gaugeId,
                            value: scope.options.format ? getFormatedValue(scope.options.format, scope.value)/1 : scope.value.toFixed(0)/1,
                            min: scope._options.min/1,
                            gaugeColor: '#7f7f7f',
                            max: scope._options.max === null ? scope.max.toFixed(0)/1 : scope._options.max/1,
                            title: " ",
                            valueFontColor: '#fff',
                            showInnerShadow: true,
                            shadowOpacity: 0.8,
                            shadowSize: 4,
                            shadowVerticalOffset: 2,
                            levelColors: colors
                        });
                    } else {
                        if (scope._options.max === null) {
                            var newMax = scope.max.toFixed(0)/1;
                            g.config.max = newMax;
                            g.txtMax.attr('text', newMax);
                        } else {
                            g.config.max = scope._options.max.toFixed(0)/1;
                            g.txtMax.attr('text', scope._options.max.toFixed(0));
                        }
                        if (scope._options.min === null) {
                            var newMin = scope.min.toFixed(0)/1;
                            g.config.min = newMin;
                            g.txtMin.attr('text', newMin);
                        } else {
                            g.config.min = scope._options.min.toFixed(0)/1;
                            g.txtMin.attr('text', scope._options.min.toFixed(0));
                        }

                        g.refresh(scope.options.format ? getFormatedValue(scope.options.format,scope.value)/1 : scope.value.toFixed(0)/1);
                    }
                } catch (ex) {
                    console.error("ERROR Gauge:", ex);
                }
            };

            scope.$watch('options', function(options) {
                if (!isNaN(scope.value)) {
                    scope._options = {min: 0, max: null, reverse: false};
                    if (typeof scope.options !== 'undefined') {
                        _.extend(scope._options, scope.options);
                    }
                    refreshGauge();
                }
            }, true);

            scope.$watch('value', function(newVal, oldVal) {
                if (isNaN(newVal)) {
                    console.warn(new Date(), "Gauge value is not a number: ", newVal);
                } else {
                    refreshGauge();
                }
            });
        }
    };
});
