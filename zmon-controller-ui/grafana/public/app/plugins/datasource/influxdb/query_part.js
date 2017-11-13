///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', 'app/core/components/query_part/query_part'], function(exports_1) {
    var lodash_1, query_part_1;
    var index, categories, groupByTimeFunctions;
    function createPart(part) {
        var def = index[part.type];
        if (!def) {
            throw { message: 'Could not find query part ' + part.type };
        }
        return new query_part_1.QueryPart(part, def);
    }
    function register(options) {
        index[options.type] = new query_part_1.QueryPartDef(options);
        options.category.push(index[options.type]);
    }
    function aliasRenderer(part, innerExpr) {
        return innerExpr + ' AS ' + '"' + part.params[0] + '"';
    }
    function fieldRenderer(part, innerExpr) {
        if (part.params[0] === '*') {
            return '*';
        }
        return '"' + part.params[0] + '"';
    }
    function replaceAggregationAddStrategy(selectParts, partModel) {
        // look for existing aggregation
        for (var i = 0; i < selectParts.length; i++) {
            var part = selectParts[i];
            if (part.def.category === categories.Aggregations) {
                selectParts[i] = partModel;
                return;
            }
            if (part.def.category === categories.Selectors) {
                selectParts[i] = partModel;
                return;
            }
        }
        selectParts.splice(1, 0, partModel);
    }
    function addTransformationStrategy(selectParts, partModel) {
        var i;
        // look for index to add transformation
        for (i = 0; i < selectParts.length; i++) {
            var part = selectParts[i];
            if (part.def.category === categories.Math || part.def.category === categories.Aliasing) {
                break;
            }
        }
        selectParts.splice(i, 0, partModel);
    }
    function addMathStrategy(selectParts, partModel) {
        var partCount = selectParts.length;
        if (partCount > 0) {
            // if last is math, replace it
            if (selectParts[partCount - 1].def.type === 'math') {
                selectParts[partCount - 1] = partModel;
                return;
            }
            // if next to last is math, replace it
            if (selectParts[partCount - 2].def.type === 'math') {
                selectParts[partCount - 2] = partModel;
                return;
            }
            else if (selectParts[partCount - 1].def.type === 'alias') {
                selectParts.splice(partCount - 1, 0, partModel);
                return;
            }
        }
        selectParts.push(partModel);
    }
    function addAliasStrategy(selectParts, partModel) {
        var partCount = selectParts.length;
        if (partCount > 0) {
            // if last is alias, replace it
            if (selectParts[partCount - 1].def.type === 'alias') {
                selectParts[partCount - 1] = partModel;
                return;
            }
        }
        selectParts.push(partModel);
    }
    function addFieldStrategy(selectParts, partModel, query) {
        // copy all parts
        var parts = lodash_1.default.map(selectParts, function (part) {
            return createPart({ type: part.def.type, params: lodash_1.default.clone(part.params) });
        });
        query.selectModels.push(parts);
    }
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (query_part_1_1) {
                query_part_1 = query_part_1_1;
            }],
        execute: function() {
            index = [];
            categories = {
                Aggregations: [],
                Selectors: [],
                Transformations: [],
                Math: [],
                Aliasing: [],
                Fields: [],
            };
            ;
            groupByTimeFunctions = [];
            register({
                type: 'field',
                addStrategy: addFieldStrategy,
                category: categories.Fields,
                params: [{ type: 'field', dynamicLookup: true }],
                defaultParams: ['value'],
                renderer: fieldRenderer,
            });
            // Aggregations
            register({
                type: 'count',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Aggregations,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'distinct',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Aggregations,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'integral',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Aggregations,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'mean',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Aggregations,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'median',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Aggregations,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'sum',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Aggregations,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            // transformations
            register({
                type: 'derivative',
                addStrategy: addTransformationStrategy,
                category: categories.Transformations,
                params: [{ name: "duration", type: "interval", options: ['1s', '10s', '1m', '5m', '10m', '15m', '1h'] }],
                defaultParams: ['10s'],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'spread',
                addStrategy: addTransformationStrategy,
                category: categories.Transformations,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'non_negative_derivative',
                addStrategy: addTransformationStrategy,
                category: categories.Transformations,
                params: [{ name: "duration", type: "interval", options: ['1s', '10s', '1m', '5m', '10m', '15m', '1h'] }],
                defaultParams: ['10s'],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'difference',
                addStrategy: addTransformationStrategy,
                category: categories.Transformations,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'moving_average',
                addStrategy: addTransformationStrategy,
                category: categories.Transformations,
                params: [{ name: "window", type: "number", options: [5, 10, 20, 30, 40] }],
                defaultParams: [10],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'stddev',
                addStrategy: addTransformationStrategy,
                category: categories.Transformations,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'time',
                category: groupByTimeFunctions,
                params: [{ name: "interval", type: "time", options: ['auto', '1s', '10s', '1m', '5m', '10m', '15m', '1h'] }],
                defaultParams: ['auto'],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'fill',
                category: groupByTimeFunctions,
                params: [{ name: "fill", type: "string", options: ['none', 'null', '0', 'previous'] }],
                defaultParams: ['null'],
                renderer: query_part_1.functionRenderer,
            });
            // Selectors
            register({
                type: 'bottom',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Selectors,
                params: [{ name: 'count', type: 'int' }],
                defaultParams: [3],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'first',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Selectors,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'last',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Selectors,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'max',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Selectors,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'min',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Selectors,
                params: [],
                defaultParams: [],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'percentile',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Selectors,
                params: [{ name: 'nth', type: 'int' }],
                defaultParams: [95],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'top',
                addStrategy: replaceAggregationAddStrategy,
                category: categories.Selectors,
                params: [{ name: 'count', type: 'int' }],
                defaultParams: [3],
                renderer: query_part_1.functionRenderer,
            });
            register({
                type: 'tag',
                category: groupByTimeFunctions,
                params: [{ name: 'tag', type: 'string', dynamicLookup: true }],
                defaultParams: ['tag'],
                renderer: fieldRenderer,
            });
            register({
                type: 'math',
                addStrategy: addMathStrategy,
                category: categories.Math,
                params: [{ name: "expr", type: "string" }],
                defaultParams: [' / 100'],
                renderer: query_part_1.suffixRenderer,
            });
            register({
                type: 'alias',
                addStrategy: addAliasStrategy,
                category: categories.Aliasing,
                params: [{ name: "name", type: "string", quote: 'double' }],
                defaultParams: ['alias'],
                renderMode: 'suffix',
                renderer: aliasRenderer,
            });
            exports_1("default",{
                create: createPart,
                getCategories: function () {
                    return categories;
                }
            });
        }
    }
});
//# sourceMappingURL=query_part.js.map