angular.module('zmon2App')
  .filter('jsonSort', function() {

      /**
       * Returns a stringified array of objects of any kind
       */
      function arrayToArrayOfJsonObjects(array) {
        return wrapInBrackets(
          array.reduce((jsonString, item, index) => {
            let jsonRow = getSortedJsonValue(item);

            jsonRow = chainJson(jsonRow, index, array.length);
            return jsonString.concat(jsonRow);
          }, '')
        );
      }

      function wrapInBrackets(wrapped) {
        return `[${wrapped}]`;
      }

      function wrapInCurlyBrackets(wrapped) {
        return `{${wrapped}}`;
      }

      /**
       *  returns a stringified object to which a comma is appended 
       *  if it is the last object of the collection it belongs to 
       */
      function chainJson(obj, index, length) {
        if (index !== length - 1) {
          return obj + ',';
        }
        return obj;
      }

      function isArray(val) {
        return typeof(val) === 'object' && val.length >= 0;
      }

      function getType(value) {
        if (value === null) {
          return 'null';
        }
        
        if (isArray(value)) {
          return 'array';
        }
        return typeof(value);
      }

      /**
       * @param {any} value - The object that has to be serialized to a string
       * Returns an Object stringified depending on it's type
       * This is where the recursion takes place in case of nested objects
       * by calling either arrayToArrayOfJsonObjects in case the object passed is an array
       * or sortObject in case it is an plain object
       */
      function getSortedJsonValue(value) {
        const ops = {
          'number': (val) => val,
          'boolean': (val) => val,
          'array': arrayToArrayOfJsonObjects,
          'object': sortObject,
          'string': val => `"${val}"`,
          'null': val => null
        };
        const stringifyingFn = ops[getType(value)];

        return stringifyingFn(value);
      }

      function stringifyKeyValue([key, value]) {
        return `"${key}":${getSortedJsonValue(value)}`;
      }
      
      /**
       * Concatenates key value pairs, in order to return a stringified Objects
       */
      function stringifyArrayOfKeyValue(arrayOfKeyValue) {
        return arrayOfKeyValue.reduce((json, keyValue, index) => {
          const jsonRow = stringifyKeyValue(keyValue);

          return json.concat(chainJson(jsonRow, index, arrayOfKeyValue.length))
        }, '');
      }

      /**
       * Given an object, returns an array of key value pairs sorted by key
       * @param {Object} obj - the object which's keys have to be sorted
       */
      function getSortedObjAsArray(obj) {  
        const map = new Map();

        Object.keys(obj).forEach(key => map.set(key, obj[key]));
        const objAsArray = [...map.entries()].sort((a, b) => {
          return a[0].toLowerCase().localeCompare(b[0].toLowerCase());
        });

        return objAsArray;
      }

      function pipe(...fns) {
        return x => fns.reduce((v, f) => f(v), x);
      }

      function sortObject(obj) {
        return pipe(
          getSortedObjAsArray,
          stringifyArrayOfKeyValue,
          wrapInCurlyBrackets,
        )(obj);
      }
  
  return pipe(getSortedJsonValue, JSON.parse);
});
