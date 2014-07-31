angular.module('travel.services')

.factory('Locations', function($http, Configuration) {

    return {
        setCurrent: function (lat, lng) {
            return $http.post(Configuration.BASE_URL + "me/locations/current?lat="+lat+"&lng="+lng);
        },
        getCurrent: function () {
            return $http.get(Configuration.BASE_URL + "me/locations/current");
        }
    }
});
