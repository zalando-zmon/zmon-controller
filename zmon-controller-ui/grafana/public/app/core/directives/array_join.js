/*! grafana - v3.1.0 - 2018-03-21
 * Copyright (c) 2018 Torkel Ödegaard; Licensed Apache-2.0 */

System.register(["lodash","../core_module"],function(a){function b(){"use strict";return{restrict:"A",require:"ngModel",link:function(a,b,d,e){function f(a){return(a||"").split(",")}function g(a){return c["default"].isArray(a)?(a||"").join(","):a}e.$parsers.push(f),e.$formatters.push(g)}}}var c,d;return a("arrayJoin",b),{setters:[function(a){c=a},function(a){d=a}],execute:function(){d["default"].directive("arrayJoin",b)}}});