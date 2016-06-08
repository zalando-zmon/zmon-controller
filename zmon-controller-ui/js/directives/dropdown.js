angular.module('zmon2App').directive('dropdown', [ '$timeout', function($timeout) {
    return {
        restrict: 'E',
        templateUrl: 'templates/dropdown.html',
        scope: {
            label: '@label',
            defaultOption: '@defaultOption',
            availableOptions: '=options',
            selectedOption: '=selected',
            onSelection: '&onSelection'
        },
        link: function(scope, elem, attrs) {
            scope.filter = '';
            scope.open = false;
            scope.focusedElement = null;

            scope.onSelect = function(option) {
                option = option || '';
                scope.filter = '';
                scope.open = false;

                // timeout to let ui-bootstrap close dropdown properly
                $timeout(function() {
                    scope.onSelection({"option":option});
                });
            };

            scope.onInput = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                scope.open = true;
            };

            scope.toggle = function(open) {
                scope.open = open;

                // reset behaviour of ng-focus directive
                if (!open) {
                    scope.focusedElement = null;
                }
            };
        }
    };
}]);
