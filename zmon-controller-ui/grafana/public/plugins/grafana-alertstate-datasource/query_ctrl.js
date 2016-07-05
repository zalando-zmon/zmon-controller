define([
  'angular',
  'lodash',
  'app/plugins/sdk'
],
function (angular, _, sdk) {
  'use strict';

  var AlertStateQueryCtrl = (function(_super) {
    var self;

    function AlertStateQueryCtrl($scope, $injector) {
      _super.call(this, $scope, $injector);

      this.panel.stack = false;
      this.target.errors = validateTarget(this.target);
      self = this;
    }

    AlertStateQueryCtrl.prototype = Object.create(_super.prototype);
    AlertStateQueryCtrl.prototype.constructor = AlertStateQueryCtrl;

    AlertStateQueryCtrl.templateUrl = 'partials/query.editor.html';

    AlertStateQueryCtrl.prototype.targetBlur = function() {
      this.target.errors = validateTarget(this.target);
      if (!_.isEqual(this.oldTarget, this.target) && _.isEmpty(this.target.errors)) {
        this.oldTarget = angular.copy(this.target);
        this.panelCtrl.refresh();
      }
    };

    // Validation
    function validateTarget(target) {
      var errs = {};

      if (!target.alert) {
        errs.alert = "You must supply an alert ID.";
      }

      return errs;
    }

    return AlertStateQueryCtrl;

  })(sdk.QueryCtrl);

  return AlertStateQueryCtrl;
});
