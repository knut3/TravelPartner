angular.module('travel.services')

.factory('Conversations', function($http, Configuration) {
    return {
        all: function() {
            return $http.get(Configuration.BASE_URL + "me/conversations");
        },
        get: function (id) {
            // Simple index lookup
            return $http.get(Configuration.BASE_URL + "me/conversations/" + id);
        },
        sendMessage: function(userId, message){
            return $http({
                url: Configuration.BASE_URL + "users/"+ userId + "/messages",
                method: "POST",
                headers: {
                    "Content-Type": "text/plain;charset=UTF-8"
                },
                data: message
            });
        },
        getUnreadMessageCount: function(){
            return $http.get(Configuration.BASE_URL + "me/messages/unread/count");
        }
    }
});
