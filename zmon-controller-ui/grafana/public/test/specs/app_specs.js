System.register(['test/lib/common', 'app/app'], function(exports_1) {
    var common_1, app_1;
    return {
        setters:[
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (app_1_1) {
                app_1 = app_1_1;
            }],
        execute: function() {
            common_1.describe('GrafanaApp', function () {
                var app = new app_1.GrafanaApp();
                common_1.it('can call inits', function () {
                    common_1.expect(app).to.not.be(null);
                });
            });
        }
    }
});
//# sourceMappingURL=app_specs.js.map