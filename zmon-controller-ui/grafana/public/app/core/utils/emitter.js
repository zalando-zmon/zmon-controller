///<reference path="../../headers/common.d.ts" />
System.register(['eventemitter3'], function(exports_1) {
    var eventemitter3_1;
    var hasOwnProp, Emitter;
    function createName(name) {
        return '$' + name;
    }
    return {
        setters:[
            function (eventemitter3_1_1) {
                eventemitter3_1 = eventemitter3_1_1;
            }],
        execute: function() {
            hasOwnProp = {}.hasOwnProperty;
            Emitter = (function () {
                function Emitter() {
                    this.emitter = new eventemitter3_1.default();
                }
                Emitter.prototype.emit = function (name, data) {
                    this.emitter.emit(name, data);
                };
                Emitter.prototype.on = function (name, handler, scope) {
                    var _this = this;
                    this.emitter.on(name, handler);
                    if (scope) {
                        scope.$on('$destroy', function () {
                            _this.emitter.off(name, handler);
                        });
                    }
                };
                Emitter.prototype.off = function (name, handler) {
                    this.emitter.off(name, handler);
                };
                return Emitter;
            })();
            exports_1("Emitter", Emitter);
        }
    }
});
//# sourceMappingURL=emitter.js.map