angular.module('zmon2App').directive('notifications', [ 'CommunicationService', 'FeedbackMessageService', function(CommunicationService, FeedbackMessageService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/notifications.html',
        link: function(scope, elem, attr) {
            scope.supported = false;
            scope.denied = false;
            scope.subscriptions = {
                subscribed: false,
                alerts: [],
                teams: [],
                priority: 2
            };

            scope.availableAlerts = [];
            scope.availableTeams = [];
            scope.availablePriorities = [1, 2, 3];
            // template used by uib-popover
            scope.templateUrl = 'templates/notificationsSettings.html';

            const messaging = firebase.messaging();

            var requestPermission = function() {
                messaging.requestPermission()
                .then(function() {
                    console.log('Notification permission granted.');
                    scope.denied = false;

                    messaging.getToken().then(function(currentToken) {
                        console.log('Token to register:', currentToken);
                        scope.token = currentToken;
                        CommunicationService.sendNotificationToken(currentToken)
                        .then(function() {
                            console.log('Token successfully sent to server');
                            scope.subscriptions.subscribed = true;
                            getNotificationAlerts();
                            getNotificationTeams();
                            getNotificationPriority();
                        })
                        .catch(function() {
                            FeedbackMessageService.showErrorMessage('Push Notifications subscription failed');
                            scope.subscriptions.subscribed = false;
                        });
                    }).catch(function() {
                        FeedbackMessageService.showErrorMessage('Push Notifications subscription failed');
                        scope.subscriptions.subscribed = false;
                    });

                    messaging.onMessage(function(payload) {
                        notification = new Notification(payload.notification.title,
                                                        {icon: payload.notification.icon, body: payload.notification.body});
                        notification.onclick = function(event) {
                            event.preventDefault(); // prevent the browser from focusing the Notification's tab
                            window.open(payload.notification.click_action, '_blank');
                        }
                    });

                })
                .catch(function(err) {
                    console.log('Unable to get permission to notify.', err);
                    scope.denied = true;
                });
            };

            var disableNotifications = function() {
                messaging.deleteToken(scope.token).then(function() {
                    CommunicationService.removeNotificationToken(scope.token).then(function() {
                        console.log('Push Notifications successfully disabled');
                        scope.subscriptions.subscribed = false;
                    });
                }).catch(function() {
                    FeedbackMessageService.showErrorMessage('Failed to disable Push Notifications');
                    scope.subscriptions.subscribed = false;
                });
            };

            var getNotificationAlerts = function() {
                CommunicationService.getNotificationAlerts()
                .then(function(alerts) {
                    scope.subscriptions.alerts = alerts.map(String);
                })
            };

            var getNotificationTeams = function() {
                CommunicationService.getNotificationTeams()
                .then(function(teams) {
                    scope.subscriptions.teams = teams;
                })
            };

            var getNotificationPriority = function() { 
                CommunicationService.getNotificationPriority()
                .then(function(prio) {
                    scope.subscriptions.priority = prio;
                })
            };

            var checkIfSupported = function() {
                if ('serviceWorker' in navigator && 'PushManager' in window) {
                    console.log('Service Worker and Push is supported');
                    scope.supported = true;
                }
            };

            var getAllTeams = function() {
                CommunicationService.getAllTeams()
                .then(function(teams) {
                    scope.availableTeams = teams;
                });
            };

            scope.addAlert = function(id) {
                CommunicationService.subscribeNotificationAlert(id)
                .then(function() {
                    console.log('alert saved');
                });
            };

            scope.removeAlert = function(id) {
                CommunicationService.removeNotificationAlert(id)
                .then(function() {
                    console.log('alert removed');
                });
            };

            scope.addTeam = function(team) {
                CommunicationService.subscribeNotificationTeam(team)
                .then(function() {
                    console.log('team added');
                });
            };

            scope.removeTeam = function(team) {
                CommunicationService.removeNotificationTeam(team)
                .then(function() {
                    console.log('team removed');
                });
            };

            scope.setPriority = function(prio) {
                CommunicationService.subscribeNotificationPriority(prio)
                .then(function() {
                    console.log('priority set');
                });
            };

            scope.updateStatus = function(status) {
                if (status) {
                    return requestPermission();
                }
                disableNotifications();
            };

            // initialize
            checkIfSupported();
            getAllTeams();
            requestPermission();
        }
    };
}]);
