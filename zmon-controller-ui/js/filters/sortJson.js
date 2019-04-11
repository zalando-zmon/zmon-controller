angular.module('zmon2App')
  .filter('jsonSort', function () {

    const isPlainObject = function (obj) {
      return Object.prototype.toString.call(obj) === '[object Object]';
    };

    function defaultSortFn(a, b) {
      return a.localeCompare(b);
    }

    function sort(src) {
      var out;

      if (Array.isArray(src)) {
        return src.map(function (item) {
          return sort(item);
        });
      }

      if (isPlainObject(src)) {
        out = {};

        Object.keys(src).sort(defaultSortFn).forEach(function (key) {
          out[key] = sort(src[key]);
        });

        return out;
      }

      return src;
    }
    
    return sort;
  })
