///<reference path="../../app/headers/common.d.ts" />
define(["require", "exports"], function (require, exports) {
    var _global = (window);
    var beforeEach = _global.beforeEach;
    exports.beforeEach = beforeEach;
    var describe = _global.describe;
    exports.describe = describe;
    var it = _global.it;
    exports.it = it;
    var sinon = _global.sinon;
    exports.sinon = sinon;
    var expect = _global.expect;
    exports.expect = expect;
    var angularMocks = {
        module: _global.module,
    };
    exports.angularMocks = angularMocks;
});
//# sourceMappingURL=common.js.map