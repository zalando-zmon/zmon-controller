describe('DashboardCtrl', function() {
    var scope, controller, httpBackend, routeParams;
    var dashboard1, alerts, checks, tags;

    dashboard1 =
{"id":1,"name":"Default Demo Dashboard","created_by":"demotoken","last_modified":1464778107616,"last_modified_by":"demotoken","widget_configuration":"[{\"type\":\"chart\",\"checkDefinitionId\":4,\"title\":\"Random\",\"entityId\":\"GLOBAL\",\"options\":{\"start_relative\":{\"unit\":\"minutes\",\"value\":\"30\"},\"legend\":{\"show\":true,\"backgroundOpacity\":0.1,\"position\":\"ne\"},\"series\":{\"stack\":false},\"cache_time\":0,\"lines\":{\"fill\":true}}}]","alert_teams":["*"],"view_mode":"FULL","edit_option":"PRIVATE","shared_teams":[],"tags":null,"editable":true,"cloneable":true,"edit_option_editable":true}

    alerts =[{"alert_definition":{"id":8,"name":"Gather API metric data","description":"Dummy alert to collect data.","team":"ZMON","responsible_team":"ZMON","entities":[],"entities_exclude":[],"condition":"False","notifications":[],"check_definition_id":9,"status":"ACTIVE","priority":3,"last_modified":1464778106205,"last_modified_by":"demotoken","period":"","template":false,"parent_id":null,"parameters":null,"tags":null,"editable":true,"cloneable":true,"deletable":true},"entities":[{"entity":"zmon-metric-cache-localhost[dc:1]","result":{"exc":1,"downtimes":[],"captures":{},"start_time":1.464778300796965E9,"worker":"plocal.zmon","ts":1.464779980755036E9,"value":"HTTP request failed for http://localhost:8086/metrics: connection failed","td":0.012368917465209961}}],"message":"Gather API metric data"},{"alert_definition":{"id":3,"name":"Random Example Alert","description":"Triggers alert if random value is larger than 10.","team":"ZMON","responsible_team":"ZMON","entities":[],"entities_exclude":[],"condition":">10","notifications":[],"check_definition_id":4,"status":"ACTIVE","priority":1,"last_modified":1464778101299,"last_modified_by":"demotoken","period":"","template":false,"parent_id":null,"parameters":null,"tags":null,"editable":true,"cloneable":true,"deletable":true},"entities":[{"entity":"GLOBAL","result":{"td":5.86E-4,"downtimes":[],"captures":{},"start_time":1.464779798759823E9,"worker":"plocal.zmon","ts":1.464780008748376E9,"value":53.46838160514609}}],"message":"Random Example Alert"},{"alert_definition":{"id":2,"name":"JVM Threads: {threads}","description":"Number of JVM threads is above threshold.","team":"ZMON","responsible_team":"ZMON","entities":[{"id":"zmon-controller","type":"demowebapp"},{"id":"zmon-scheduler","type":"demowebapp"}],"entities_exclude":[],"condition":"capture(threads=value['threads']) > 40","notifications":[],"check_definition_id":8,"status":"ACTIVE","priority":3,"last_modified":1464778100495,"last_modified_by":"demotoken","period":"","template":false,"parent_id":null,"parameters":null,"tags":null,"editable":true,"cloneable":true,"deletable":true},"entities":[{"entity":"zmon-scheduler","result":{"td":0.013105,"downtimes":[],"captures":{"threads":41},"start_time":1.46477819078936E9,"worker":"plocal.zmon","ts":1.464780020756769E9,"value":{"gc.ps_marksweep.count":2,"heap":631808,"gc.ps_scavenge.time":942,"heap.committed":290304,"gc.ps_marksweep.time":593,"gc.ps_scavenge.count":15,"threads":41,"heap.used":58754,"heap.init":57344}}},{"entity":"zmon-controller","result":{"td":0.047826,"downtimes":[],"captures":{"threads":44},"start_time":1.4647781308386E9,"worker":"plocal.zmon","ts":1.464780020759302E9,"value":{"gc.ps_marksweep.count":3,"heap.used":180837,"gc.ps_scavenge.time":740,"heap.committed":367616,"gc.ps_marksweep.time":977,"gc.ps_scavenge.count":24,"threads":44,"heap":788480,"heap.init":57344}}}],"message":"JVM Threads: 41, 44"}];
    checks = {
        "9": {"values":{}},
        "4": {"values":{"":[[1.464780008748376E9,53.46838160514609],[1.464779993749564E9,44.80903596113408],[1.46477997875463E9,69.51177064763823],[1.464779963748919E9,29.157293606968953],[1.464779948753137E9,50.087626198197064],[1.464779933749963E9,70.31095039052842],[1.46477991875037E9,39.08447857958534],[1.464779903748972E9,31.38519582792813],[1.464779888755771E9,59.47079359995065],[1.464779873749774E9,50.87516444106397],[1.464779858749606E9,71.93086841515623],[1.464779843748696E9,24.193425634268124],[1.464779828748067E9,81.96961773827735],[1.464779813749361E9,37.39196286523474],[1.464779798752075E9,42.81784901942312],[1.464779783749993E9,8.312313428127531],[1.464779768750179E9,27.74408197290569],[1.464779753749E9,74.44006382160858],[1.464779738749682E9,61.168839265104445],[1.46477972374994E9,58.78663378446937]]}},
        "8": {"values":{"gc.ps_marksweep.count":[[1.464780020759302E9,3],[1.464780005756396E9,3],[1.464779990751916E9,3],[1.464779975753504E9,3],[1.464779960751926E9,3],[1.464779945752073E9,3],[1.46477993075226E9,3],[1.464779915754167E9,3],[1.464779900754967E9,3],[1.464779885756519E9,3],[1.464779870755742E9,3],[1.464779855751767E9,3],[1.464779840752564E9,3],[1.46477982575217E9,3],[1.464779810756469E9,3],[1.464779795753041E9,3],[1.464779780753594E9,3],[1.464779765756562E9,3],[1.464779750757024E9,3],[1.46477973576044E9,3]],"heap.committed":[[1.464780020759302E9,367616],[1.464780005756396E9,367616],[1.464779990751916E9,367616],[1.464779975753504E9,367616],[1.464779960751926E9,367616],[1.464779945752073E9,367616],[1.46477993075226E9,367616],[1.464779915754167E9,367616],[1.464779900754967E9,367616],[1.464779885756519E9,367616],[1.464779870755742E9,367616],[1.464779855751767E9,367616],[1.464779840752564E9,367616],[1.46477982575217E9,367616],[1.464779810756469E9,367616],[1.464779795753041E9,367616],[1.464779780753594E9,367616],[1.464779765756562E9,367616],[1.464779750757024E9,367616],[1.46477973576044E9,367616]],"heap.used":[[1.464780020759302E9,180837],[1.464780005756396E9,177966],[1.464779990751916E9,175988],[1.464779975753504E9,171961],[1.464779960751926E9,169778],[1.464779945752073E9,167106],[1.46477993075226E9,164948],[1.464779915754167E9,160197],[1.464779900754967E9,157979],[1.464779885756519E9,155336],[1.464779870755742E9,153557],[1.464779855751767E9,148408],[1.464779840752564E9,145493],[1.46477982575217E9,143685],[1.464779810756469E9,141248],[1.464779795753041E9,137705],[1.464779780753594E9,134904],[1.464779765756562E9,131477],[1.464779750757024E9,129741],[1.46477973576044E9,125126]],"threads":[[1.464780020759302E9,44],[1.464780005756396E9,44],[1.464779990751916E9,44],[1.464779975753504E9,44],[1.464779960751926E9,44],[1.464779945752073E9,44],[1.46477993075226E9,44],[1.464779915754167E9,44],[1.464779900754967E9,44],[1.464779885756519E9,44],[1.464779870755742E9,44],[1.464779855751767E9,44],[1.464779840752564E9,44],[1.46477982575217E9,44],[1.464779810756469E9,44],[1.464779795753041E9,44],[1.464779780753594E9,44],[1.464779765756562E9,44],[1.464779750757024E9,44],[1.46477973576044E9,44]],"heap.init":[[1.464780020759302E9,57344],[1.464780005756396E9,57344],[1.464779990751916E9,57344],[1.464779975753504E9,57344],[1.464779960751926E9,57344],[1.464779945752073E9,57344],[1.46477993075226E9,57344],[1.464779915754167E9,57344],[1.464779900754967E9,57344],[1.464779885756519E9,57344],[1.464779870755742E9,57344],[1.464779855751767E9,57344],[1.464779840752564E9,57344],[1.46477982575217E9,57344],[1.464779810756469E9,57344],[1.464779795753041E9,57344],[1.464779780753594E9,57344],[1.464779765756562E9,57344],[1.464779750757024E9,57344],[1.46477973576044E9,57344]],"heap":[[1.464780020759302E9,788480],[1.464780005756396E9,788480],[1.464779990751916E9,788480],[1.464779975753504E9,788480],[1.464779960751926E9,788480],[1.464779945752073E9,788480],[1.46477993075226E9,788480],[1.464779915754167E9,788480],[1.464779900754967E9,788480],[1.464779885756519E9,788480],[1.464779870755742E9,788480],[1.464779855751767E9,788480],[1.464779840752564E9,788480],[1.46477982575217E9,788480],[1.464779810756469E9,788480],[1.464779795753041E9,788480],[1.464779780753594E9,788480],[1.464779765756562E9,788480],[1.464779750757024E9,788480],[1.46477973576044E9,788480]],"gc.ps_scavenge.time":[[1.464780020759302E9,740],[1.464780005756396E9,740],[1.464779990751916E9,740],[1.464779975753504E9,740],[1.464779960751926E9,740],[1.464779945752073E9,740],[1.46477993075226E9,740],[1.464779915754167E9,740],[1.464779900754967E9,740],[1.464779885756519E9,740],[1.464779870755742E9,740],[1.464779855751767E9,740],[1.464779840752564E9,740],[1.46477982575217E9,740],[1.464779810756469E9,740],[1.464779795753041E9,740],[1.464779780753594E9,740],[1.464779765756562E9,740],[1.464779750757024E9,740],[1.46477973576044E9,740]],"gc.ps_scavenge.count":[[1.464780020759302E9,24],[1.464780005756396E9,24],[1.464779990751916E9,24],[1.464779975753504E9,24],[1.464779960751926E9,24],[1.464779945752073E9,24],[1.46477993075226E9,24],[1.464779915754167E9,24],[1.464779900754967E9,24],[1.464779885756519E9,24],[1.464779870755742E9,24],[1.464779855751767E9,24],[1.464779840752564E9,24],[1.46477982575217E9,24],[1.464779810756469E9,24],[1.464779795753041E9,24],[1.464779780753594E9,24],[1.464779765756562E9,24],[1.464779750757024E9,24],[1.46477973576044E9,24]],"gc.ps_marksweep.time":[[1.464780020759302E9,977],[1.464780005756396E9,977],[1.464779990751916E9,977],[1.464779975753504E9,977],[1.464779960751926E9,977],[1.464779945752073E9,977],[1.46477993075226E9,977],[1.464779915754167E9,977],[1.464779900754967E9,977],[1.464779885756519E9,977],[1.464779870755742E9,977],[1.464779855751767E9,977],[1.464779840752564E9,977],[1.46477982575217E9,977],[1.464779810756469E9,977],[1.464779795753041E9,977],[1.464779780753594E9,977],[1.464779765756562E9,977],[1.464779750757024E9,977],[1.46477973576044E9,977]]}}
    };
    tags = [];

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
            controller = $controller('DashboardCtrl', {
                $scope: scope,
                $routeParams: { "dashboardId": 1 }
            });
            httpBackend = $httpBackend;

            httpBackend.when('GET', 'rest/dashboard?id=1').respond(dashboard1);
            httpBackend.when('GET', 'rest/allTags?').respond(tags);
            httpBackend.when('GET', 'rest/allAlerts?team=*').respond(alerts);
            httpBackend.when('GET', 'rest/checkResultsChart?check_id=4&entity=GLOBAL').respond(checks[4]);
            httpBackend.when('GET', 'rest/checkResultsChart?check_id=8&entity=zmon-controller').respond(checks[8]);
            httpBackend.when('GET', 'rest/checkResultsChart?check_id=8&entity=zmon-scheduler').respond(checks[8]);
            httpBackend.when('GET', 'rest/checkResultsChart?check_id=9&entity=zmon-metric-cache-localhost%5Bdc%3A1%5D').respond(checks[9]);
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    it('should initially have one alert', function() {
        httpBackend.expectGET('rest/dashboard?id=1');
        httpBackend.expectGET('rest/allTags?');
        httpBackend.expectGET('rest/allAlerts?team=*');
        httpBackend.expectGET('rest/checkResultsChart?check_id=9&entity=zmon-metric-cache-localhost%5Bdc%3A1%5D');
        httpBackend.expectGET('rest/checkResultsChart?check_id=4&entity=GLOBAL');
        httpBackend.expectGET('rest/checkResultsChart?check_id=8&entity=zmon-controller');
        httpBackend.expectGET('rest/checkResultsChart?check_id=8&entity=zmon-scheduler');
        httpBackend.flush();
        expect(scope.alerts.length).toBe(3);
    });

    it('should have chart data for alerts with entities', function() {
        httpBackend.expectGET('rest/dashboard?id=1');
        httpBackend.expectGET('rest/allTags?');
        httpBackend.expectGET('rest/allAlerts?team=*');
        httpBackend.expectGET('rest/checkResultsChart?check_id=9&entity=zmon-metric-cache-localhost%5Bdc%3A1%5D');
        httpBackend.expectGET('rest/checkResultsChart?check_id=4&entity=GLOBAL');
        httpBackend.expectGET('rest/checkResultsChart?check_id=8&entity=zmon-controller');
        httpBackend.expectGET('rest/checkResultsChart?check_id=8&entity=zmon-scheduler');
        httpBackend.flush();
        expect(scope.charts[2].length).toBe(9);
        expect(scope.charts[3].length).toBe(1);
        expect(scope.charts[8].length).toBe(0);
    });

});
