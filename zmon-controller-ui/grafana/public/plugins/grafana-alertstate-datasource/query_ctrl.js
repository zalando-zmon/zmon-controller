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

    // Filter metric by entity
    AlertStateQueryCtrl.prototype.addEntity = function() {
      if (!this.panel.addEntityMode) {
        this.panel.addEntityMode = true;
        this.validateEntity();
        return;
      }

      if (!this.target.entities) {
        this.target.entities = [];
      }

      this.validateEntity();
      if (!this.target.errors.entities) {
        this.target.entities.push(this.target.currentEntity);
        this.target.currentEntity = '';
        this.targetBlur();
      }

      this.panel.addEntityMode = false;
    };

    AlertStateQueryCtrl.prototype.removeEntity = function(key) {
      this.target.entities = _.without(this.target.entities, key);
      if (_.size(this.target.entities) === 0) {
        this.target.entities = null;
      }
      this.targetBlur();
    };

    AlertStateQueryCtrl.prototype.validateEntity = function() {
      this.target.errors.entities = null;
      if (!this.target.currentEntity) {
        this.target.errors.entities = "You must specify an entity ID.";
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
