/*! grafana - v3.1.0 - 2018-03-21
 * Copyright (c) 2018 Torkel Ödegaard; Licensed Apache-2.0 */

System.register([],function(a){var b;return{setters:[],execute:function(){b=function(){function a(){this.columns=[],this.rows=[],this.type="table"}return a.prototype.sort=function(a){null===a.col||this.columns.length<=a.col||(this.rows.sort(function(b,c){return b=b[a.col],c=c[a.col],b<c?-1:b>c?1:0}),this.columns[a.col].sort=!0,a.desc?(this.rows.reverse(),this.columns[a.col].desc=!0):this.columns[a.col].desc=!1)},a}(),a("default",b)}}});