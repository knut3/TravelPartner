angular.module('travel.services', ['http-auth-interceptor', 'ezfb'])


.factory('AuthenticationService', function($rootScope, $http, authService, ezfb, $window) {
    var service = {

        authHeaderUpdateFromCache: function(){
            $http.defaults.headers.common.Authorization = 'Bearer ' + $window.localStorage['accessToken'];
        },

        login: function() {

            ezfb.login(function(res){
                var accessToken = res.authResponse.accessToken;
                $http.defaults.headers.common.Authorization = 'Bearer ' + accessToken;
                $window.localStorage['accessToken'] = accessToken;
                authService.loginConfirmed(accessToken, function(config) {
                    config.headers.Authorization = 'Bearer ' + accessToken;
                    return config;
                });
            }, {scope: 'email,user_likes'});
        },
        logout: function() {

            ezfb.logout();
            delete $http.defaults.headers.common.Authorization;
            $rootScope.$broadcast('event:auth-logout-complete');
        }
    };
    return service;
})
.factory('Travelers', function($http) {

  return {
    all: function() {
        return $http.get("users");
    },
    get: function (travelerId) {
        return $http.get("users/"+travelerId);
    }
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
