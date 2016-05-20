///<reference path="../../headers/common.d.ts" />
System.register(['lodash', 'app/core/core_module', 'tether-drop'], function(exports_1) {
    var lodash_1, core_module_1, tether_drop_1;
    /** @ngInject **/
    function popoverSrv($compile, $rootScope) {
        this.show = function (options) {
            var popoverScope = lodash_1.default.extend($rootScope.$new(true), options.model);
            var drop;
            function destroyDrop() {
                setTimeout(function () {
                    if (drop.tether) {
                        drop.destroy();
                    }
                });
            }
            popoverScope.dismiss = function () {
                popoverScope.$destroy();
                destroyDrop();
            };
            var contentElement = document.createElement('div');
            contentElement.innerHTML = options.template;
            $compile(contentElement)(popoverScope);
            drop = new tether_drop_1.default({
                target: options.element,
                content: contentElement,
                position: options.position,
                classes: 'drop-popover',
                openOn: options.openOn || 'hover',
                hoverCloseDelay: 200,
                tetherOptions: {
                    constraints: [{ to: 'window', pin: true, attachment: "both" }]
                }
            });
            drop.on('close', function () {
                popoverScope.dismiss({ fromDropClose: true });
                destroyDrop();
                if (options.onClose) {
                    options.onClose();
                }
            });
            setTimeout(function () { drop.open(); }, 10);
        };
    }
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            },
            function (tether_drop_1_1) {
                tether_drop_1 = tether_drop_1_1;
            }],
        execute: function() {
            core_module_1.default.service('popoverSrv', popoverSrv);
        }
    }
});
//# sourceMappingURL=popover_srv.js.map