angular.module('zmon2App').directive('chartd3', [ function() {
    return {
        restrict: 'E',
        templateUrl: 'templates/chart.html',
        scope: {
            data: '=chartData',
            id: '=id'
        },
        link: function(scope, elem, attrs) {

            var margin = {top: 20, right: 20, bottom: 30, left: 50},
                width = 300 - margin.left - margin.right,
                height = 120 - margin.top - margin.bottom;

            var x = d3.time.scale()
                        .range([0, width]);

            var y = d3.scale.linear()
                        .range([height, 0]);

            var yScale = d3.scale.ordinal()
                        .range([height,0]);

            var yAxis = d3.svg.axis()
                        .scale(yScale)
                        .orient("left")
                        .ticks(4)
                        .tickFormat(function(v) { 
                            var p = d3.formatPrefix(v);
                            return p.scale(v).toFixed() + p.symbol;
                        });

            var area = d3.svg.area()
                        .x(function(d) { return x(d[0]); })
                        .y0(height)
                        .y1(function(d) { return y(d[1]); });

            var format = d3.time.format("%H:%M:%S");

            scope.$watch('data', function(data) {

                if (data && data.length) {

                    d3.selectAll("svg#area-"+ scope.id + " g").remove();

                    var svg = d3.select("svg#area-"+scope.id)
                                .attr("width", width + margin.left + margin.right)
                                .attr("height", height + margin.top + margin.bottom)
                                .append("g")
                                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                    var px = {
                        max: d3.max(data, function(d) {
                            return d3.max(d, function(_d) {
                                return _d[0];
                            });
                        }),
                        min: d3.min(data, function(d) {
                            return d3.min(d, function(_d) {
                                return _d[0];
                            });
                        })
                    };

                    var py = {
                        max: d3.max(data, function(d) {
                            return d3.max(d, function(_d) {
                                return _d[1];
                            });
                        }),
                        min: 0
                    };

                    x.domain([px.min, px.max]);
                    y.domain([py.min, py.max]);
                    yScale.domain([py.min,py.max]);

                    _.each(data, function(d) {
                        svg.append("path")
                            .datum(d)
                            .attr("class", "area")
                            .attr("stroke", "white")
                            .attr("d", area);
                    });

                    svg.append("g")
                        .attr("class", "y axis")
                        .call(yAxis);

                    svg.selectAll(".hlines")
                        .filter(function(d,i) {
                            return i === 0 ? null : this;
                        })
                        .data(d3.range(3)).enter()
                            .append("line")
                            .attr("class","hlines")
                            .attr("x1", 0)
                            .attr("y1", function(d, i) {
                                return height * d / 3;
                            })
                            .attr("x2", width)
                            .attr("y2", function(d, i) {
                                return height * d/3;
                            });

                    svg.selectAll(".hlines")
                        .filter(function(d, i) {
                            return i === 0 ? null : d%3 === 0;
                        })
                        .style("stroke-opacity",0.7);

                    svg.append("svg:text")
                        .attr("y", height + 8)
                        .attr("x", 0)
                        .attr("dy", ".71em")
                        .attr("class", "ts")
                        .text(format(new Date(px.min*1000)));

                    svg.append("svg:text")
                        .attr("y", height + 8)
                        .attr("x", width - 32)
                        .attr("dy", ".71em")
                        .attr("class", "ts")
                        .text(format(new Date(px.max*1000)));
                }
            });
        }
    };
}]);
