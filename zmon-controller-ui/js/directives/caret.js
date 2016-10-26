angular.module('zmon2App').directive('caret', ['$timeout', function($timeout) {

    function setCaretPosition(elem, caretPos) {
        if (elem !== null) {
            if (elem.createTextRange) {
                var range = elem.createTextRange();
                range.move('character', caretPos);
                range.select();
            } else {
                if (elem.setSelectionRange) {
                    elem.focus();
                    elem.setSelectionRange(caretPos, caretPos);
                } else
                    elem.focus();
            }
        }
    }

    return {
        scope: {value: '=ngModel'},
        link: function(scope, element, attrs) {
            scope.$watch('value', function(newValue, oldValue) {
                $timeout(function() {
                    if (newValue && newValue != oldValue && !isNaN(newValue)) {
                        setCaretPosition(element[0], newValue.length + 1);
                    }
                });
            });
        }
    };
}]);
