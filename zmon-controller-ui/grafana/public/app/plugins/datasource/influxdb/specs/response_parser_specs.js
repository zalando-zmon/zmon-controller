System.register(['lodash', 'test/lib/common', '../response_parser'], function(exports_1) {
    var _this = this;
    var lodash_1, common_1, response_parser_1;
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (common_1_1) {
                common_1 = common_1_1;
            },
            function (response_parser_1_1) {
                response_parser_1 = response_parser_1_1;
            }],
        execute: function() {
            common_1.describe("influxdb response parser", function () {
                _this.parser = new response_parser_1.default();
                common_1.describe("SHOW TAG response", function () {
                    var query = 'SHOW TAG KEYS FROM "cpu"';
                    var response = {
                        "results": [
                            {
                                "series": [
                                    {
                                        "name": "cpu",
                                        "columns": ["tagKey"],
                                        "values": [["datacenter"], ["hostname"], ["source"]]
                                    }
                                ]
                            }
                        ]
                    };
                    var result = _this.parser.parse(query, response);
                    common_1.it("expects three results", function () {
                        common_1.expect(lodash_1.default.size(result)).to.be(3);
                    });
                });
                common_1.describe("SHOW TAG VALUES response", function () {
                    var query = 'SHOW TAG VALUES FROM "cpu" WITH KEY = "hostname"';
                    common_1.describe("response from 0.10.0", function () {
                        var response = {
                            "results": [
                                {
                                    "series": [
                                        {
                                            "name": "hostnameTagValues",
                                            "columns": ["hostname"],
                                            "values": [["server1"], ["server2"], ["server2"]]
                                        }
                                    ]
                                }
                            ]
                        };
                        var result = _this.parser.parse(query, response);
                        common_1.it("should get two responses", function () {
                            common_1.expect(lodash_1.default.size(result)).to.be(2);
                            common_1.expect(result[0].text).to.be("server1");
                            common_1.expect(result[1].text).to.be("server2");
                        });
                    });
                    common_1.describe("response from 0.12.0", function () {
                        var response = {
                            "results": [
                                {
                                    "series": [
                                        {
                                            "name": "cpu",
                                            "columns": ["key", "value"],
                                            "values": [
                                                ["source", "site"],
                                                ["source", "api"]
                                            ]
                                        },
                                        {
                                            "name": "logins",
                                            "columns": ["key", "value"],
                                            "values": [
                                                ["source", "site"],
                                                ["source", "webapi"]
                                            ]
                                        },
                                    ]
                                }
                            ]
                        };
                        var result = _this.parser.parse(query, response);
                        common_1.it("should get two responses", function () {
                            common_1.expect(lodash_1.default.size(result)).to.be(3);
                            common_1.expect(result[0].text).to.be('site');
                            common_1.expect(result[1].text).to.be('api');
                            common_1.expect(result[2].text).to.be('webapi');
                        });
                    });
                });
                common_1.describe("SHOW FIELD response", function () {
                    var query = 'SHOW FIELD KEYS FROM "cpu"';
                    common_1.describe("response from 0.10.0", function () {
                        var response = {
                            "results": [
                                {
                                    "series": [
                                        {
                                            "name": "measurements",
                                            "columns": ["name"],
                                            "values": [
                                                ["cpu"], ["derivative"], ["logins.count"], ["logs"], ["payment.ended"], ["payment.started"]
                                            ]
                                        }
                                    ]
                                }
                            ]
                        };
                        var result = _this.parser.parse(query, response);
                        common_1.it("should get two responses", function () {
                            common_1.expect(lodash_1.default.size(result)).to.be(6);
                        });
                    });
                    common_1.describe("response from 0.11.0", function () {
                        var response = {
                            "results": [
                                {
                                    "series": [
                                        {
                                            "name": "cpu",
                                            "columns": ["fieldKey"],
                                            "values": [["value"]]
                                        }
                                    ]
                                }
                            ]
                        };
                        var result = _this.parser.parse(query, response);
                        common_1.it("should get two responses", function () {
                            common_1.expect(lodash_1.default.size(result)).to.be(1);
                        });
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=response_parser_specs.js.map