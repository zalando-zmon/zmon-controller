///<reference path="../headers/common.d.ts" />
System.register(['./utils/emitter'], function(exports_1) {
    var emitter_1;
    var appEvents;
    return {
        setters:[
            function (emitter_1_1) {
                emitter_1 = emitter_1_1;
            }],
        execute: function() {
            appEvents = new emitter_1.Emitter();
            exports_1("default",appEvents);
        }
    }
});
//# sourceMappingURL=app_events.js.map