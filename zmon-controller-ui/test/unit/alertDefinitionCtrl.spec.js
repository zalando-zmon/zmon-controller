describe('AlertDefinitionCtrl', function() {
    var scope, controller, httpBackend;
    var store = {};
    var ls = function() {
        return JSON.parse(store.storage);
    };

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

        // LocalStorage mock.
        spyOn(localStorage, 'getItem').and.callFake(function (key) {
          return store[key];
        });
        Object.defineProperty(sessionStorage, "setItem", { writable: true });
        spyOn(localStorage, 'setItem').and.callFake(function(key, value) {
            store[key] = value;
        });

        angular.mock.inject(function($rootScope, $controller, $httpBackend) {
            scope = $rootScope.$new();

            controller = $controller('AlertDefinitionCtrl', {
                $scope: scope
            });

            httpBackend = $httpBackend;

            httpBackend.when('GET', 'rest/allTeams?').respond(["teamA", "Greendale", "Platform"]);
            httpBackend.when('GET', 'rest/allAlerts?').respond(["teamB", "teamB", "Platform"]);
            httpBackend.when('GET', 'rest/alertDefinitions?').respond([
                {
                    "id": 691,
                    "name": "Alert on http code (1) --",
                    "description": "Alert description",
                    "team": "teamB",
                    "responsible_team": "Platform",
                    "entities": [{
                        "project": "shop",
                        "environment": "release-staging",
                        "type": "zomcat"
                    }],
                    "condition": "!=0",
                    "notifications": ["send_mail('example@email.com')"],
                    "check_definition_id": 2,
                    "status": "ACTIVE",
                    "priority": 1,
                    "last_modified": 1396619805429,
                    "last_modified_by": "userZ",
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
                    "responsible_team": "Platform",
                    "entities": [],
                    "condition": "!=0",
                    "notifications": [],
                    "check_definition_id": 2,
                    "status": "INACTIVE",
                    "priority": 1,
                    "last_modified": 1396362646573,
                    "last_modified_by": "userZ",
                    "period": null,
                    "editable": true,
                    "cloneable": true,
                    "deletable": true
                }, {
                    "id":730,
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

            httpBackend.when('GET', 'rest/alertDefinitions?team=Platform').respond([
                {
                    "id": 691,
                    "name": "Alert on http code (1) --",
                    "description": "Alert description",
                    "team": "Platform",
                    "responsible_team": "Platform",
                    "entities": [{
                        "project": "shop",
                        "environment": "release-staging",
                        "type": "zomcat"
                    }],
                    "condition": "!=0",
                    "notifications": ["send_mail('example@email.com')"],
                    "check_definition_id": 2,
                    "status": "ACTIVE",
                    "priority": 1,
                    "last_modified": 1396619805429,
                    "last_modified_by": "userZ",
                    "period": "xxxxx",
                    "editable": true,
                    "cloneable": true,
                    "deletable": true,
                    "star": true
                }
            ]);
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
        store = {};
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
        expect(store['ls.returnTo']).toBe("/#?tab=ACTIVE");
    });

    it('should have one template alert after applying filter for templates', function() {
        httpBackend.flush();
        scope.setAlertsFilter('All');
        scope.setTemplateFilter('template');
        expect(scope.alertDefinitionsByStatus.length).toBe(1);
        expect(store['ls.returnTo']).toBe("/#?tab=All");
        expect(scope.isFilteredByTemplate).toBe(true);
    });

    it('should have one alert after applying team filter "Platform"', function() {
        scope.setTeamFilter('Platform');
        httpBackend.flush();
        expect(scope.alertDefinitionsByStatus.length).toBe(1);
        expect(store['ls.teamFilter']).toBe("Platform");
        expect(store['ls.returnTo']).toBe("/#?tf=Platform");
    });

    it('should have one star alert at least', function() {
        httpBackend.flush();
        expect(scope.tabHasStar('All')).toBe(true);
    });


});
