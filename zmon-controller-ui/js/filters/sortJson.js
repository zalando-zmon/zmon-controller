angular.module('zmon2App')
  .filter('jsonSort', function() {
 
      function arrayToArrayOfJsonObjects(array) {
        return wrapInBrackets(array.reduce((jsonString, item, index) => {
          let jsonRow = sortJson(item);

          jsonRow = chainJson(jsonRow, index, array.length);
          return jsonString.concat(jsonRow);
        }, ''));
      }

      function wrapInBrackets(wrapped) {
        return `[${wrapped}]`;
      }

      function wrapInCurlyBrackets(wrapped) {
        return `{${wrapped}}`;
      }

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

      function getSortedJsonValue(value) {
        const ops = {
          number: (val) => val,
          boolean: (val) => val,
          array: arrayToArrayOfJsonObjects,
          object: sortJson,
          string: val => `"${val}"`,
          null: val => null
        };
        const stringifyingFn = ops[getType(value)];
        return stringifyingFn(value);
      }

      function stringifyKeyValue([key, value]) {
        return `"${key}":${getSortedJsonValue(value)}`;
      }

      function stringifyArrayOfKeyValue(arrayOfKeyValue) {
        return arrayOfKeyValue.reduce((json, keyValue, index) => {
          const jsonRow = stringifyKeyValue(keyValue);

          return json.concat(chainJson(jsonRow, index, arrayOfKeyValue.length))
        }, '');
      }

      function getSortedObjAsArray(obj) {  
        const map = new Map();

        Object.keys(obj).forEach(key => map.set(key, obj[key]));
        const objAsArray = [...map.entries()].sort();

        return objAsArray;
      }

      function pipe(...fns) {
        return x => fns.reduce((v, f) => f(v), x);
      }

      function sortJson(obj) {
        return pipe(
          getSortedObjAsArray,
          stringifyArrayOfKeyValue,
          wrapInCurlyBrackets,
        )(obj);
      }
  
  return pipe(getSortedJsonValue, JSON.parse);
});
