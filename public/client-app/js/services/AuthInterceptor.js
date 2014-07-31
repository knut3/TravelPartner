angular.module('travel.services')

.factory('AuthInterceptor',function($window, LocalStorageKeys){
    return {
        response: function(response){
            var authHeader = response.headers("Authorization");
            if(authHeader != null)
                $window.localStorage[LocalStorageKeys.ACCESS_TOKEN] = authHeader;
            return response;
        }
    }
});
