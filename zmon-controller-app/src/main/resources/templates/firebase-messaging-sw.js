// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here, other Firebase libraries
// are not available in the service worker.
importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-messaging.js');

// Initialize the Firebase app in the service worker by passing in the
// messagingSenderId.
firebase.initializeApp({
    'messagingSenderId': '[[${firebaseSenderId}]]'
});

// Retrieve an instance of Firebase Messaging so that it can handle background
// messages.
const messaging = firebase.messaging();

messaging.setBackgroundMessageHandler(function(payload) {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);
    // Customize notification here

    /*
    if (payload.data.type == "alert_start") {
        icon = "warning.png";
    }
    else if(payload.data.type == "alert_end") {
        icon = "clean.png";
    }
    else {
        icon = "logo.png";
    }
    */

    const notificationTitle = payload.title;
    const notificationOptions = {
        body: payload.body,
        icon: icon
    };

    return self.registration.showNotification(notificationTitle,
        notificationOptions);
});