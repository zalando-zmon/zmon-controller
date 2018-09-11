var TrialRunCtrl = function ($scope, $interval, $timeout, timespanFilter, CommunicationService, MainAlertService, FeedbackMessageService, UserInfoService, $window, $location,$routeParams) {

    $scope.$parent.activePage = 'trial-run';
    MainAlertService.removeDataRefresh();

    /** Trail Run Class
     Collection of methods for communication with data provider, periodic running and termination of periodic run process
     # init - Instance configuration and registration of test on back-end side, as result returning promise object
     # get - Calling data provider, as result returning promise object
     # periodically - Starting the pulling request periodically, return result via callback
     # terminate - Canceling the periodic pull requests
     # autoTerminate - Time limiter for periodic pull requests, terminating them after 5 minutes
     # convert - Converting JSON like string to JSON
     */

    var TrialRun = function (ctrl) {
        var self = this;
        this.uid = null;
        this.config = {};

        this.init = function (config) {
            self.config = self.convert(config);
            self.autoTerminate();
            return CommunicationService.initTrialRun(self.config);
        };

        this.get = function () {
            if (self.uid) return CommunicationService.getTrialRunResult(self.uid);
        };

        this.periodically = function (interval, cb) {
            if (!self.interval) {
                self.interval = $interval(function () {
                    self.get().then(function (data) {
                        cb(data);
                    });
                }, interval);
            }
        };

        this.terminate = function () {
            if (self.interval) {
                $interval.cancel(self.interval);
                delete self.interval;
            }
        };

        this.autoTerminate = function () {
            var interval = $interval(function () {
                self.terminate();
                ctrl.stop();
                $interval.cancel(interval);
            }, 4 * 60000);
        };

        this.convert = function (data) {
            config = angular.copy(data);

            Object.keys(config).forEach(function (key) {
                try {
                    config[key] = JSON.parse(config[key]);
                } catch (e) {
                }
            });

            return config;
        };
    };

    var trc = $scope.TrialRunCtrl = this;

    var formParametersObject = function() {
        var parameters = {};
        _.each(trc.parameters, function(param) {
            var val = param.value;

            if (param.type === 'int') {
                val = parseInt(param.value);
            }
            if (param.type === 'float') {
                val = parseFloat(param.value);
            }

            parameters[param.name] = {
                "value": val,
                "comment": param.comment,
                "type": param.type
            };
        });
        return parameters;
    };

    var formParametersArray = function(pObj) {
        var parameters = [];
        _.each(pObj, function(p, name) {
            parameters.push({
                name: name,
                value: p.value,
                comment: p.comment,
                type: p.type
            });
        });
        return parameters;
    };

    var updateUrlParameters = function() {
        $location.search('json', JSON.stringify($scope.alert)).replace();
    };

    var getMatchedEntities = function() {
        if ($scope.filter.include_filters[0].length === 0
          && $scope.filter.include_filters[1].length === 0
          && $scope.filter.exclude_filters[0].length === 0) {
            $scope.matchedEntitiesCount = null;
            $scope.matchedEntities = [];
            return;
        }

        CommunicationService.getMatchedEntities($scope.filter).then(function(response) {
            $scope.matchedEntitiesCount = response.count;
            $scope.matchedEntities = _.map(response.entities, 'id');
        })
    };

    var user = UserInfoService.get();
    trc.teams = user.teams !== "" ? user.teams.split(',') : [];

    // Alert Parameters initial data
    trc.alertParameters = [];
    trc.paramTypes = ['string', 'int', 'boolean', 'date'];
    trc.parameterTypeOptions = [
        {
            'value': 'str',
            'label': 'String'
        },
        {   'value': 'int',
            'label': 'Integer'
        },
        {   'value': 'float',
            'label': 'Float'
        },
        {
            'value': 'bool',
            'label': 'Boolean'
        }
    ];

    trc.parameterTypeBooleanOptions = [
        {
            'value': true,
            'label': 'True'
        },
        {
            'value': false,
            'label': 'False'
        }
    ];

    // Entities filter data
    trc.entityFilter = {};
    trc.entityExcludeFilter = {};

    // User can define the entity filters either as plain JSON text or through a form which corresponds to an array of objects
    trc.entityFilter.formEntityFilters = [];
    trc.entityFilter.textEntityFilters = [];
    trc.entityExcludeFilter.formEntityFilters = [];
    trc.entityExcludeFilter.textEntityFilters = [];

    // Flag to toggle UI on whether user types JSON text or uses form to define the entity filters
    trc.entityFilterInputMethod = 'text';
    trc.entityExcludeFilterInputMethod = 'text';

    trc.INDENT = '    ';

    trc.alerts = [];
    trc.parameters = [];
    trc.formVisible = true;
    trc.alertsVisible = false;
    trc.criticalAlerts = true;
    trc.normalAlerts = false;
    trc.criticalAlertsCounter = 0;
    trc.normalAlertsCounter = 0;
    trc.running = false;
    trc.progress = 0;
    trc.authorized = Object.keys(UserInfoService.get()).length > 0;

    $scope.onRun = false;

    $scope.sortType = 'entity.id';
    $scope.sortOrder = false;

    $scope.matchedEntitiesCount = null;
    $scope.matchedEntities = [];

    $scope.filter = {
        include_filters: [[],[]],
        exclude_filters: [[]]
    };

    $scope.alert = _.extend($scope.alert || {}, {
       entities: [],
       entities_exclude: [],
       parameters: [],
       interval: 120
    });

    // Deep watch
    $scope.$watch('alert', function () {
        // Clean up previous blob and create new one
        window.URL.revokeObjectURL($scope.blob);
        try {
            $scope.blob = new Blob([trc.buildYAMLContent()], {
                type: 'text/plain'
            });
            $scope.yamlBlobUrl = window.URL.createObjectURL($scope.blob);
        } catch (e) {
            console.log('Failed to create blob. Probably empty content?');
        }
        updateUrlParameters();
    }, true);

    // If entity filter input method is 'form', reflect changes of entityFilter.formEntityFilters on entityFilter.textEntityFilters
    $scope.$watch('TrialRunCtrl.entityFilter.formEntityFilters', function (newVal, oldVal) {
        if (trc.entityFilterInputMethod === 'form') {
            // Process a copy so we safely remove $$hashKey property which we don't want to be transfered to entityFilter.textEntityFilters
            var formEntityFiltersClone = angular.copy(trc.entityFilter.formEntityFilters);
            for (var p in formEntityFiltersClone) {
                if (formEntityFiltersClone.hasOwnProperty(p) && p === '$$hashKey') {
                    delete formEntityFiltersClone[p];
                }
            }
            trc.entityFilter.textEntityFilters = JSON.stringify(formEntityFiltersClone, null, trc.INDENT);
            $scope.alert.entities = angular.copy(formEntityFiltersClone);

            $scope.filter.include_filters[0] = formEntityFiltersClone;
            getMatchedEntities();
        }
    }, true);

    // Same as above, for excluded entities.
    $scope.$watch('TrialRunCtrl.entityExcludeFilter.formEntityFilters', function (newVal, oldVal) {
        if (trc.entityExcludeFilterInputMethod === 'form') {
            // Process a copy so we safely remove $$hashKey property which we don't want to be transfered to entityExcludeFilter.textEntityFilters
            var formEntityFiltersClone = angular.copy(trc.entityExcludeFilter.formEntityFilters);
            for (var p in formEntityFiltersClone) {
                if (formEntityFiltersClone.hasOwnProperty(p) && p === '$$hashKey') {
                    delete formEntityFiltersClone[p];
                }
            }
            trc.entityExcludeFilter.textEntityFilters = JSON.stringify(formEntityFiltersClone, null, trc.INDENT);
            $scope.alert.entities_exclude = angular.copy(formEntityFiltersClone);

            $scope.filter.exclude_filters[0] = formEntityFiltersClone;
            getMatchedEntities();

        }
    }, true);

    // If entity filter input method is 'text', reflect changes of entityFilter.textEntityFilters on entityFilter.formEntityFilters
    $scope.$watch('TrialRunCtrl.entityFilter.textEntityFilters', function (newVal, oldVal) {
        if (trc.entityFilterInputMethod === 'text') {
            try {
                $scope.alert.entities = JSON.parse(trc.entityFilter.textEntityFilters);
                trc.entityFilter.formEntityFilters = JSON.parse(trc.entityFilter.textEntityFilters);
                trc.invalidFormat = false;

                $scope.filter.include_filters[0] = $scope.alert.entities;
                getMatchedEntities();
            } catch (ex) {
                trc.invalidFormat = true;
            }
        }
    }, true);

    // Same as above, for excluded entities.
    $scope.$watch('TrialRunCtrl.entityExcludeFilter.textEntityFilters', function (newVal, oldVal) {
        if (trc.entityExcludeFilterInputMethod === 'text') {
            try {
                $scope.alert.entities_exclude = JSON.parse(trc.entityExcludeFilter.textEntityFilters);
                trc.entityExcludeFilter.formEntityFilters = JSON.parse(trc.entityExcludeFilter.textEntityFilters);
                trc.invalidFormat = false;
                $scope.filter.exclude_filters[0] = $scope.alert.entities_exclude;
                getMatchedEntities();
            } catch (ex) {
                trc.invalidFormat = true;
            }
        }
    }, true);

   $scope.$watch('TrialRunCtrl.parameters', function() {
       $scope.alert.parameters = formParametersObject($scope.TrialRunCtrl.parameters);
   }, true);

   if ($location.search().json) {
    var urlJson =JSON.parse($location.search().json)
    }

     if($routeParams.checkId){
        $scope.checkId = $routeParams.checkId
            CommunicationService.getCheckDefinition($scope.checkId).then(
                function(response) {
                    $scope.alert = response;
                    $scope.alert.check_command = $scope.alert.command;

                    if (urlJson) {
                        _.extend($scope.alert, urlJson);
                    }
                    if ($scope.alert.owning_team && trc.teams.indexOf($scope.alert.owning_team) === -1) {
                        trc.teams.push($scope.alert.owning_team);
                    }
                    trc.entityFilter.formEntityFilters = $scope.alert.entities;
                    trc.entityFilter.textEntityFilters = JSON.stringify($scope.alert.entities, null, trc.INDENT);
                    trc.entityExcludeFilter.formEntityFilters = $scope.alert.entities_exclude ? $scope.alert.entities_exclude:[];
                    trc.entityExcludeFilter.textEntityFilters = $scope.alert.entities_exclude ? JSON.stringify($scope.alert.entities_exclude, null, trc.INDENT):"[]";
                    trc.parameters = formParametersArray($scope.alert.parameters);
                

                   
                    }
            );
        
    }else if (urlJson) {
        _.extend($scope.alert, urlJson);
    }
    
    // One-time set of the entityFilter.formEntityFilters and entityFilter.textEntityFilters now that we have alert definition
    trc.entityFilter.formEntityFilters = $scope.alert.entities;
    trc.entityFilter.textEntityFilters = JSON.stringify($scope.alert.entities, null, trc.INDENT);
    trc.entityExcludeFilter.formEntityFilters = $scope.alert.entities_exclude;
    trc.entityExcludeFilter.textEntityFilters = JSON.stringify($scope.alert.entities_exclude, null, trc.INDENT);
    trc.parameters = formParametersArray($scope.alert.parameters);

    var tr = new TrialRun(trc);

    // save as new check
    trc.save = function (isNew) {

        if ($scope.trForm.$valid) {
            try {
                if (trc.entityFilter.textEntityFilters === '') {
                    delete $scope.alert.entities;
                } else {
                    $scope.alert.entities = JSON.parse(trc.entityFilter.textEntityFilters);
                }

                if (trc.entityExcludeFilter.textEntityFilters === '') {
                    delete $scope.alert.entities_exclude;
                } else {
                    $scope.alert.entities_exclude = JSON.parse(trc.entityExcludeFilter.textEntityFilters);
                }

                if (trc.parameters.length === 0) {
                    delete $scope.alert.parameters;
                } else {
                    $scope.alert.parameters = formParametersObject();
                }

                if (typeof $scope.alert.period === 'undefined') {
                    $scope.alert.period = "";
                }

            } catch (ex) {
                trc.invalidFormat = true;
                return FeedbackMessageService.showErrorMessage('JSON format is incorrect' + ex);
            }
           
            var obj = {};
            obj.name = $scope.alert.name.trim();
            obj.entities = $scope.alert.entities;
            obj.command = $scope.alert.check_command;
            obj.interval = $scope.alert.interval;
            obj.description = $scope.alert.description;
            obj.owning_team = $scope.alert.owning_team.trim();
            obj.technical_details = $scope.alert.technical_details;
            obj.status = isNew ? "ACTIVE" : $scope.alert.status;
            obj.id = isNew ? undefined : $scope.alert.id // this id is actually the check id

            MainAlertService.isValidCheckName(obj).then((valid)=>{
                if(valid){
                    CommunicationService.updateCheckDefinition(obj).then(function(data) {
                        FeedbackMessageService.showSuccessMessage('Saved successfully; redirecting...', 500, function() {
                            $location.path('/check-definitions/view/' + data.id);
                        });
                    });
                }else{
                    $("#alertModal .modal-body").html(`A check with name <b>${obj.name}</b> already exists for team <b>${obj.owning_team}</b>. Please select a different name to save.`)
                    $("#alertModal").modal();  
                   
                }
            })
        } else {
            $scope.trForm.submitted = true;
            $scope.focusedElement = null;
        }
    };

    // Download implementation
    trc.download = function ($event) {
        $event.stopPropagation();
        updateUrlParameters();

        if (!$scope.trForm.$valid) {
            $scope.trForm.submitted = true;
            return true; // form validation check
        }

        // Emulate link download.
        var a = document.createElement("a");
        a.href = $scope.yamlBlobUrl;
        a.download = trc.alertNameToFilename() + ".yaml";
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    };

    // Start trial run
    trc.run = function ($event) {

        $scope.onRun = true;
        $event.stopPropagation();
        updateUrlParameters();

        // have to wait one digest cycle before the validity of the form is set according to
        // the onRun requirements
        $timeout(function() {

            if (!$scope.trForm.$valid) {
                trc.formVisible = true;
                $scope.trForm.submitted = true;
                return true; // form validation check
            }

            // Get the latest parameters from the parameters form in propper format
            $scope.alert.parameters = formParametersObject();
            // Get the latest entities filter content before saving to local storage
            $scope.alert.entities = JSON.parse(trc.entityFilter.textEntityFilters);
            $scope.alert.entities_exclude = JSON.parse(trc.entityExcludeFilter.textEntityFilters);
            $scope.trForm.submitted = false;
            $scope.onRun = false;

            // Test run registration
            tr.init($scope.alert).then(function (alert) {
                // Settings for page state (opening/closing result/form panel, moving progress bar on 0 position...)
                tr.uid = alert.id;
                trc.running = true;
                trc.progress = 0;
                trc.formVisible = false;
                trc.alertsVisible = true;

                // Fetching alerts for test run
                tr.get().then(function (data) {
                    trc.progress = data.percentage || 5;
                    trc.alerts = data.results;
                    trc.counter();
                });

                // Scheduling periodic pull requests
                tr.periodically(3000, function (data) {
                    trc.progress = (data.percentage > 5) ? data.percentage : 5;
                    trc.alerts = data.results;
                    trc.counter();

                    // Terminating periodic pull requests after completing
                    if (trc.progress === 100) {
                        trc.done();
                        // Displaying alert
                        var message = (trc.alerts.length === 0) ? 'Your request did\'t match any entity. Please check your entities and excluded entities filter and run test again.' : 'Trial run completed.';
                        FeedbackMessageService.showSuccessMessage(message);
                    }
                });
            });
        });
    };

    // Stop trial run
    trc.stop = function () {
        tr.terminate();
        trc.running = false;
        trc.formVisible = true;
        trc.alertsVisible = false;
    };

    // Set the state of page after completed test run
    trc.done = function () {
        tr.terminate();
        trc.running = false;
        trc.formVisible = false;
        trc.alertsVisible = true;
    };

    // Calculate the number of critical and normal alerts
    trc.counter = function () {
        trc.criticalAlertsCounter = trc.alerts.filter(function (alert) {
            return alert.is_alert;
        }).length;

        trc.normalAlertsCounter = trc.alerts.filter(function (alert) {
            return !alert.is_alert;
        }).length;
    };

    trc.timeAgo = function (epochPastTs) {
        var timeIntervalSinceLastUpdate = MainAlertService.millisecondsApart(epochPastTs, MainAlertService.getLastUpdate());
        return timespanFilter(timeIntervalSinceLastUpdate);
    };

    trc.buildYAMLContent = function () {
        var alert = {
            name: $scope.alert.name,
            description: $scope.alert.description || "TrialRun Test",
            status: 'ACTIVE',
            interval: $scope.alert.interval
        };

        if (typeof $scope.alert.entities !== 'undefined' && $scope.alert.entities.length) {
            alert.entities = $scope.alert.entities;
        }
        if (typeof $scope.alert.entities_exclude !== 'undefined' && $scope.alert.entities_exclude.length) {
            entitiesExclude = $scope.alert.entities_exclude;
        }
        if ($scope.alert.period) {
            period = $scope.alert.period;
        }

        var content = jsyaml.safeDump(alert);
        content += "command: |\n  " + $scope.alert.check_command.split('\n').join('\n  ');
        content += "\nowning_team: " + $scope.alert.owning_team;
        content += "\n# OPTIONAL FIELDS\n#technical_details: Optional Technical Details\n#potential_analysis: Optional Potential analysis\n#potential_impact: Optional potential impact\n#potential_solution: Optional potential solution";
        return  content;
    };

    trc.alertNameToFilename = function () {
        return $scope.alert.name ? $scope.alert.name.toLowerCase().replace(/\s+/g, "-") : "check-definition";
    };

    // Add a new parameter with cleared values and type string by default
    trc.addParameter = function() {
        trc.parameters.push({type: 'str'});
    };

    // Remove a parameter from the parameters json object
    trc.removeParameter = function(name) {
        var index = null;
        _.each(trc.parameters, function(param, i) {
            if (param.name === name) {
                index = i;
            }
        });
        if (index !== null) {
            trc.parameters.splice(index, 1);
        }
    };

    // Validate a parameter's name to be a valid python variable name
    trc.paramNameIsValid = function(name) {
        var re = /^[_a-zA-Z][_a-zA-Z0-9]*/;
        return re.test(name);
    };

    trc.dateOptions = {
        formatYear: 'yy',
        startingDay: 1
    };

    trc.format = 'dd.MM.yyyy';

    // Used by ui-select in view to return list of teams for Team field dropdown
    // and allow inserting new values
    $scope.getItems = function(prop, search) {
        var teams = _.extend([], trc.teams);
        var options = teams.indexOf(prop) === -1 ? teams.concat(prop) : teams;
        if (search && options.indexOf(search) === -1) {
            options.unshift(search);
        }
        return options.sort();
    };
};


angular.module('zmon2App').controller('TrialRunCtrl', ['$scope', '$interval', '$timeout', 'timespanFilter', 'CommunicationService', 'MainAlertService', 'FeedbackMessageService', 'UserInfoService', '$window', '$location','$routeParams', TrialRunCtrl]);
