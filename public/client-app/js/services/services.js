angular.module('travel.services', ['http-auth-interceptor', 'ezfb'])


.factory('AuthenticationService', function($rootScope, $http, authService, ezfb, $window, LocalStorageKeys) {
    var service = {

        authHeaderUpdateFromCache: function(){
            $http.defaults.headers.common.Authorization = 'Bearer ' + $window.localStorage['accessToken'];
        },

        login: function() {

            ezfb.login(function(res){
                var accessToken = res.authResponse.accessToken;
                $http.defaults.headers.common.Authorization = 'Bearer ' + accessToken;
                $window.localStorage[LocalStorageKeys.ACCESS_TOKEN] = accessToken;
                authService.loginConfirmed(accessToken, function(config) {
                    config.headers.Authorization = 'Bearer ' + accessToken;
                    return config;
                });
            }, {scope: 'user_photos'});
        },
        logout: function() {

            ezfb.logout();
            delete $http.defaults.headers.common.Authorization;
            $rootScope.$broadcast('event:auth-logout-complete');
        }
    };
    return service;
})
.factory('EventSourceService', function(Locations, $window, LocalStorageKeys, $rootScope) {

    var serverEvents = { NEW_MESSAGE: "new-message" };
    var serverEventsHandled = [ serverEvents.NEW_MESSAGE ];

    return {

        Events: serverEvents,

        setup: function(){
            var eventSource = new EventSource("/subscribe-events?accessToken="+$window.localStorage[LocalStorageKeys.ACCESS_TOKEN]);

            for(var i = 0; i < serverEventsHandled.length; i++)
            {
                var event = serverEventsHandled[i];
                eventSource.addEventListener(event, function(data){
                    $rootScope.$broadcast(data.type, JSON.parse(data.data));
                } , false);
            }
        }
    }
})
.factory('Users', function($http) {

  return {
    all: function() {
        return $http.get("users");
    },
    get: function (userId) {
        return $http.get("users/"+userId);
    }
  }
})

.factory('Locations', function($http) {

    return {
        setCurrent: function (lat, lng) {
            return $http.post("me/locations/current?lat="+lat+"&lng="+lng);
        },
        getCurrent: function () {
            return $http.get("me/locations/current");
        }
    }
})

.factory('LocalStorageKeys', function() {

    return {
        ACCESS_TOKEN: "accessToken"
    }
})

.factory('Dialogs', function() {
    // Might use a resource here that returns a JSON array

    // Some fake testing data
    var dialogs = [
      { id: 0, messages: [{ me: false, message: "Hi, friend!" }, { me: true, message: "Halla" }, { me: false, message: "But how the hell are you doing, bro?" }] },
      { id: 1, messages: [{ me: false, message: "Hi, friend!" }, { me: true, message: "Halla" }, { me: false, message: "But how the hell are you doing, bro?" }] },
      { id: 2, messages: [{ me: false, message: "Hi, friend!" }, { me: true, message: "Halla" }, { me: false, message: "But how the hell are you doing, bro?" }] },
      { id: 3, messages: [{ me: false, message: "Hi, friend!" }, { me: true, message: "Halla" }, { me: false, message: "But how the hell are you doing, bro?" }] }
    ];

    return {
        all: function() {
            return dialogs;
        },
        get: function (travelerId) {
            // Simple index lookup
            return dialogs[travelerId];
        }
    }
});
