angular.module('travel.services')

.factory('LocalStorageKeys', function() {

    return {
        ACCESS_TOKEN: "accessToken",
        CURRENT_ZOOM_LEVEL: "currentZoomLevel"
    }
})

.factory('LocalEvents', function() {

    return {
        MESSAGES_READ: "messagesRead",
        MESSAGE_SENT: "messageSent"
    }
})

.factory('Configuration', function(){
    return {
        BASE_URL: "../../"
    }
});
