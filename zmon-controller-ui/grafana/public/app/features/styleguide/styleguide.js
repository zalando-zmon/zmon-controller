System.register(['app/core/core_module', 'app/core/config', 'lodash'], function(exports_1) {
    var core_module_1, config_1, lodash_1;
    var StyleGuideCtrl;
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            },
            function (config_1_1) {
                config_1 = config_1_1;
            },
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            }],
        execute: function() {
            StyleGuideCtrl = (function () {
                /** @ngInject **/
                function StyleGuideCtrl($http, $routeParams) {
                    this.$http = $http;
                    this.colors = [];
                    this.buttonNames = ['primary', 'secondary', 'inverse', 'success', 'warning', 'danger'];
                    this.buttonSizes = ['btn-small', '', 'btn-large'];
                    this.buttonVariants = ['-', '-outline-'];
                    this.pages = ['colors', 'buttons'];
                    this.theme = config_1.default.bootData.user.lightTheme ? 'light' : 'dark';
                    this.page = {};
                    if ($routeParams.page) {
                        this.page[$routeParams.page] = 1;
                    }
                    else {
                        this.page.colors = true;
                    }
                    if (this.page.colors) {
                        this.loadColors();
                    }
                }
                StyleGuideCtrl.prototype.loadColors = function () {
                    var _this = this;
                    this.$http.get('public/sass/styleguide.json').then(function (res) {
                        _this.colors = lodash_1.default.map(res.data[_this.theme], function (value, key) {
                            return { name: key, value: value };
                        });
                    });
                };
                StyleGuideCtrl.prototype.switchTheme = function () {
                    var other = this.theme === 'dark' ? 'light' : 'dark';
                    window.location.href = window.location.href + '?theme=' + other;
                };
                return StyleGuideCtrl;
            })();
            core_module_1.default.controller('StyleGuideCtrl', StyleGuideCtrl);
        }
    }
});
//# sourceMappingURL=styleguide.js.map