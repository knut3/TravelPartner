angular.module('travel.services')

.factory('HttpErrorInterceptor',function($q, toaster){
    return {
        responseError: function(response){
            if (response.status == 403){
                toaster.pop("error", "", response.data);
            }
            return $q.reject(response);
        }
    }
});