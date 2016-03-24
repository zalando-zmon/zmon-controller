/**
 * Checks if passed data is valid JSON. Also if element with this directive additionally has attr "non-empty-json" then
 * it means that the inputs [], [{}] and {} are invalid
 */
angular.module('zmon2App').directive('json', function() {
    return {
        require: 'ngModel',
        scope: {
            nonEmptyIf: "=nonEmptyJsonIf",
            model: "=ngModel"
        },
        link: function(scope, elem, attrs, ctrl) {
            var setValidation = function() {
                var value;
                // Initially input is considered valid
                ctrl.$setValidity('valid-json', true);
                ctrl.$setValidity('non-empty-json', true);

                // First check: make sure it is valid JSON
                try {
                    // Check if it's a code editor.
                    if ($(elem).hasClass("ace_editor")) {
                        value = ace.edit(elem[0]).getSession().getValue();
                    } else {
                        value = elem.val();
                    }

                    // No need to parse if value is an empty String.
                    // Useful for template alerts.
                    if (value !== '') {

                        var normalizedJsonText = JSON.stringify(JSON.parse(value));

                        if ('nonEmptyJson' in attrs && scope.nonEmptyIf && (normalizedJsonText === '[]' || normalizedJsonText === '[{}]' || normalizedJsonText === '{}')) {
                            ctrl.$setValidity('non-empty-json', false);
                            return;
                        }
                    }
                } catch (e) {
                    ctrl.$setValidity('valid-json', false);
                    ctrl.$setValidity('non-empty-json', false);
                }
            };
            scope.$watch('model', setValidation);
            scope.$watch('nonEmptyIf', setValidation);
        }
    };
});
