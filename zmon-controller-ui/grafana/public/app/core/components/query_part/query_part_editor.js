///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', 'jquery', 'app/core/core_module'], function(exports_1) {
    var lodash_1, jquery_1, core_module_1;
    var template;
    /** @ngInject */
    function queryPartEditorDirective($compile, templateSrv) {
        var paramTemplate = '<input type="text" style="display:none"' +
            ' class="input-mini tight-form-func-param"></input>';
        return {
            restrict: 'E',
            template: template,
            scope: {
                part: "=",
                removeAction: "&",
                partUpdated: "&",
                getOptions: "&",
            },
            link: function postLink($scope, elem) {
                var part = $scope.part;
                var partDef = part.def;
                var $paramsContainer = elem.find('.query-part-parameters');
                var $controlsContainer = elem.find('.tight-form-func-controls');
                function clickFuncParam(paramIndex) {
                    /*jshint validthis:true */
                    var $link = jquery_1.default(this);
                    var $input = $link.next();
                    $input.val(part.params[paramIndex]);
                    $input.css('width', ($link.width() + 16) + 'px');
                    $link.hide();
                    $input.show();
                    $input.focus();
                    $input.select();
                    var typeahead = $input.data('typeahead');
                    if (typeahead) {
                        $input.val('');
                        typeahead.lookup();
                    }
                }
                function inputBlur(paramIndex) {
                    /*jshint validthis:true */
                    var $input = jquery_1.default(this);
                    var $link = $input.prev();
                    var newValue = $input.val();
                    if (newValue !== '' || part.def.params[paramIndex].optional) {
                        $link.html(templateSrv.highlightVariablesAsHtml(newValue));
                        part.updateParam($input.val(), paramIndex);
                        $scope.$apply($scope.partUpdated);
                    }
                    $input.hide();
                    $link.show();
                }
                function inputKeyPress(paramIndex, e) {
                    /*jshint validthis:true */
                    if (e.which === 13) {
                        inputBlur.call(this, paramIndex);
                    }
                }
                function inputKeyDown() {
                    /*jshint validthis:true */
                    this.style.width = (3 + this.value.length) * 8 + 'px';
                }
                function addTypeahead($input, param, paramIndex) {
                    if (!param.options && !param.dynamicLookup) {
                        return;
                    }
                    var typeaheadSource = function (query, callback) {
                        if (param.options) {
                            return param.options;
                        }
                        $scope.$apply(function () {
                            $scope.getOptions().then(function (result) {
                                var dynamicOptions = lodash_1.default.map(result, function (op) { return op.value; });
                                callback(dynamicOptions);
                            });
                        });
                    };
                    $input.attr('data-provide', 'typeahead');
                    var options = param.options;
                    if (param.type === 'int') {
                        options = lodash_1.default.map(options, function (val) { return val.toString(); });
                    }
                    $input.typeahead({
                        source: typeaheadSource,
                        minLength: 0,
                        items: 1000,
                        updater: function (value) {
                            setTimeout(function () {
                                inputBlur.call($input[0], paramIndex);
                            }, 0);
                            return value;
                        }
                    });
                    var typeahead = $input.data('typeahead');
                    typeahead.lookup = function () {
                        this.query = this.$element.val() || '';
                        var items = this.source(this.query, jquery_1.default.proxy(this.process, this));
                        return items ? this.process(items) : items;
                    };
                }
                $scope.toggleControls = function () {
                    var targetDiv = elem.closest('.tight-form');
                    if (elem.hasClass('show-function-controls')) {
                        elem.removeClass('show-function-controls');
                        targetDiv.removeClass('has-open-function');
                        $controlsContainer.hide();
                        return;
                    }
                    elem.addClass('show-function-controls');
                    targetDiv.addClass('has-open-function');
                    $controlsContainer.show();
                };
                $scope.removeActionInternal = function () {
                    $scope.toggleControls();
                    $scope.removeAction();
                };
                function addElementsAndCompile() {
                    lodash_1.default.each(partDef.params, function (param, index) {
                        if (param.optional && part.params.length <= index) {
                            return;
                        }
                        if (index > 0) {
                            jquery_1.default('<span>, </span>').appendTo($paramsContainer);
                        }
                        var paramValue = templateSrv.highlightVariablesAsHtml(part.params[index]);
                        var $paramLink = jquery_1.default('<a class="graphite-func-param-link pointer">' + paramValue + '</a>');
                        var $input = jquery_1.default(paramTemplate);
                        $paramLink.appendTo($paramsContainer);
                        $input.appendTo($paramsContainer);
                        $input.blur(lodash_1.default.partial(inputBlur, index));
                        $input.keyup(inputKeyDown);
                        $input.keypress(lodash_1.default.partial(inputKeyPress, index));
                        $paramLink.click(lodash_1.default.partial(clickFuncParam, index));
                        addTypeahead($input, param, index);
                    });
                }
                function relink() {
                    $paramsContainer.empty();
                    addElementsAndCompile();
                }
                relink();
            }
        };
    }
    exports_1("queryPartEditorDirective", queryPartEditorDirective);
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (jquery_1_1) {
                jquery_1 = jquery_1_1;
            },
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            template = "\n<div class=\"tight-form-func-controls\">\n  <span class=\"pointer fa fa-remove\" ng-click=\"removeActionInternal()\"></span>\n</div>\n\n<a ng-click=\"toggleControls()\" class=\"query-part-name\">{{part.def.type}}</a>\n<span>(</span><span class=\"query-part-parameters\"></span><span>)</span>\n";
            core_module_1.default.directive('queryPartEditor', queryPartEditorDirective);
        }
    }
});
//# sourceMappingURL=query_part_editor.js.map