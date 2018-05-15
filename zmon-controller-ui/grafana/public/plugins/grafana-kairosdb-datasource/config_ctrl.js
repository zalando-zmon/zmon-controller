System.register([], function(exports_1) {
    var KairosDBConfigCtrl;
    return {
        setters:[],
        execute: function() {
            KairosDBConfigCtrl = (function () {
                /** @ngInject */
                function KairosDBConfigCtrl($scope) {
                    this.current.jsonData = this.current.jsonData || {};
                }
                KairosDBConfigCtrl.templateUrl = 'partials/config.html';
                return KairosDBConfigCtrl;
            })();
            exports_1("KairosDBConfigCtrl", KairosDBConfigCtrl);
        }
    }
});
//# sourceMappingURL=config_ctrl.js.map