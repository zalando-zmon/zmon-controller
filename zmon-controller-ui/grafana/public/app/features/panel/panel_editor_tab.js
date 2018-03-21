/*! grafana - v3.1.0 - 2018-03-21
 * Copyright (c) 2018 Torkel Ödegaard; Licensed Apache-2.0 */

System.register(["angular"],function(a){function b(a){return a.create({scope:{ctrl:"=",editorTab:"=",index:"="},directive:function(a){var b=a.ctrl.pluginId,c=a.index;return Promise.resolve({name:"panel-editor-tab-"+b+c,fn:a.editorTab.directiveFn})}})}b.$inject=["dynamicDirectiveSrv"];var c,d;return{setters:[function(a){c=a}],execute:function(){d=c["default"].module("grafana.directives"),d.directive("panelEditorTab",b)}}});