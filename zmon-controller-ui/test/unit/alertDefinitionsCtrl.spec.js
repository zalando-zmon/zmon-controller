describe('AlertDefinitionsCtrl', function() {
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

        angular.mock.inject(function($rootScope, $controller, $httpBackend) {
            scope = $rootScope.$new();

            controller = $controller('AlertDefinitionCtrl', {
                $scope: scope
            });

            httpBackend = $httpBackend;

            httpBackend.when('GET', 'rest/allTeams?').respond(["STUPS", "Greendale", "Platform/Software"]);
            httpBackend.when('GET', 'rest/allAlerts?').respond(["Backend/Order", "Backend/Payment", "Platform/System"]);
            httpBackend.when('GET', 'rest/alertDefinitions?').respond([
                {
                    "id": 691,
                    "name": "Alert on http code (1) --",
                    "description": "Alert description",
                    "team": "Backend/Order",
                    "responsible_team": "Platform/Software",
                    "entities": [{
                        "project": "shop",
                        "environment": "release-staging",
                        "type": "zomcat"
                    }],
                    "condition": "!=0",
                    "notifications": ["send_mail('vitalii.kapara@zalando.de')"],
                    "check_definition_id": 2,
                    "status": "ACTIVE",
                    "priority": 1,
                    "last_modified": 1396619805429,
                    "last_modified_by": "hjacobs",
                    "period": "xxxxx",
                    "editable": true,
                    "cloneable": true,
                    "deletable": true,
                    "star": true
                }, {
                    "id": 695,
                    "name": "Alert on http code1",
                    "description": "Alert description",
                    "team": "Platform",
                    "responsible_team": "Platform/System",
                    "entities": [],
                    "condition": "!=0",
                    "notifications": [],
                    "check_definition_id": 2,
                    "status": "INACTIVE",
                    "priority": 1,
                    "last_modified": 1396362646573,
                    "last_modified_by": "hjacobs",
                    "period": null,
                    "editable": true,
                    "cloneable": true,
                    "deletable": true
                }, {
                    "id":730,
                    "name":"Alert template",
                    "description":"Test alert on all keys extended",
                    "team":"Platform/Software",
                    "responsible_team":"Platform/Software",
                    "entities":[],
                    "entities_exclude":[],
                    "condition":"['load1']>1",
                    "notifications":[],
                    "check_definition_id":2,
                    "status":"INACTIVE",
                    "priority":1,
                    "last_modified":1407504616498,
                    "last_modified_by":"hjacobs",
                    "period":"",
                    "template":true,
                    "parent_id":728,
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
                }
            ]);
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });


    it('should initially have three alert teams and two alert statuses', function() {
        httpBackend.expectGET('rest/allTeams?');
        httpBackend.expectGET('rest/alertDefinitions?');
        httpBackend.flush();
        expect(scope.alertTeams.length).toBe(3);
        expect(scope.alertStatuses.length).toBe(3);
    });

    it('should initially have two alert definitions in total (excludes templates)', function() {
        httpBackend.flush();
        expect(scope.alertDefinitions['All'].length).toBe(2);
    });

    it('should have one alert after applying filter for "ACTIVE" alerts only', function() {
        httpBackend.flush();
        scope.setAlertsFilter('ACTIVE');
        expect(scope.alertDefinitionsByStatus.length).toBe(1);
    });

    it('should have one template alert after applying filter for templates', function() {
        httpBackend.flush();
        scope.setAlertsFilter('All');
        scope.setTemplateFilter('template');
        expect(scope.alertDefinitionsByStatus.length).toBe(1);
    });

    it('should have one star alert at least', function() {
        httpBackend.flush();
        expect(scope.tabHasStar('All')).toBe(true);
    });


});
