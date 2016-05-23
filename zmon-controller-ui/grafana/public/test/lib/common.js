///<reference path="../../app/headers/common.d.ts" />
System.register([], function(exports_1) {
    var _global, beforeEach, before, describe, it, sinon, expect, angularMocks;
    return {
        setters:[],
        execute: function() {
            _global = (window);
            beforeEach = _global.beforeEach;
            before = _global.before;
            describe = _global.describe;
            it = _global.it;
            sinon = _global.sinon;
            expect = _global.expect;
            angularMocks = {
                module: _global.module,
                inject: _global.inject,
            };
            exports_1("beforeEach", beforeEach);
            exports_1("before", before);
            exports_1("describe", describe);
            exports_1("it", it);
            exports_1("sinon", sinon);
            exports_1("expect", expect);
            exports_1("angularMocks", angularMocks);
        }
    }
});
//# sourceMappingURL=common.js.map