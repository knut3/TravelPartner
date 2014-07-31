angular.module('travel.services')

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
});