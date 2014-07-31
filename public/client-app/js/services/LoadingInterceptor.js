angular.module('travel.services')

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
});