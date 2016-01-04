describe('AlertDefinitionEditCtrl', function() {
    var scope, controller, httpBackend, routeParams;

    var alertDefinition = {
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
    };

    beforeEach(function() {
        // Fake the module's UserInfoService dependency
        angular.mock.module('zmon2App', function($provide) {
            $provide.factory('UserInfoService', function() {
                return {
                    get: function() {
                        return {
                            'teams': 'STUPS,greendale',
                            'fakeKey': 'fakeValue'
                        };
                    }
                };
            });
        });

        angular.mock.inject(function($rootScope, $controller, $httpBackend, $routeParams) {
            scope = $rootScope.$new();
            $routeParams.alertDefinitionId = 691;

            controller = $controller('AlertDefinitionEditCtrl', {
                $scope: scope,
            });

            httpBackend = $httpBackend;

            httpBackend.when('GET', 'rest/alertDefinitions?').respond([alertDefinition]);
            httpBackend.when('GET', 'rest/alertDefinitionNode?id=691').respond(alertDefinition);
            httpBackend.when('GET', 'rest/entityProperties?').respond({
                "instance": {
                    "infrastructure_account": [
                        "aws:085668006708", "aws:162935806085", "aws:356702503974", "aws:681709283245",
                        "aws:576069677832", "aws:149830196184", "aws:625928549634", "aws:352469958076",
                        "aws:786011980701", "aws:341135513002", "aws:021483111008", "aws:075097227389",
                        "aws:257146137867", "aws:607300429032", "aws:368335482178", "aws:493493404749",
                        "aws:511305221567", "aws:950209458978", "aws:007149717068", "aws:263820974194",
                        "aws:222970907460", "aws:790810764578"
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
                "owning_team":"Platform/Software",
                "entities":[{"type":"zomcat"}],
                "interval":60,
                "command":"http('/heartbeat.jsp', timeout=5).text().strip()=='OK: Zalando JVM is running'",
                "status":"ACTIVE",
                "source_url":"https://stash.zalando.net/scm/platform/zmon2-software-checks.git/zmon-checks/heartbeatjsp.yaml",
                "last_modified_by":"Henning Jacobs"
            });
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });


    it('should initialize with two entity filter types and eight tags', function() {
        httpBackend.expectGET('rest/entityProperties?');
        httpBackend.expectGET('rest/allTags?');
        httpBackend.flush();
        expect(scope.entityFilter.types.length).toBe(2);
        expect(scope.allTags.length).toBe(8);
    });

    it('should initialize to Edit mode if an alert id is given', function() {
        httpBackend.flush();
        expect(scope.mode).toBe('edit');
        expect(scope.alertDefinition.id).toBe(691);
        expect(scope.alertDefinition.check_definition_id).toBe(2);
    });

});
