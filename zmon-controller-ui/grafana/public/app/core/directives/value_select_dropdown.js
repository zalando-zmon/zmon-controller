define([
  'angular',
  'lodash',
  '../core_module',
],
function (angular, _, coreModule) {
  'use strict';

  coreModule.default.controller('ValueSelectDropdownCtrl', function($q) {
    var vm = this;

    vm.show = function() {
      vm.oldVariableText = vm.variable.current.text;
      vm.highlightIndex = -1;

      vm.options = vm.variable.options;
      vm.selectedValues = _.filter(vm.options, {selected: true});

      vm.tags = _.map(vm.variable.tags, function(value) {
        var tag = { text: value, selected: false };
        _.each(vm.variable.current.tags, function(tagObj) {
          if (tagObj.text === value) {
            tag = tagObj;
          }
        });
        return tag;
      });

      vm.search = {
        query: '',
        options: vm.options.slice(0, Math.min(vm.options.length, 1000))
      };

      vm.dropdownVisible = true;
    };

    vm.updateLinkText = function() {
      var current = vm.variable.current;

      if (current.tags && current.tags.length) {
        // filer out values that are in selected tags
        var selectedAndNotInTag = _.filter(vm.variable.options, function(option) {
          if (!option.selected) { return false; }
          for (var i = 0; i < current.tags.length; i++)  {
            var tag = current.tags[i];
            if (_.indexOf(tag.values, option.value) !== -1) {
              return false;
            }
          }
          return true;
        });

        // convert values to text
        var currentTexts = _.pluck(selectedAndNotInTag, 'text');

        // join texts
        vm.linkText = currentTexts.join(' + ');
        if (vm.linkText.length > 0) {
          vm.linkText += ' + ';
        }
      } else {
        vm.linkText = vm.variable.current.text;
      }
    };

    vm.clearSelections = function() {
      _.each(vm.options, function(option) {
        option.selected = false;
      });

      vm.selectionsChanged(false);
    };

    vm.selectTag = function(tag) {
      tag.selected = !tag.selected;
      var tagValuesPromise;
      if (!tag.values) {
        tagValuesPromise = vm.getValuesForTag({tagKey: tag.text});
      } else {
        tagValuesPromise = $q.when(tag.values);
      }

      tagValuesPromise.then(function(values) {
        tag.values = values;
        tag.valuesText = values.join(' + ');
        _.each(vm.options, function(option) {
          if (_.indexOf(tag.values, option.value) !== -1) {
            option.selected = tag.selected;
          }
        });

        vm.selectionsChanged(false);
      });
    };

    vm.keyDown = function (evt) {
      if (evt.keyCode === 27) {
        vm.hide();
      }
      if (evt.keyCode === 40) {
        vm.moveHighlight(1);
      }
      if (evt.keyCode === 38) {
        vm.moveHighlight(-1);
      }
      if (evt.keyCode === 13) {
        if (vm.search.options.length === 0) {
          vm.commitChanges();
        } else {
          vm.selectValue(vm.search.options[vm.highlightIndex], {}, true, false);
        }
      }
      if (evt.keyCode === 32) {
        vm.selectValue(vm.search.options[vm.highlightIndex], {}, false, false);
      }
    };

    vm.moveHighlight = function(direction) {
      vm.highlightIndex = (vm.highlightIndex + direction) % vm.search.options.length;
    };

    vm.selectValue = function(option, event, commitChange, excludeOthers) {
      if (!option) { return; }

      option.selected = !option.selected;

      commitChange = commitChange || false;
      excludeOthers = excludeOthers || false;

      var setAllExceptCurrentTo = function(newValue) {
        _.each(vm.options, function(other) {
          if (option !== other) { other.selected = newValue; }
        });
      };

      // commit action (enter key), should not deselect it
      if (commitChange) {
        option.selected = true;
      }

      if (option.text === 'All' || excludeOthers) {
        setAllExceptCurrentTo(false);
        commitChange = true;
      }
      else if (!vm.variable.multi) {
        setAllExceptCurrentTo(false);
        commitChange = true;
      } else if (event.ctrlKey || event.metaKey || event.shiftKey) {
        commitChange = true;
        setAllExceptCurrentTo(false);
      }

      vm.selectionsChanged(commitChange);
    };

    vm.selectionsChanged = function(commitChange) {
      vm.selectedValues = _.filter(vm.options, {selected: true});

      if (vm.selectedValues.length > 1) {
        if (vm.selectedValues[0].text === 'All') {
          vm.selectedValues[0].selected = false;
          vm.selectedValues = vm.selectedValues.slice(1, vm.selectedValues.length);
        }
      }

      // validate selected tags
      _.each(vm.tags, function(tag) {
        if (tag.selected)  {
          _.each(tag.values, function(value) {
            if (!_.findWhere(vm.selectedValues, {value: value})) {
              tag.selected = false;
            }
          });
        }
      });

      vm.selectedTags = _.filter(vm.tags, {selected: true});
      vm.variable.current.value = _.pluck(vm.selectedValues, 'value');
      vm.variable.current.text = _.pluck(vm.selectedValues, 'text').join(' + ');
      vm.variable.current.tags = vm.selectedTags;

      if (!vm.variable.multi) {
        vm.variable.current.value = vm.selectedValues[0].value;
      }

      if (commitChange) {
        vm.commitChanges();
      }
    };

    vm.commitChanges = function() {
      // if we have a search query and no options use that
      if (vm.search.options.length === 0 && vm.search.query.length > 0) {
        vm.variable.current = {text: vm.search.query, value: vm.search.query};
      }
      else if (vm.selectedValues.length === 0) {
        // make sure one option is selected
        vm.options[0].selected = true;
        vm.selectionsChanged(false);
      }

      vm.dropdownVisible = false;
      vm.updateLinkText();

      if (vm.variable.current.text !== vm.oldVariableText) {
        vm.onUpdated();
      }
    };

    vm.queryChanged = function() {
      vm.highlightIndex = -1;
      vm.search.options = _.filter(vm.options, function(option) {
        return option.text.toLowerCase().indexOf(vm.search.query.toLowerCase()) !== -1;
      });

      vm.search.options = vm.search.options.slice(0, Math.min(vm.search.options.length, 1000));
    };

    vm.init = function() {
      vm.selectedTags = vm.variable.current.tags || [];
      vm.updateLinkText();
    };

  });

  coreModule.default.directive('valueSelectDropdown', function($compile, $window, $timeout, $rootScope) {
    return {
      scope: { variable: "=", onUpdated: "&", getValuesForTag: "&" },
      templateUrl: 'public/app/partials/valueSelectDropdown.html',
      controller: 'ValueSelectDropdownCtrl',
      controllerAs: 'vm',
      bindToController: true,
      link: function(scope, elem) {
        var bodyEl = angular.element($window.document.body);
        var linkEl = elem.find('.variable-value-link');
        var inputEl = elem.find('input');

        function openDropdown() {
          inputEl.css('width', Math.max(linkEl.width(), 30) + 'px');

          inputEl.show();
          linkEl.hide();

          inputEl.focus();
          $timeout(function() { bodyEl.on('click', bodyOnClick); }, 0, false);
        }

        function switchToLink() {
          inputEl.hide();
          linkEl.show();
          bodyEl.off('click', bodyOnClick);
        }

        function bodyOnClick (e) {
          if (elem.has(e.target).length === 0) {
            scope.$apply(function() {
              scope.vm.commitChanges();
            });
          }
        }

        scope.$watch('vm.dropdownVisible', function(newValue) {
          if (newValue) {
            openDropdown();
          } else {
            switchToLink();
          }
        });

        var cleanUp = $rootScope.$on('template-variable-value-updated', function() {
          scope.vm.updateLinkText();
        });

        scope.$on("$destroy", function() {
          cleanUp();
        });

        scope.vm.init();
      },
    };
  });

});
