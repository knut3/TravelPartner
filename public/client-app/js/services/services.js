angular.module('travel.services', ['http-auth-interceptor', 'ezfb'])


.factory('AuthenticationService', function($rootScope, $http, $q, authService, ezfb, $window, LocalStorageKeys, Configuration) {
    var service = {

        authHeaderUpdateFromCache: function(){
            $http.defaults.headers.common.Authorization = 'Bearer ' + $window.localStorage['accessToken'];
        },

        verifyAccessToken: function(){
            var deferred = $q.defer();
            var currentAccessToken = $window.localStorage[LocalStorageKeys.ACCESS_TOKEN];
            if(currentAccessToken == null) {
                this.login().then(function(){
                    deferred.resolve();
                })
            }
            else deferred.resolve();
            return deferred.promise;

        },

        login: function() {
            var deffered = $q.defer();
            ezfb.login(function(res){
                var accessToken = res.authResponse.accessToken;
                $http.defaults.headers.common.Authorization = 'Bearer ' + accessToken;
                $window.localStorage[LocalStorageKeys.ACCESS_TOKEN] = accessToken;
                authService.loginConfirmed(accessToken, function(config) {
                    config.headers.Authorization = 'Bearer ' + accessToken;
                    deffered.resolve();
                    return config;
                });
            }, {scope: 'user_photos'});
            return deffered.promise;
        },
        logout: function() {

            ezfb.logout();
            delete $http.defaults.headers.common.Authorization;
            $rootScope.$broadcast('event:auth-logout-complete');
        }
    };
    return service;
})
.factory('EventSourceService', function(Locations, $window, LocalStorageKeys, $rootScope, toaster, Configuration) {

    var serverEvents = { NEW_MESSAGE: "new-message" };
    var serverEventsHandled = [ serverEvents.NEW_MESSAGE ];

    return {

        Events: serverEvents,

        setup: function(){
            if(typeof(EventSource) === "undefined"){
                toaster.pop("error", "", "Server-sent events are not supported by your browser. " +
                    "It is highly suggested that you change to a browser that supports it, i.e. Firefox or Chrome", 5000);
                return;
            }
            var eventSource = new EventSource(Configuration.BASE_URL + "subscribe-events?accessToken="+$window.localStorage[LocalStorageKeys.ACCESS_TOKEN]);

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
.factory('Users', function($http, Configuration) {

  return {
    all: function() {
        return $http.get(Configuration.BASE_URL + "users");
    },
    get: function (userId) {
        return $http.get(Configuration.BASE_URL + "users/"+userId);
    }
  }
})

.factory('Locations', function($http, Configuration) {

    return {
        setCurrent: function (lat, lng) {
            return $http.post(Configuration.BASE_URL + "me/locations/current?lat="+lat+"&lng="+lng);
        },
        getCurrent: function () {
            return $http.get(Configuration.BASE_URL + "me/locations/current");
        }
    }
})

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
})
.factory('HttpErrorInterceptor',function($q, toaster){
    return {
        responseError: function(response){
            if (response.status == 403){
                toaster.pop("error", "", response.data);
            }
            return $q.reject(response);
        }
    }
})
.factory('LoadingInterceptor',function($injector){
    return {
        request: function(config){
            $injector.get("$ionicLoading").show({
                template: '<i class="loader icon ion-loading-c"></i>'
            });
            return config;
        },

        response: function(response){
            $injector.get("$ionicLoading").hide();
            return response;
        }
    }
})
.factory('AuthInterceptor',function($window, LocalStorageKeys){
    return {
        response: function(response){
            var authHeader = response.headers("Authorization");
            if(authHeader != null)
                $window.localStorage[LocalStorageKeys.ACCESS_TOKEN] = authHeader;
            return response;
        }
    }
})
.factory('Configuration', function(){
    return {
        BASE_URL: ""
    }
});
