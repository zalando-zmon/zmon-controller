///<reference path="../../headers/common.d.ts" />
System.register(['lodash', 'angular', 'moment', '../core_module'], function(exports_1) {
    var lodash_1, angular_1, moment_1, core_module_1;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            core_module_1.default.filter('stringSort', function () {
                return function (input) {
                    return input.sort();
                };
            });
            core_module_1.default.filter('slice', function () {
                return function (arr, start, end) {
                    if (!lodash_1.default.isUndefined(arr)) {
                        return arr.slice(start, end);
                    }
                };
            });
            core_module_1.default.filter('stringify', function () {
                return function (arr) {
                    if (lodash_1.default.isObject(arr) && !lodash_1.default.isArray(arr)) {
                        return angular_1.default.toJson(arr);
                    }
                    else {
                        return lodash_1.default.isNull(arr) ? null : arr.toString();
                    }
                };
            });
            core_module_1.default.filter('moment', function () {
                return function (date, mode) {
                    switch (mode) {
                        case 'ago':
                            return moment_1.default(date).fromNow();
                    }
                    return moment_1.default(date).fromNow();
                };
            });
            core_module_1.default.filter('noXml', function () {
                var noXml = function (text) {
                    return lodash_1.default.isString(text)
                        ? text
                            .replace(/&/g, '&amp;')
                            .replace(/</g, '&lt;')
                            .replace(/>/g, '&gt;')
                            .replace(/'/g, '&#39;')
                            .replace(/"/g, '&quot;')
                        : text;
                };
                return function (text) {
                    return lodash_1.default.isArray(text)
                        ? lodash_1.default.map(text, noXml)
                        : noXml(text);
                };
            });
            core_module_1.default.filter('interpolateTemplateVars', function (templateSrv) {
                var filterFunc = function (text, scope) {
                    var scopedVars;
                    if (scope.ctrl && scope.ctrl.panel) {
                        scopedVars = scope.ctrl.panel.scopedVars;
                    }
                    else {
                        scopedVars = scope.row.scopedVars;
                    }
                    return templateSrv.replaceWithText(text, scopedVars);
                };
                filterFunc.$stateful = true;
                return filterFunc;
            });
            exports_1("default",{});
        }
    }
});
//# sourceMappingURL=filters.js.map