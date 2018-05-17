angular.module('zmon2App').factory('EntityFilterTypesService', ['$q', 'debounce', 'CommunicationService',
    function($q, debounce, CommunicationService) {
    	console.log('EntityFilterTypesService started');

    	var service = {};

    	service.entityProperties = {};

    	service.entityTypeNames = [];

    	var queue = [];

    	var fetch = function(cb) {
    		console.log('fetch');
    		CommunicationService.getEntityProperties().then(function(data) {
    			service.entityProperties = data;
	    		service.entityTypeNames = [].concat(Object.keys(data).sort());
	    		cb(data);
    		})
    	}

    	var shiftDebounced = debounce(function() { queue.shift()() }, 2000, false);

    	service.fetchEntityProperties = function(cb) {

    		var deferred = $q.defer();		

			if (service.entityTypeNames.length) {
				
				console.log('cached')

	    		deferred.resolve(service.entityProperties);	

			} else {
	    		
	    		console.log('bounce')
				queue.push(function() { fetch(function(data) {
					console.log('cb!', data, service.entityProperties)
					deferred.resolve(service.entityProperties);
				})});

				shiftDebounced();
			}

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
				console.log('properties', service.entityProperties, name);
    			deferred.resolve(service.entityProperties[name]);
    		});
    		return deferred.promise;
    	}

    	return service;
    }

]);