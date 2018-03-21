/*! grafana - v3.1.0 - 2018-03-21
 * Copyright (c) 2018 Torkel Ödegaard; Licensed Apache-2.0 */

System.register(["eventemitter3"],function(a){var b,c,d;return{setters:[function(a){b=a}],execute:function(){c={}.hasOwnProperty,d=function(){function a(){this.emitter=new b["default"]}return a.prototype.emit=function(a,b){this.emitter.emit(a,b)},a.prototype.on=function(a,b,c){var d=this;this.emitter.on(a,b),c&&c.$on("$destroy",function(){d.emitter.off(a,b)})},a.prototype.off=function(a,b){this.emitter.off(a,b)},a}(),a("Emitter",d)}}});