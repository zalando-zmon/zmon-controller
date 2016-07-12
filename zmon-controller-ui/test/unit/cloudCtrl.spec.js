describe('CloudCtrl', function() {
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

            controller = $controller('CloudCtrl', {
                $scope: scope
            });

            httpBackend = $httpBackend;
            httpBackend.when('POST', 'rest/entities', '{"type":"application"}')
                .respond([{"id": "a-aaa[aws:00000:xxx]", "type": "application", "region": "region/some-region-1", "created_by": "agent", "application_id": "aaa", "infrastructure_account": "aws:0000000"},{"id": "a-bbb[aws:111111111111:some-region-1]", "type": "application", "region": "some-region-1", "created_by": "agent", "application_id": "bbb", "infrastructure_account": "aws:222222222222"},{"id": "a-ccc[aws:333333333333:some-region-1]", "type": "application", "region": "some-region-1", "created_by": "agent", "application_id": "phoenix-zal-cuweb-live", "infrastructure_account": "aws:333333333333"},{"id": "a-ddd[aws:444444444444:some-region-1]", "type": "application", "region": "some-region-1", "created_by": "agent", "application_id": "ddd", "infrastructure_account": "aws:444444444444"},{"id": "a-eee[aws:6666666666:some-region-1]", "type": "application", "region": "some-region-1", "created_by": "agent", "application_id": "eee", "infrastructure_account": "aws:333333333333"}]);

            httpBackend.when('POST', 'rest/entities', '{"type":"local"}')
                .respond([{"id": "aws-ac[aws:111111111111:some-region-1]", "type": "local", "region": "some-region-1", "created_by": "agent", "account_alias": "111", "infrastructure_account": "aws:111111111111"},{"id": "aws-ac[aws:222222222222:some-region-1]", "type": "local", "region": "some-region-1", "created_by": "agent", "account_alias": "222", "infrastructure_account": "aws:222222222222"},{"id": "aws-ac[aws:333333333333:some-region-1]", "type": "local", "region": "some-region-1", "created_by": "agent", "account_alias": "333", "infrastructure_account": "aws:3333333333"},{"id": "aws-ac[aws:444444444444:region/some-region-1]", "type": "local", "region": "region/some-region-1", "created_by": "agent", "account_alias": "444", "infrastructure_account": "aws:444444444444"},{"id": "aws-ac[aws:555555555555:some-region-1]", "type": "local", "region": "some-region-1", "created_by": "agent", "account_alias": "555", "infrastructure_account": "aws:55555555555"}]);

            httpBackend.when('POST', 'rest/entities', '{"type":"instance"}')
                .respond([]);

            httpBackend.when('POST', 'rest/entities', '{"type":"elb"}')
                .respond([]);
        });
   });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    it('should initially have 5 teams and 5 apps', function() {
        httpBackend.expectPOST('rest/entities', '{"type":"local"}');
        httpBackend.expectPOST('rest/entities', '{"type":"application"}');
        httpBackend.expectPOST('rest/entities', '{"type":"instance"}');
        httpBackend.expectPOST('rest/entities', '{"type":"elb"}');
        httpBackend.flush();
        expect(_.keys(scope.teams).length).toBe(5);
        expect(_.keys(scope.applications).length).toBe(5);
    });

    it('should return elbs for a given team', function() {
        httpBackend.expectPOST('rest/entities', '{"type":"local"}');
        httpBackend.expectPOST('rest/entities', '{"type":"application"}');
        httpBackend.expectPOST('rest/entities', '{"type":"instance"}');
        httpBackend.expectPOST('rest/entities', '{"type":"elb"}');
        httpBackend.flush();
        expect(scope.getPublicElbsByTeam(scope.teams['111'])).toBe(0);
    });

    it('should confirm there are teams available', function() {
        httpBackend.expectPOST('rest/entities', '{"type":"local"}');
        httpBackend.expectPOST('rest/entities', '{"type":"application"}');
        httpBackend.expectPOST('rest/entities', '{"type":"instance"}');
        httpBackend.expectPOST('rest/entities', '{"type":"elb"}');
        httpBackend.flush();
        expect(scope.haveTeams()).toBe(true);
    });

    it('should confirm there are no metrics available for a given app', function() {
        httpBackend.expectPOST('rest/entities', '{"type":"local"}');
        httpBackend.expectPOST('rest/entities', '{"type":"application"}');
        httpBackend.expectPOST('rest/entities', '{"type":"instance"}');
        httpBackend.expectPOST('rest/entities', '{"type":"elb"}');
        httpBackend.flush();
        expect(scope.hasMetrics(scope.applications.aaa)).toBe(false);
        expect(scope.notHasMetrics(scope.applications.aaa)).toBe(true);
    });
});
