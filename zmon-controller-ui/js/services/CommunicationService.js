angular.module('zmon2App').factory('CommunicationService', ['$http', '$q', '$log', 'APP_CONST', 'PreconditionsService',
    function($http, $q, $log, APP_CONST, PreconditionsService) {
        var service = {},
            alertIdCache = {},
            alertNameCache = {},
            checkIdCache = {},
            checkNameCache = {};

        /**
         * Covers all http methods; the arg postSuccessProcessing is a function which will receive as single argument
         * the upon success response data in case it needs to go through additional processing before resolving the promise
         */
        var doHttpCall = function(httpMethod, endpoint, payload, extraHeaders, timeout, postSuccessProcessing) {
            PreconditionsService.isNotEmpty(httpMethod);
            PreconditionsService.isHTTPMethod(httpMethod);
            PreconditionsService.isNotEmpty(endpoint);
            /**
             * Converts a simple object (flat key/value pairs; no nested objects|arrays) into a query string
             * Used only for GETs/DELETEs; POST payload is sent as is
             */
            function objectToQueryString(obj) {
                var str = [];
                angular.forEach(obj, function(nextValue, nextKey) {
                    str.push(encodeURIComponent(nextKey) + "=" + encodeURIComponent(nextValue));
                });
                return str.join("&");
            }
            var deferred = $q.defer();

            // console.log(' method: ', httpMethod, ' / endpoint: ', endpoint, ' / payload: ', objectToQueryString(payload));
            var httpConfig = {
                method: httpMethod
            };
            if (httpMethod === "POST") {
                httpConfig.url = endpoint;
                httpConfig.data = payload;
            } else {
                // GETs & DELETEs
                httpConfig.url = endpoint + "?" + objectToQueryString(payload);
            }

            if (extraHeaders) {
                httpConfig.headers = extraHeaders;
            }

            if (timeout) {
                httpConfig.timeout = timeout;
            }

            $http(httpConfig).success(function(response, status, headers, config) {
                if (postSuccessProcessing) {
                    var result = postSuccessProcessing(response);
                    response = result ? result : response;
                }
                deferred.resolve(response);
            }).error(function(response, status, headers, config) {
                deferred.reject(status);
            });

            return deferred.promise;
        };

        /*
         * Get all the alerts based on passed filter object (empty object to get everything).
         * Format of passed param object: {'team':'Platform/Software'}
         */
        service.getAllAlerts = function(filter) {
            var params = {};
            var timeout = 20000;
            if (filter.team) {
                params.team = filter.team;
            }
            if (filter.tags) {
                params.tags = filter.tags;
            }

            return doHttpCall("GET", "rest/allAlerts", params, null, timeout);
        };

        /*
         * Get alerts by ID.
         */
        service.getAlertsById = function(ids) {
            if (!ids) {
                ids = "";
            } else if (_.isArray(ids)) {
                ids = ids.join(',');
            }
            var params = {
                id: ids
            };
            return doHttpCall("GET", "rest/alertsById", params);
        };

        service.getAlertDetails = function(alertId) {
            PreconditionsService.isNotEmpty(alertId);
            PreconditionsService.isNumber(alertId);
            var params = {
                "alert_id": alertId
            };
            return doHttpCall("GET", "rest/alertDetails", params);
        };

        /*
         * Returns check results for passed checkId with optional limitCount & entityId filter
         */
        service.getCheckResults = function(checkId, entityId, limitCount) {
            PreconditionsService.isNotEmpty(checkId);
            PreconditionsService.isNumber(checkId);
            var params = {
                "check_id": checkId
            };
            var timeout = 5000;
            if (entityId) {
                PreconditionsService.isNumber(entityId);
                params.entity = entityId;
            }
            if (limitCount) {
                PreconditionsService.isNumber(limitCount);
                params.limit = limitCount;
            }
            return doHttpCall("GET", "rest/checkResults", params, null, timeout);
        };

        /*
         * Returns check results for passed checkId with optional limitCount & entityId filter without entity information
         */
        service.getCheckResultsWithoutEntities = function(checkId, entityId, limitCount) {
            PreconditionsService.isNotEmpty(checkId);
            PreconditionsService.isNumber(checkId);
            var params = {
                "check_id": checkId
            };
            var timeout = 5000;
            if (entityId) {
                PreconditionsService.isNumber(entityId);
                params.entity = entityId;
            }
            if (limitCount) {
                PreconditionsService.isNumber(limitCount);
                params.limit = limitCount;
            }
            return doHttpCall("GET", "rest/checkResultsWithoutEntities", params, null, timeout);
        };

        /*
         * Returns chart data of check results for passed checkId with optional limitCount & entityId filter
         */
        service.getCheckResultsChart = function(checkId, entityId, limitCount) {
            PreconditionsService.isNotEmpty(checkId);
            PreconditionsService.isNumber(checkId);
            var params = {
                "check_id": checkId
            };
            var timeout = 5000;
            if (entityId) {
                PreconditionsService.isNumber(entityId);
                params.entity = entityId;
            }
            if (limitCount) {
                PreconditionsService.isNumber(limitCount);
                params.limit = limitCount;
            }
            return doHttpCall("GET", "rest/checkResultsChart", params, null, timeout);
        };

        service.getCheckResultsForAlert = function(alertId, limitCount) {
            PreconditionsService.isNotEmpty(alertId);
            PreconditionsService.isNumber(alertId);
            var params = {
                "alert_id": alertId
            };
            if (limitCount) {
                PreconditionsService.isNumber(limitCount);
                params.limit = limitCount;
            }
            return doHttpCall("GET", "rest/checkAlertResults", params);
        };

        service.getKairosResults = function(options) {
            return doHttpCall("POST", "rest/kairosdbs/kairosdb/api/v1/datapoints/query", options);
        };

        service.getCloudViewEndpoints = function(params) {
            return doHttpCall("GET", "rest/cloud-view-endpoints", params);
        };

        service.getEntityMetaData = function(query) {
            var params = JSON.stringify(query);
            return doHttpCall("POST", "rest/entities", params);
        };

        service.getCloudData = function(type) {
            var t = {
                type: type
            };

            return service.getEntityMetaData(t);
        };

        service.getCheckResultsFiltered = function(id, filter) {
            return doHttpCall("GET", "rest/lastResults/"+id+"/"+filter);
        };

        service.getAlertDefinitions = function(team, checkId) {
            var params = {};
            if (team) {
                params.team = team;
            }
            if (checkId) {
                params.check_id = checkId;
            }
            var postSuccessProcessing = function(data) {
                _.each(data, function(item) {
                    alertNameCache[item.name] = 1;
                    alertIdCache[item.id] = item.name;
                });
            };
            return doHttpCall("GET", "rest/alertDefinitions", params, null, null, postSuccessProcessing);
        };

        service.getAlertDefinition = function(id) {
            var params = {};
            if (id) {
                PreconditionsService.isNumber(id);
                params.id = id;
            }
            var postSuccessProcessing = function(data) {
                alertNameCache[data.name] = 1;
                alertIdCache[data.id] = data.name;

                // parameters come as a JSON of stringified JSON objects. i.e. { 'key': "{}" }
                _.each(data.parameters, function(param, key) {
                    data.parameters[key] = JSON.parse(param);
                });
            };
            return doHttpCall("GET", "rest/alertDefinition", params, null, null, postSuccessProcessing);
        };

        service.getAlertDefinitionNode = function(id) {
            var params = {};
            if (id) {
                PreconditionsService.isNumber(id);
                params.id = id;
            }
            var postSuccessProcessing = function(data) {
                alertNameCache[data.name] = 1;
                alertIdCache[data.id] = data.name;

                // parameters come as a JSON of stringified JSON objects. i.e. { 'key': "{}" }
                _.each(data.parameters, function(param, key) {
                    data.parameters[key] = JSON.parse(param);
                });
            };
            return doHttpCall("GET", "rest/alertDefinitionNode", params, null, null, postSuccessProcessing);
        };

        service.getAlertDefinitionChildren = function(id) {
            var params = {};
            if (id) {
                PreconditionsService.isNumber(id);
                params.id = id;
            }
            var postSuccessProcessing = function(data) {

                // parameters come as a JSON of stringified JSON objects. i.e. { 'key': "{}" }
                _.each(data.parameters, function(param, key) {
                    data.parameters[key] = JSON.parse(param);
                });
            };
            return doHttpCall("GET", "rest/alertDefinitionChildren", params, null, null, postSuccessProcessing);
        };

        service.forceAlertEvaluation = function(id) {
            var params = {};
            if (id) {
                PreconditionsService.isNumber(id);
                params.alert_definition_id = id;
            }
            return doHttpCall("POST", "rest/forceAlertEvaluation", params);
        };

        service.forceAlertCleanup = function(id) {
            var params = {};
            if (id) {
                PreconditionsService.isNumber(id);
                params.alert_definition_id = id;
            }
            return doHttpCall("POST", "rest/cleanAlertState", params);
        };

        service.getAllTeams = function() {
            return doHttpCall("GET", "rest/allTeams");
        };

        service.getAllTags = function() {
            var postSuccessProcessing = function(response) {
                var tags = [];
                _.each(response.sort(), function(t) {
                    tags.push({ 'id': t, 'text': t });
                });
                return tags;
            };
            return doHttpCall("GET", "rest/allTags", null, null, null, postSuccessProcessing);
        };

        service.alertNotificationsAck = function(id) {
            PreconditionsService.isNotEmpty(id);
            var params = {
                "alert_id": id
            };
            return doHttpCall("PUT", "rest/alertNotificationsAck/", params)
        }

        service.getDashboard = function(id) {
            PreconditionsService.isNotEmpty(id);
            PreconditionsService.isNumber(id);
            var params = {
                "id": id
            };
            return doHttpCall("GET", "rest/dashboard", params);
        };

        service.getAllDashboards = function() {
            return doHttpCall("GET", "rest/allDashboards");
        };

        service.deleteDashboard = function(id) {
            PreconditionsService.isNotEmpty(id);
            PreconditionsService.isNumber(id);
            var params = {
                "id": id
            };
            return doHttpCall("DELETE", "rest/deleteDashboard", params);
        };

        service.getCheckDefinitions = function(team) {
            var params = {};

            if (team) {
                params.team = team;
            }

            return doHttpCall("GET", "rest/checkDefinitions", params);
        };

        service.getCheckDefinition = function(id) {
            PreconditionsService.isNotEmpty(id);
            PreconditionsService.isNumber(id);
            var params = {
                "check_id": id
            };
            return doHttpCall("GET", "rest/checkDefinition", params);
        };

        service.updateCheckDefinition = function(def) {
            var deferred = $q.defer();
            if (def.id) {
                if (def.name != checkIdCache[def.id]) {
                    checkNameCache[def.name] = 1;
                    delete checkNameCache[checkIdCache[def.id]];
                }
            } else {
                checkNameCache[def.name] = 1;
            }

            $http({
                method: 'POST',
                url: 'rest/updateCheckDefinition',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: def
            }).success(function(data, status, headers, config) {
                checkIdCache[data.id] = data.name;
                deferred.resolve(data);
            }).error(function(data, status, headers, config) {
                checkNameCache[alertIdCache[def.id]] = 1;
                delete checkNameCache[def.name];
                deferred.reject(status);
            });
            return deferred.promise;
        };

        service.getMatchedEntities = function(filter) {
            PreconditionsService.isNotEmpty(filter);
            var headers = {
                'Content-Type': 'application/json'
            };
            return doHttpCall("POST", "rest/entity-filters", filter, headers);
        }

        service.getStatus = function() {
            return doHttpCall("GET", "rest/status");
        };

        service.updateAlertDefinition = function(def) {
            var deferred = $q.defer();
            if (def.id) {
                if (def.name != alertIdCache[def.id]) {
                    alertNameCache[def.name] = 1;
                    delete alertNameCache[alertIdCache[def.id]];
                }
            } else {
                alertNameCache[def.name] = 1;
            }

            $http({
                method: 'POST',
                url: 'rest/updateAlertDefinition',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: def
            }).success(function(data, status, headers, config) {
                alertIdCache[data.id] = data.name;
                deferred.resolve(data);
            }).error(function(data, status, headers, config) {
                alertNameCache[checkIdCache[def.id]] = 1;
                delete alertNameCache[def.name];
                deferred.reject(status);
            });
            return deferred.promise;
        };

        service.deleteAlertDefinition = function(alertId) {
            PreconditionsService.isNotEmpty(alertId);
            PreconditionsService.isNumber(alertId);
            var params = {
                id: alertId
            };
            return doHttpCall('DELETE', 'rest/deleteAlertDefinition', params);
        };

        service.updateDashboard = function(dashboard) {
            PreconditionsService.isNotEmpty(dashboard);
            var headers = {
                'Content-Type': 'application/json'
            };
            return doHttpCall("POST", "rest/updateDashboard", dashboard, headers);
        };

        service.isValidAlertName = function(name) {
            return !(name in alertNameCache);
        };

        /*
         * Fetches downtimes for specific alert; if omitted, fetches all downtimes of all alerts
         */
        service.getDowntimes = function(alertDefId) {
            PreconditionsService.isNotEmpty(alertDefId);
            PreconditionsService.isNumber(alertDefId);
            var params = {
                "alert_definition_id": alertDefId
            };
            return doHttpCall("GET", "rest/downtimes", params);
        };

        /**
         * Generic downtime scheduler. The entitiesPerAlert is an array of objects each of which has format:
         *      {   "alert_definition": 1,
         *          "entity_ids": ["a", "b"]
         *      }
         */
        service.scheduleDowntime = function(downtime) {
            PreconditionsService.isNotEmpty(downtime);
            PreconditionsService.isNotEmpty(downtime.comment);
            PreconditionsService.isNotEmpty(downtime.startTime);
            PreconditionsService.isDate(downtime.startTime);
            PreconditionsService.isNotEmpty(downtime.endTime);
            PreconditionsService.isDate(downtime.endTime);
            PreconditionsService.isNotEmpty(downtime.entity_ids);
            PreconditionsService.isNotEmpty(downtime.alert_definition_id);
            var postData = {
                "comment": downtime.comment,
                "start_time": parseInt(downtime.startTime.getTime() / 1000, 10), // convert to seconds
                "end_time": parseInt(downtime.endTime.getTime() / 1000, 10),
                "downtime_entities": [{
                    "alert_definition_id": downtime.alert_definition_id,
                    "entity_ids": downtime.entity_ids
                }]
            };
            var headers = {
                'Content-Type': 'application/json;charset=utf-8'
            };
            return doHttpCall("POST", "rest/scheduleDowntime", postData, headers);
        };

        /**
         * NOTE: we cannot use doHttpCall() because we cannot pass params through object
         * as we normally do because in this case all keys are the same ("downtime_id")
         */
        service.deleteDowntime = function(downtimeUUIDs) {
            PreconditionsService.isNotEmpty(downtimeUUIDs);
            var deferred = $q.defer();
            var deleteUrl = 'rest/deleteDowntimes?';
            _.each(downtimeUUIDs, function(nextUUID) {
                deleteUrl += '&downtime_id=' + nextUUID;
            });

            $http({
                method: 'DELETE',
                url: deleteUrl
            }).success(function(data, status, headers, config) {
                deferred.resolve(data);
            }).error(function(data, status, headers, config) {
                deferred.reject(status);
            });
            return deferred.promise;
        };

        service.initTrialRun = function(params) {
            PreconditionsService.isNotEmpty(params.alert_condition);
            PreconditionsService.isNotEmpty(params.check_command);
            PreconditionsService.isNotEmpty(params.name);
            var postData = params;
            var headers = {
                'Content-Type': 'application/json'
            };
            return doHttpCall("POST", "rest/scheduleTrialRun", postData, headers);
        };

        service.getTrialRunResult = function(trialRunId) {
            PreconditionsService.isNotEmpty(trialRunId);
            PreconditionsService.isNumber(trialRunId);
            var params = {
                "id": trialRunId
            };
            return doHttpCall("GET", "rest/trialRunResults", params);
        };

        service.insertAlertComment = function(params) {
            var postData = params;
            var headers = {
                'Content-Type': 'application/json'
            };
            return doHttpCall("POST", "rest/comment", postData, headers);
        };

        service.getAlertComments = function(alertId, limit, offset) {
            PreconditionsService.isNotEmpty(alertId);
            PreconditionsService.isNumber(alertId);
            PreconditionsService.isNotEmpty(limit);
            PreconditionsService.isNumber(limit);
            PreconditionsService.isNotEmpty(offset);
            PreconditionsService.isNumber(offset);
            var params = {
                "alert_definition_id": alertId,
                "limit": limit,
                "offset": offset
            };
            return doHttpCall("GET", "rest/comments", params);
        };

        service.deleteAlertComment = function(commentId) {
            PreconditionsService.isNotEmpty(commentId);
            PreconditionsService.isNumber(commentId);
            var params = {
                "id": commentId
            };
            return doHttpCall("DELETE", "rest/deleteComment", params);
        };

        service.getAlertHistory = function(alertId, fromEpoch, toEpoch, batchSize) {
            PreconditionsService.isNotEmpty(alertId);
            PreconditionsService.isNumber(alertId);
            var params = {
                "alert_definition_id": alertId
            };
            if (batchSize) {
                PreconditionsService.isNumber(batchSize);
                params.limit = batchSize;
            }
            if (fromEpoch) {
                PreconditionsService.isNumber(fromEpoch);
                params.from = fromEpoch;
            }
            if (toEpoch) {
                PreconditionsService.isNumber(toEpoch);
                params.to = toEpoch;
            }
            return doHttpCall("GET", "rest/alertHistory", params);
        };

        /*
         * Fetches check or alert definition history of changes
         */
        service.getAllChanges = function(params) {
            PreconditionsService.isNotEmpty(params.alert_definition_id);
            PreconditionsService.isNumber(params.alert_definition_id);
            PreconditionsService.isNotEmpty(params.limit);
            PreconditionsService.isNumber(params.limit);
            PreconditionsService.isNotEmpty(params.from);
            PreconditionsService.isNumber(params.from);
            url = params.check_definition_id ? 'rest/checkDefinitionHistory' : 'rest/alertDefinitionHistory';
            return doHttpCall("GET", url, params);
        };

        service.getEntityProperties = function() {
            return doHttpCall("GET", 'rest/entityProperties');
        };

        service.getAlertCoverage = function(entityFilter) {
            var postData = entityFilter;
            var headers = {
                'Content-Type': 'application/json'
            };
            return doHttpCall("POST", "rest/alertCoverage", postData, headers);
        };

        service.search = function(query, teams) {
            PreconditionsService.isNotEmpty(query);
            var params = {
                query: query
            };
            if (teams) {
                params.teams = teams;
            }
            return doHttpCall("GET", "rest/search", params);
        };

        service.getNotificationAlerts = function() {
            return doHttpCall("GET", "/rest/notifications/alerts");
        };

        service.subscribeNotificationAlert = function(id) {
            PreconditionsService.isNotEmpty(id);
            var params = {
                alert_id: id
            };
            return doHttpCall("POST", "/rest/notifications/alerts", params)
        };

        service.removeNotificationAlert = function(id) {
            PreconditionsService.isNotEmpty(id);
            var params = {
                alert_id: id
            };
            return doHttpCall("DELETE", "/rest/notifications/alerts", params);
        };

        service.getNotificationTeams = function() {
            return doHttpCall("GET", "/rest/notifications/teams");
        };

        service.subscribeNotificationTeam = function(id) {
            PreconditionsService.isNotEmpty(id);
            var params = {
                team: id
            };
            return doHttpCall("POST", "/rest/notifications/teams", params);
        };

        service.removeNotificationTeam = function(id) {
            PreconditionsService.isNotEmpty(id);
            var params = {
                team: id
            };
            return doHttpCall("DELETE", "/rest/notifications/teams", params);
        };

        service.getNotificationPriority = function() {
            return doHttpCall("GET", "/rest/notifications/priority");
        };

        service.subscribeNotificationPriority = function(prio) {
            PreconditionsService.isNotEmpty(prio);
            var params = {
                priority: prio
            };
            return doHttpCall("POST", "/rest/notifications/priority", params);
        };

        service.sendNotificationToken = function(token) {
            PreconditionsService.isNotEmpty(token);
            var params = {
                registration_token: token
            };
            return doHttpCall("POST", "/rest/notifications/devices", params);
        };

        service.removeNotificationToken = function(token) {
            PreconditionsService.isNotEmpty(token);
            var params = {
                registration_token: token
            };
            return doHttpCall("DELETE", "/rest/notifications/devices", params);
        };

        service.getSubscriptions = function() {
            return doHttpCall("GET", "/rest/user/subscriptions");
        };

        return service;
    }
]);
