define([
  '../core_module',
  'app/core/utils/kbn',
  'app/core/utils/rangeutil',
],
function (coreModule, kbn, rangeUtil) {
  'use strict';

  coreModule.default.directive('ngModelOnblur', function() {
    return {
      restrict: 'A',
      priority: 1,
      require: 'ngModel',
      link: function(scope, elm, attr, ngModelCtrl) {
        if (attr.type === 'radio' || attr.type === 'checkbox') {
          return;
        }

        elm.off('input keydown change');
        elm.bind('blur', function() {
          scope.$apply(function() {
            ngModelCtrl.$setViewValue(elm.val());
          });
        });
      }
    };
  });

  coreModule.default.directive('emptyToNull', function () {
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function (scope, elm, attrs, ctrl) {
        ctrl.$parsers.push(function (viewValue) {
          if(viewValue === "") { return null; }
          return viewValue;
        });
      }
    };
  });

  coreModule.default.directive('validTimeSpan', function() {
    return {
      require: 'ngModel',
      link: function(scope, elm, attrs, ctrl) {
        ctrl.$validators.integer = function(modelValue, viewValue) {
          if (ctrl.$isEmpty(modelValue)) {
            return true;
          }
          if (viewValue.indexOf('$') === 0) {
            return true; // allow template variable
          }
          var info = rangeUtil.describeTextRange(viewValue);
          return info.invalid !== true;
        };
      }
    };
  });
});
