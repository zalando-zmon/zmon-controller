describe('TrialRunCtrl', function() {
    var scope, controller;
    var expectedYAML = 'name: test-yaml\n\
description: TrialRun Test\n\
status: ACTIVE\n\
interval: 120\n\
command: |\n\
  sql().execute("""select 1 as a, now() as b""").results()\n\
owning_team: STUPS\n\
# OPTIONAL FIELDS\n\
#technical_details: Optional Technical Details\n\
#potential_analysis: Optional Potential analysis\n\
#potential_impact: Optional potential impact\n\
#potential_solution: Optional potential solution';

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

        angular.mock.inject(function($rootScope, $controller) {
            scope = $rootScope.$new();
            scope.alert = {};
            scope.alert.name = 'test-yaml';
            scope.alert.check_command = 'sql().execute("""select 1 as a, now() as b""").results()';
            scope.alert.entities = '[{"type": "database","environment": "live","name":"customer", "role":"master"}]';

            controller = $controller('TrialRunCtrl', {
                $scope: scope
            });
        });
    });

    it('should build valid YAML', function() {
        expect(scope.TrialRunCtrl.buildYAMLContent()).toEqual(expectedYAML);
    });
});
