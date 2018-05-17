angular.module('zmon2App').factory('EntityFilterTypesService', ['$q', 'debounce', 'CommunicationService',
    function($q, debounce, CommunicationService) {

    	var service = {};

    	service.entityProperties = {};

    	service.entityTypeNames = [];

    	var queue = [];

    	var fetch = function(cb) {
    		
    		if (service.entityTypeNames.length) {
    			resolveQueue();
    			return cb(service.entityProperties);
    		} 

    		CommunicationService.getEntityProperties().then(function(data) {
    			service.entityProperties = data;
	    		service.entityTypeNames = [].concat(Object.keys(data).sort());
	    		resolveQueue();
	    		cb(data);
    		});
    	}

    	var resolveQueue = function() {
    		while (queue.length) {
    			queue.shift()();
    		}
    	}

    	var shiftDebounced = debounce(function() { queue.shift()() }, 0, false);

    	service.fetchEntityProperties = function(cb) {
    		var deferred = $q.defer();		
			queue.push(function() { fetch(function(data) {
				deferred.resolve(service.entityProperties);
		
			})});
			shiftDebounced();
    		return deferred.promise;
    	}


    	service.getEntityTypeNames = function() {
    		var deferred = $q.defer();
			service.fetchEntityProperties().then(function() {
				deferred.resolve(service.entityTypeNames);
			});
    		return deferred.promise;
    	}

    	service.getEntityProperties = function() {
	   		var deferred = $q.defer();
   			service.fetchEntityProperties().then(function() {
    			deferred.resolve(service.entityProperties);
    		});
    		return deferred.promise;

    	}

    	service.getEntityPropertiesByName = function(name) {
    		var deferred = $q.defer();
			service.fetchEntityProperties().then(function() {
    			deferred.resolve(service.entityProperties[name]);
    		});
    		return deferred.promise;
    	}

    	return service;
    }

]);