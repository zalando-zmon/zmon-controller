///<reference path="../../headers/common.d.ts" />
System.register(['../../core/core_module'], function(exports_1) {
    var core_module_1;
    var OrgUsersCtrl;
    return {
        setters:[
            function (core_module_1_1) {
                core_module_1 = core_module_1_1;
            }],
        execute: function() {
            OrgUsersCtrl = (function () {
                /** @ngInject */
                function OrgUsersCtrl($scope, $http, backendSrv) {
                    this.$scope = $scope;
                    this.$http = $http;
                    this.backendSrv = backendSrv;
                    this.user = {
                        loginOrEmail: '',
                        role: 'Viewer',
                    };
                    this.get();
                    this.editor = { index: 0 };
                }
                OrgUsersCtrl.prototype.get = function () {
                    var _this = this;
                    this.backendSrv.get('/api/org/users')
                        .then(function (users) {
                        _this.users = users;
                    });
                    this.backendSrv.get('/api/org/invites')
                        .then(function (pendingInvites) {
                        _this.pendingInvites = pendingInvites;
                    });
                };
                OrgUsersCtrl.prototype.updateOrgUser = function (user) {
                    this.backendSrv.patch('/api/org/users/' + user.userId, user);
                };
                OrgUsersCtrl.prototype.removeUser = function (user) {
                    var _this = this;
                    this.$scope.appEvent('confirm-modal', {
                        title: 'Delete',
                        text: 'Are you sure you want to delete user ' + user.login + '?',
                        yesText: "Delete",
                        icon: "fa-warning",
                        onConfirm: function () {
                            _this.removeUserConfirmed(user);
                        }
                    });
                };
                OrgUsersCtrl.prototype.removeUserConfirmed = function (user) {
                    var _this = this;
                    this.backendSrv.delete('/api/org/users/' + user.userId)
                        .then(function () {
                        _this.get();
                    });
                };
                OrgUsersCtrl.prototype.revokeInvite = function (invite, evt) {
                    var _this = this;
                    evt.stopPropagation();
                    this.backendSrv.patch('/api/org/invites/' + invite.code + '/revoke')
                        .then(function () {
                        _this.get();
                    });
                };
                OrgUsersCtrl.prototype.copyInviteToClipboard = function (evt) {
                    evt.stopPropagation();
                };
                OrgUsersCtrl.prototype.openInviteModal = function () {
                    var modalScope = this.$scope.$new();
                    modalScope.invitesSent = function () {
                        this.get();
                    };
                    this.$scope.appEvent('show-modal', {
                        src: 'public/app/features/org/partials/invite.html',
                        modalClass: 'invite-modal',
                        scope: modalScope
                    });
                };
                return OrgUsersCtrl;
            })();
            exports_1("OrgUsersCtrl", OrgUsersCtrl);
            core_module_1.default.controller('OrgUsersCtrl', OrgUsersCtrl);
        }
    }
});
//# sourceMappingURL=org_users_ctrl.js.map