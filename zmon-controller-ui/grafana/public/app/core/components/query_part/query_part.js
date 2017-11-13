///<reference path="../../../headers/common.d.ts" />
System.register(['lodash'], function(exports_1) {
    var lodash_1;
    var QueryPartDef, QueryPart;
    function functionRenderer(part, innerExpr) {
        var str = part.def.type + '(';
        var parameters = lodash_1.default.map(part.params, function (value, index) {
            var paramType = part.def.params[index];
            if (paramType.type === 'time') {
                if (value === 'auto') {
                    value = '$interval';
                }
            }
            if (paramType.quote === 'single') {
                return "'" + value + "'";
            }
            else if (paramType.quote === 'double') {
                return '"' + value + '"';
            }
            return value;
        });
        if (innerExpr) {
            parameters.unshift(innerExpr);
        }
        return str + parameters.join(', ') + ')';
    }
    exports_1("functionRenderer", functionRenderer);
    function suffixRenderer(part, innerExpr) {
        return innerExpr + ' ' + part.params[0];
    }
    exports_1("suffixRenderer", suffixRenderer);
    function identityRenderer(part, innerExpr) {
        return part.params[0];
    }
    exports_1("identityRenderer", identityRenderer);
    function quotedIdentityRenderer(part, innerExpr) {
        return '"' + part.params[0] + '"';
    }
    exports_1("quotedIdentityRenderer", quotedIdentityRenderer);
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            }],
        execute: function() {
            QueryPartDef = (function () {
                function QueryPartDef(options) {
                    this.type = options.type;
                    this.params = options.params;
                    this.defaultParams = options.defaultParams;
                    this.renderer = options.renderer;
                    this.category = options.category;
                    this.addStrategy = options.addStrategy;
                }
                return QueryPartDef;
            })();
            exports_1("QueryPartDef", QueryPartDef);
            QueryPart = (function () {
                function QueryPart(part, def) {
                    this.part = part;
                    this.def = def;
                    if (!this.def) {
                        throw { message: 'Could not find query part ' + part.type };
                    }
                    part.params = part.params || lodash_1.default.clone(this.def.defaultParams);
                    this.params = part.params;
                    this.updateText();
                }
                QueryPart.prototype.render = function (innerExpr) {
                    return this.def.renderer(this, innerExpr);
                };
                QueryPart.prototype.hasMultipleParamsInString = function (strValue, index) {
                    if (strValue.indexOf(',') === -1) {
                        return false;
                    }
                    return this.def.params[index + 1] && this.def.params[index + 1].optional;
                };
                QueryPart.prototype.updateParam = function (strValue, index) {
                    // handle optional parameters
                    // if string contains ',' and next param is optional, split and update both
                    if (this.hasMultipleParamsInString(strValue, index)) {
                        lodash_1.default.each(strValue.split(','), function (partVal, idx) {
                            this.updateParam(partVal.trim(), idx);
                        }, this);
                        return;
                    }
                    if (strValue === '' && this.def.params[index].optional) {
                        this.params.splice(index, 1);
                    }
                    else {
                        this.params[index] = strValue;
                    }
                    this.part.params = this.params;
                    this.updateText();
                };
                QueryPart.prototype.updateText = function () {
                    if (this.params.length === 0) {
                        this.text = this.def.type + '()';
                        return;
                    }
                    var text = this.def.type + '(';
                    text += this.params.join(', ');
                    text += ')';
                    this.text = text;
                };
                return QueryPart;
            })();
            exports_1("QueryPart", QueryPart);
        }
    }
});
//# sourceMappingURL=query_part.js.map