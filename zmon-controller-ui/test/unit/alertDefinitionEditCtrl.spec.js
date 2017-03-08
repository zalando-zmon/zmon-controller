describe('AlertDefinitionEditCtrl', function() {
    var scope, controller, httpBackend, routeParams;

    var alertDefinition = {
        "id": 123,
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
        "notifications": ["send_mail('mail@example.com')"],
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
    };

    beforeEach(function() {
        // Fake the module's UserInfoService dependency
        angular.mock.module('zmon2App', function($provide) {
            $provide.factory('UserInfoService', function() {
                return {
                    get: function() {
                        return {
                            'teams': 'teamA,teamB',
                            'fakeKey': 'fakeValue'
                        };
                    }
                };
            });
        });

        angular.mock.inject(function($rootScope, $controller, $httpBackend, $routeParams) {
            scope = $rootScope.$new();
            $routeParams.alertDefinitionId = 123;

            controller = $controller('AlertDefinitionEditCtrl', {
                $scope: scope,
            });

            httpBackend = $httpBackend;

            httpBackend.when('GET', 'rest/alertDefinitions?').respond([alertDefinition]);
            httpBackend.when('GET', 'rest/alertDefinitionNode?id=123').respond(alertDefinition);
            httpBackend.when('GET', 'rest/entityProperties?').respond({
                "instance": {
                    "infrastructure_account": [
                        "aws:111", "aws:222", "aws:333", "aws:444",
                        "aws:555", "aws:666", "aws:777", "aws:888",
                        "aws:999", "aws:112", "aws:113", "aws:114",
                        "aws:221", "aws:223", "aws:224", "aws:331",
                        "aws:332", "aws:334", "aws:441", "aws:442",
                        "aws:443", "aws:551"
                ]}
            });

            httpBackend.when('GET', 'rest/allTags?').respond([
                "CLAUDIA","PEDRO","test1","DEV","test34232","TEST","RIBEIRO","MARTINS"
            ]);

            httpBackend.when('GET', 'rest/checkDefinition?check_id=2').respond({
                "id":2,
                "name":"Heartbeat.jsp Check",
                "description":"Check description",
                "technical_details":null,
                "potential_analysis":null,
                "potential_impact":null,
                "potential_solution":null,
                "owning_team":"Platform",
                "entities":[{"type":"zomcat"}],
                "interval":60,
                "command":"http('/heartbeat.jsp', timeout=5).text().strip()=='OK: Zalando JVM is running'",
                "status":"ACTIVE",
                "source_url":"somewhere.yaml",
                "last_modified_by":"userZ"
            });

            httpBackend.when('POST', 'rest/entity-filters').respond({"count":1411,"entities":[]});
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });


    it('should initialize with two entity filter types and eight tags', function() {
        //httpBackend.expectGET('rest/entityProperties?');
        //httpBackend.expectGET('rest/allTags?');
        httpBackend.flush();
        expect(scope.entityFilter.types.length).toBe(2);
        expect(scope.allTags.length).toBe(8);
    });

    it('should initialize to Edit mode if an alert id is given', function() {
        httpBackend.flush();
        expect(scope.mode).toBe('edit');
        expect(scope.alertDefinition.id).toBe(123);
        expect(scope.alertDefinition.check_definition_id).toBe(2);
    });

    it('should validate a python variable name', function() {
        httpBackend.flush();
        expect(scope.paramNameIsValid('_goodVariableName12')).toBe(true);
        expect(scope.paramNameIsValid('!bad#Variable!bad')).toBe(false);
    });

    it('should set correct name for new cloned alert if in clone mode', function() {
        httpBackend.flush();
        scope.cloneFromAlertDefinitionId = 123;
        expect(scope.alertDefinition.name).toBe('Alert on http code (1) --');
    });
});
