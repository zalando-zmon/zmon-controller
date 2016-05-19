///<reference path="../../headers/common.d.ts" />
System.register(['../core_module'], function(exports_1) {
    var core_module_1;
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            core_module_1.default.directive('giveFocus', function () {
                return function (scope, element, attrs) {
                    element.click(function (e) {
                        e.stopPropagation();
                    });
                    scope.$watch(attrs.giveFocus, function (newValue) {
                        if (!newValue) {
                            return;
                        }
                        setTimeout(function () {
                            element.focus();
                            var domEl = element[0];
                            if (domEl.setSelectionRange) {
                                var pos = element.val().length * 2;
                                domEl.setSelectionRange(pos, pos);
                            }
                        }, 200);
                    }, true);
                };
            });
            exports_1("default",{});
        }
    }
});
//# sourceMappingURL=give_focus.js.map