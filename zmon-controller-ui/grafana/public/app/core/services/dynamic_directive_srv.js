///<reference path="../../headers/common.d.ts" />
System.register(['angular', '../core_module'], function(exports_1) {
    var angular_1, core_module_1;
    var DynamicDirectiveSrv;
    return {
        setters:[
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            DynamicDirectiveSrv = (function () {
                /** @ngInject */
                function DynamicDirectiveSrv($compile, $parse, $rootScope) {
                    this.$compile = $compile;
                    this.$parse = $parse;
                    this.$rootScope = $rootScope;
                }
                DynamicDirectiveSrv.prototype.addDirective = function (element, name, scope) {
                    var child = angular_1.default.element(document.createElement(name));
                    this.$compile(child)(scope);
                    element.empty();
                    element.append(child);
                };
                DynamicDirectiveSrv.prototype.link = function (scope, elem, attrs, options) {
                    var _this = this;
                    options.directive(scope).then(function (directiveInfo) {
                        if (!directiveInfo || !directiveInfo.fn) {
                            elem.empty();
                            return;
                        }
                        if (!directiveInfo.fn.registered) {
                            core_module_1.default.directive(attrs.$normalize(directiveInfo.name), directiveInfo.fn);
                            directiveInfo.fn.registered = true;
                        }
                        _this.addDirective(elem, directiveInfo.name, scope);
                    }).catch(function (err) {
                        console.log('Plugin load:', err);
                        _this.$rootScope.appEvent('alert-error', ['Plugin error', err.toString()]);
                    });
                };
                DynamicDirectiveSrv.prototype.create = function (options) {
                    var _this = this;
                    var directiveDef = {
                        restrict: 'E',
                        scope: options.scope,
                        link: function (scope, elem, attrs) {
                            if (options.watchPath) {
                                var childScope = null;
                                scope.$watch(options.watchPath, function () {
                                    if (childScope) {
                                        childScope.$destroy();
                                    }
                                    childScope = scope.$new();
                                    _this.link(childScope, elem, attrs, options);
                                });
                            }
                            else {
                                _this.link(scope, elem, attrs, options);
                            }
                        }
                    };
                    return directiveDef;
                };
                return DynamicDirectiveSrv;
            })();
            core_module_1.default.service('dynamicDirectiveSrv', DynamicDirectiveSrv);
        }
    }
});
//# sourceMappingURL=dynamic_directive_srv.js.map