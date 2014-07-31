angular.module('travel.services')

.factory('Users', function($http, Configuration) {

  return {
    all: function() {
        return $http.get(Configuration.BASE_URL + "users");
    },
    get: function (userId) {
        return $http.get(Configuration.BASE_URL + "users/"+userId);
    }
  }
});
