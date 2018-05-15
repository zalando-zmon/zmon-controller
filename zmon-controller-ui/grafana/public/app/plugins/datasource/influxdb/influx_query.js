///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', './query_part'], function(exports_1) {
    var lodash_1, query_part_1;
    var InfluxQuery;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (query_part_1_1) {
                query_part_1 = query_part_1_1;
            }],
        execute: function() {
            InfluxQuery = (function () {
                /** @ngInject */
                function InfluxQuery(target, templateSrv, scopedVars) {
                    this.target = target;
                    this.templateSrv = templateSrv;
                    this.scopedVars = scopedVars;
                    target.policy = target.policy || 'default';
                    target.dsType = 'influxdb';
                    target.resultFormat = target.resultFormat || 'time_series';
                    target.tags = target.tags || [];
                    target.groupBy = target.groupBy || [
                        { type: 'time', params: ['$interval'] },
                        { type: 'fill', params: ['null'] },
                    ];
                    target.select = target.select || [[
                            { type: 'field', params: ['value'] },
                            { type: 'mean', params: [] },
                        ]];
                    this.updateProjection();
                }
                InfluxQuery.prototype.updateProjection = function () {
                    this.selectModels = lodash_1.default.map(this.target.select, function (parts) {
                        return lodash_1.default.map(parts, query_part_1.default.create);
                    });
                    this.groupByParts = lodash_1.default.map(this.target.groupBy, query_part_1.default.create);
                };
                InfluxQuery.prototype.updatePersistedParts = function () {
                    this.target.select = lodash_1.default.map(this.selectModels, function (selectParts) {
                        return lodash_1.default.map(selectParts, function (part) {
                            return { type: part.def.type, params: part.params };
                        });
                    });
                };
                InfluxQuery.prototype.hasGroupByTime = function () {
                    return lodash_1.default.find(this.target.groupBy, function (g) { return g.type === 'time'; });
                };
                InfluxQuery.prototype.hasFill = function () {
                    return lodash_1.default.find(this.target.groupBy, function (g) { return g.type === 'fill'; });
                };
                InfluxQuery.prototype.addGroupBy = function (value) {
                    var stringParts = value.match(/^(\w+)\((.*)\)$/);
                    var typePart = stringParts[1];
                    var arg = stringParts[2];
                    var partModel = query_part_1.default.create({ type: typePart, params: [arg] });
                    var partCount = this.target.groupBy.length;
                    if (partCount === 0) {
                        this.target.groupBy.push(partModel.part);
                    }
                    else if (typePart === 'time') {
                        this.target.groupBy.splice(0, 0, partModel.part);
                    }
                    else if (typePart === 'tag') {
                        if (this.target.groupBy[partCount - 1].type === 'fill') {
                            this.target.groupBy.splice(partCount - 1, 0, partModel.part);
                        }
                        else {
                            this.target.groupBy.push(partModel.part);
                        }
                    }
                    else {
                        this.target.groupBy.push(partModel.part);
                    }
                    this.updateProjection();
                };
                InfluxQuery.prototype.removeGroupByPart = function (part, index) {
                    var categories = query_part_1.default.getCategories();
                    if (part.def.type === 'time') {
                        // remove fill
                        this.target.groupBy = lodash_1.default.filter(this.target.groupBy, function (g) { return g.type !== 'fill'; });
                        // remove aggregations
                        this.target.select = lodash_1.default.map(this.target.select, function (s) {
                            return lodash_1.default.filter(s, function (part) {
                                var partModel = query_part_1.default.create(part);
                                if (partModel.def.category === categories.Aggregations) {
                                    return false;
                                }
                                if (partModel.def.category === categories.Selectors) {
                                    return false;
                                }
                                return true;
                            });
                        });
                    }
                    this.target.groupBy.splice(index, 1);
                    this.updateProjection();
                };
                InfluxQuery.prototype.removeSelect = function (index) {
                    this.target.select.splice(index, 1);
                    this.updateProjection();
                };
                InfluxQuery.prototype.removeSelectPart = function (selectParts, part) {
                    // if we remove the field remove the whole statement
                    if (part.def.type === 'field') {
                        if (this.selectModels.length > 1) {
                            var modelsIndex = lodash_1.default.indexOf(this.selectModels, selectParts);
                            this.selectModels.splice(modelsIndex, 1);
                        }
                    }
                    else {
                        var partIndex = lodash_1.default.indexOf(selectParts, part);
                        selectParts.splice(partIndex, 1);
                    }
                    this.updatePersistedParts();
                };
                InfluxQuery.prototype.addSelectPart = function (selectParts, type) {
                    var partModel = query_part_1.default.create({ type: type });
                    partModel.def.addStrategy(selectParts, partModel, this);
                    this.updatePersistedParts();
                };
                InfluxQuery.prototype.renderTagCondition = function (tag, index, interpolate) {
                    var str = "";
                    var operator = tag.operator;
                    var value = tag.value;
                    if (index > 0) {
                        str = (tag.condition || 'AND') + ' ';
                    }
                    if (!operator) {
                        if (/^\/.*\/$/.test(value)) {
                            operator = '=~';
                        }
                        else {
                            operator = '=';
                        }
                    }
                    // quote value unless regex
                    if (operator !== '=~' && operator !== '!~') {
                        if (interpolate) {
                            value = this.templateSrv.replace(value, this.scopedVars);
                        }
                        if (operator !== '>' && operator !== '<') {
                            value = "'" + value.replace(/\\/g, '\\\\') + "'";
                        }
                    }
                    else if (interpolate) {
                        value = this.templateSrv.replace(value, this.scopedVars, 'regex');
                    }
                    return str + '"' + tag.key + '" ' + operator + ' ' + value;
                };
                InfluxQuery.prototype.getMeasurementAndPolicy = function (interpolate) {
                    var policy = this.target.policy;
                    var measurement = this.target.measurement || 'measurement';
                    if (!measurement.match('^/.*/')) {
                        measurement = '"' + measurement + '"';
                    }
                    else if (interpolate) {
                        measurement = this.templateSrv.replace(measurement, this.scopedVars, 'regex');
                    }
                    if (policy !== 'default') {
                        policy = '"' + this.target.policy + '".';
                    }
                    else {
                        policy = "";
                    }
                    return policy + measurement;
                };
                InfluxQuery.prototype.render = function (interpolate) {
                    var _this = this;
                    var target = this.target;
                    if (target.rawQuery) {
                        if (interpolate) {
                            return this.templateSrv.replace(target.query, this.scopedVars, 'regex');
                        }
                        else {
                            return target.query;
                        }
                    }
                    var query = 'SELECT ';
                    var i, y;
                    for (i = 0; i < this.selectModels.length; i++) {
                        var parts = this.selectModels[i];
                        var selectText = "";
                        for (y = 0; y < parts.length; y++) {
                            var part_1 = parts[y];
                            selectText = part_1.render(selectText);
                        }
                        if (i > 0) {
                            query += ', ';
                        }
                        query += selectText;
                    }
                    query += ' FROM ' + this.getMeasurementAndPolicy(interpolate) + ' WHERE ';
                    var conditions = lodash_1.default.map(target.tags, function (tag, index) {
                        return _this.renderTagCondition(tag, index, interpolate);
                    });
                    query += conditions.join(' ');
                    query += (conditions.length > 0 ? ' AND ' : '') + '$timeFilter';
                    var groupBySection = "";
                    for (i = 0; i < this.groupByParts.length; i++) {
                        var part = this.groupByParts[i];
                        if (i > 0) {
                            // for some reason fill has no seperator
                            groupBySection += part.def.type === 'fill' ? ' ' : ', ';
                        }
                        groupBySection += part.render('');
                    }
                    if (groupBySection.length) {
                        query += ' GROUP BY ' + groupBySection;
                    }
                    if (target.fill) {
                        query += ' fill(' + target.fill + ')';
                    }
                    return query;
                };
                return InfluxQuery;
            })();
            exports_1("default", InfluxQuery);
        }
    }
});
//# sourceMappingURL=influx_query.js.map