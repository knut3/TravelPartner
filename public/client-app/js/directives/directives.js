angular.module("leaflet-directives", [])

.directive('leafletMap', function () {
    return {
        restrict: 'E',
        replace: true,
        transclude: true,
        template: '<div id="map" ng-transclude>' +
                  '</div>',
        scope: {
            ratingValue: '='
        },
        controller: function ($scope) {

            $scope.markers = [];

            this.addMarker = function (coords) {
                $scope.markers.push("pIKK");
                alert("added");
            }
        },
        link: function (scope, elem, attrs) {
            scope.map = L.map('map').setView([55.67, 12.56], 13);
            L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>',
                maxZoom: 18,
                minZoom: 8
            }).addTo(scope.map);
            console.log(scope.markers.length);
            for (var marker in scope.markers) {

                marker.addTo(scope.map);
            }
            
        }
    }
})

.directive('marker', function () {
    return {
        require: "^leafletMap",
        restrict: 'E',
        scope: {
            ratingValue: '='
        },
        link: function (scope, elem, attrs, mapCtrl) {
            alert("marker");
            mapCtrl.addMarker([55.67, 12.56]);
            
            
        }
    }
});