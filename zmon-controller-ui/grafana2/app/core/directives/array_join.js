///<reference path="../../headers/common.d.ts" />
define(["require", "exports", 'lodash', '../core_module'], function (require, exports, _, coreModule) {
    function arrayJoin() {
        'use strict';
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                function split_array(text) {
                    return (text || '').split(',');
                }
                function join_array(text) {
                    if (_.isArray(text)) {
                        return (text || '').join(',');
                    }
                    else {
                        return text;
                    }
                }
                ngModel.$parsers.push(split_array);
                ngModel.$formatters.push(join_array);
            }
        };
    }
    exports.arrayJoin = arrayJoin;
    coreModule.directive('arrayJoin', arrayJoin);
});
//# sourceMappingURL=array_join.js.map