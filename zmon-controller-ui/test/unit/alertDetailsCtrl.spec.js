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
            httpBackend.when('GET', 'rest/comments?alert_definition_id=1&limit=6&offset=0')
                .respond([]);
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
                "parameters": {
                    "param_4": {
                        "value":false,
                        "comment":"test boolean",
                        "type":"bool"
                    },
                    "param_5": {
                        "value":"text",
                        "comment":null,
                        "type":"str"
                    },
                    "param_1": {
                        "value":20,
                        "comment":"test",
                        "type":"float"
                    },
                    "param_7": {
                        "value":20,
                        "comment":"test conversion",
                        "type":"int"
                    }
                },
                "tags":[],
                "editable":true,
                "cloneable":true,
                "deletable":true
            });
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    it('should initially have three alert teams and two alert statuses', function() {
        httpBackend.expectGET('rest/comments?alert_definition_id=1&limit=6&offset=0');
        httpBackend.expectGET('rest/alertDefinition?id=1');
        httpBackend.expectGET('rest/checkDefinition?check_id=2');
        httpBackend.flush();
        expect(scope.alertDefinition.id).toBe(1);
    });

});
