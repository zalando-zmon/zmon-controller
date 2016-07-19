describe('AlertDetailsCtrl', function() {
    var scope, controller, httpBackend;

    beforeEach(function() {
        // Fake the module's UserInfoService dependency
        angular.mock.module('zmon2App', function($provide) {
            $provide.factory('UserInfoService', function() {
                return {
                    get: function() {
                        return {
                            'fakeKey': 'fakeValue'
                        };
                    }
                };
            });
        });

        angular.mock.inject(function($rootScope, $controller, $routeParams, $httpBackend) {

            var params =  {
                "param_4": JSON.stringify({
                    "value":false,
                    "comment":"test boolean",
                    "type":"bool"
                }),
                "param_5": JSON.stringify({
                    "value":"text",
                    "comment":null,
                    "type":"str"
                }),
                "param_1": JSON.stringify({
                    "value":20,
                    "comment":"test",
                    "type":"float"
                }),
                "param_7": JSON.stringify({
                    "value":20,
                    "comment":"test conversion",
                    "type":"int"
                })
            };

            scope = $rootScope.$new();

            $routeParams.alertId = 1;

            controller = $controller('AlertDetailsCtrl', {
                $scope: scope
            });

            httpBackend = $httpBackend;

            httpBackend.when('GET', 'rest/comments?alert_definition_id=1&limit=6&offset=0')
                .respond([]);
            httpBackend.when('GET', 'rest/allTeams?').respond(["teamA", "Greendale", "Platform"]);
            httpBackend.when('GET', 'rest/alertDefinitionChildren?id=1').respond([]);
            httpBackend.when('GET', 'rest/comments?alert_definition_id=1&limit=11&offset=0')
                .respond([]);
            httpBackend.when('GET', 'rest/downtimes?alert_definition_id=1').respond([]);
            httpBackend.when('GET', 'rest/alertDetails?alert_id=1').respond({
                "alert_definition":{
                    "id":1,
                    "name":"JVM Heap Memory Usage: {heap_usage_mb} MB",
                    "description":"JVM uses too much heap memory.",
                    "team":"ZMON",
                    "responsible_team":"ZMON",
                    "entities":[
                        {"id":"zmon-controller"},
                        {"id":"zmon-scheduler"
                    }],
                    "entities_exclude":[],
                    "condition":"capture(heap_usage_mb=value['heap.used']/1024) > 1000",
                    "notifications":[],
                    "check_definition_id":8,
                    "status":"ACTIVE",
                    "priority":3,
                    "last_modified":1468395753416,
                    "last_modified_by":"demotoken",
                    "period":"",
                    "template":false,
                    "parent_id":null,
                    "parameters":null,
                    "tags":null,
                    "editable":true,
                    "cloneable":true,
                    "deletable":true
                },
                "entities":[],
                "message":"JVM Heap Memory Usage: {heap_usage_mb} MB"
            });
            httpBackend.when('GET', 'rest/checkAlertResults?alert_id=1&limit=1').respond([
                {
                    "entity":"zmon-controller",
                    "results": [
                        {
                            "td":0.069347,"worker":"plocal.zmon",
                            "ts":1.468422550741681E9,
                            "value":{
                                "gc.ps_marksweep.count":3,
                                "heap.used":59071,
                                "gc.ps_scavenge.time":1265,
                                "heap.committed":180224,
                                "gc.ps_marksweep.time":686,
                                "gc.ps_scavenge.count":59,
                                "threads":44,"heap":788480,
                                "heap.init":57344},
                                "captures":{"heap_usage_mb":57}
                        }
                    ],
                    "active_alert_ids":[1]
                },{
                    "entity":"zmon-scheduler",
                    "results":[
                        {
                            "td":0.022762,
                            "worker":"plocal.zmon",
                            "ts":1.468422550736624E9,
                            "value":{
                                "gc.ps_marksweep.count":2,
                                "heap.used":38895,
                                "gc.ps_scavenge.time":893,
                                "heap.committed":162304,
                                "gc.ps_marksweep.time":320,
                                "gc.ps_scavenge.count":32,
                                "threads":38,
                                "heap":631808,
                                "heap.init":57344
                            },
                            "captures":{
                                "heap_usage_mb":37
                            }
                        }
                    ],
                    "active_alert_ids":[1]
                }
            ]);
            httpBackend.when('GET', 'rest/checkDefinition?check_id=2').respond({
                    "id":1,
                    "name":"Random",
                    "description":"Test",
                    "technical_details":"",
                    "potential_analysis":"",
                    "potential_impact":"",
                    "potential_solution":"",
                    "owning_team":"Example Team",
                    "entities":[{"type":"GLOBAL"}],
                    "interval":10,
                    "command":"normalvariate(50, 20)",
                    "status":"ACTIVE",
                    "source_url":null,
                    "last_modified_by":"Vagrant setup.sh"
            });
            httpBackend.when('GET', 'rest/alertDefinition?id=1').respond({
                "id":1,
                "name":"Alert template",
                "description":"Test alert on all keys extended",
                "team":"Platform",
                "responsible_team":"Platform",
                "entities":[],
                "entities_exclude":[],
                "condition":"['load1']>1",
                "notifications":[],
                "check_definition_id":2,
                "status":"INACTIVE",
                "priority":1,
                "last_modified":1407504616498,
                "last_modified_by":"userZ",
                "period":"",
                "template":true,
                "parameters": '',
                "tags":[],
                "editable":true,
                "cloneable":true,
                "deletable":true
            });
            httpBackend.when('POST', 'rest/entities').respond({});
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    it('should initially have one alert with id "1"', function() {
        httpBackend.expectGET('rest/alertDefinition?id=1');
        httpBackend.expectGET('rest/checkDefinition?check_id=2');
        httpBackend.expectGET('rest/alertDetails?alert_id=1');
        httpBackend.flush();
        expect(scope.alert.id).toBe(1);
    });

    it('should display no alerts after filtering with inexisting id', function() {
        httpBackend.expectGET('rest/alertDefinition?id=1');
        httpBackend.expectGET('rest/checkDefinition?check_id=2');
        httpBackend.expectGET('rest/alertDetails?alert_id=1');
        httpBackend.expectGET('rest/checkAlertResults?alert_id=1&limit=1');
        httpBackend.flush();
        scope.alertDetailsSearch = { "str": 'notExistingAlertId' };
        expect(scope.allAlerts.length).toBe(0);
    });
});
