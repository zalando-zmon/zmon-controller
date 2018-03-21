/*! grafana - v3.1.0 - 2018-03-21
 * Copyright (c) 2018 Torkel Ödegaard; Licensed Apache-2.0 */

define(["angular","../core_module"],function(a,b){"use strict";b["default"].controller("JsonEditorCtrl",["$scope",function(b){b.json=a.toJson(b.object,!0),b.canUpdate=void 0!==b.updateHandler&&b.contextSrv.isEditor,b.update=function(){var c=a.fromJson(b.json);b.updateHandler(c,b.object)}}])});