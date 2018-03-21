/*! grafana - v3.1.0 - 2018-03-21
 * Copyright (c) 2018 Torkel Ödegaard; Licensed Apache-2.0 */

System.register(["test/lib/common","app/app"],function(a){var b,c;return{setters:[function(a){b=a},function(a){c=a}],execute:function(){b.describe("GrafanaApp",function(){var a=new c.GrafanaApp;b.it("can call inits",function(){b.expect(a).to.not.be(null)})})}}});