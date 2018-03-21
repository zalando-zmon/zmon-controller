/*! grafana - v3.1.0 - 2018-03-21
 * Copyright (c) 2018 Torkel Ödegaard; Licensed Apache-2.0 */

System.register([],function(a){var b;return{setters:[],execute:function(){b=function(){function a(a){this.backendSrv=a}return a.$inject=["backendSrv"],a.prototype.query=function(a){return this.backendSrv.get("/api/metrics/test",{from:a.range.from.valueOf(),to:a.range.to.valueOf(),maxDataPoints:a.maxDataPoints})},a}(),a("GrafanaDatasource",b)}}});