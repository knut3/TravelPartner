angular.module('travel.controllers')

.controller('MapCtrl', function ($scope, $window, Users, leafletEvents, $state, Locations, toaster, leafletBoundsHelpers) {


    $scope.message = "";
    $scope.markers = {};
    $scope.maxbounds = {};
    $scope.layers = {
        baselayers: {
            osm: {
                name: 'OpenStreetMap',
                type: 'xyz',
                url: 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                layerOptions: {
                    subdomains: ['a', 'b', 'c'],
                    attribution: '© OpenStreetMap contributors',
                    continuousWorld: true
                }
            }
        },
        overlays: {
            users: {
                name: "Users",
                type: "markercluster",
                visible: true
            },
            self: {
                name: "Self",
                type: "group",
                visible: true
            }

        }
    }
    $scope.events = {
        markers: {
            enable: ['leafletDirectiveMarker.click']
        }
    };
    $scope.center = {
        lat: 59.7475,
        lng: 10.3936,
        zoom: 12
    };
    $scope.defaults = {
        minZoom: 8,
        keyboard: false,
        doubleClickZoom: false,
        attributionControl: false
    };
    $scope.$on('leafletDirectiveMarker.click' , function(event, args){
        if(args.markerName === "self")
            $state.go("app.account");
        else {
            $state.go('app.view-profile', {userId: args.markerName})
        }
    });
    $scope.setPosition = function(){
        $window.navigator.geolocation.getCurrentPosition(
            function(position){
                Locations.setCurrent(position.coords.latitude, position.coords.longitude)
                    .success(function(){
                        Users.all().success(function(users){
                            var RADIUS = 0.1;
                            var latitude = position.coords.latitude;
                            var longitude = position.coords.longitude;
                            $scope.center = {lat: latitude, lon: longitude};
                            $scope.markers = createMarkers(users, latitude, longitude);
                            $scope.message = "";
                            $scope.maxbounds = leafletBoundsHelpers.createBoundsFromArray([
                                [ location.latitude - RADIUS, location.longitude - RADIUS ],
                                [ location.latitude + RADIUS, location.longitude + RADIUS ]
                            ]);
                            toaster.pop("success", "", "Location updated", 2500);
                        });

                    });
            },
            function(){
                toaster.pop("error", "", "Unable to get your location");
            }
        );
    };

    Locations.getCurrent().success(function(location){
        if(location.latitude != null && location.longitude != null){
            $scope.center.lat = location.latitude;
            $scope.center.lng = location.longitude;
            Users.all().success(function(users){
                $scope.markers = createMarkers(users, location.latitude, location.longitude);
            });
            var RADIUS = 0.1;
            $scope.maxbounds = leafletBoundsHelpers.createBoundsFromArray([
                [ location.latitude - RADIUS, location.longitude - RADIUS ],
                [ location.latitude + RADIUS, location.longitude + RADIUS ]
            ]);
        }
        else{
            $scope.message = "Update your location to see travelers near you";
        }
    });

    function createMarkers(users, selfLatitude, selfLongitude){
        var markers = {};
        for (var i = 0; i < users.length; i++) {
            var user = users[i];
            var marker = {
                layer: "users",
                lat: user.latitude,
                lng: user.longitude,
                icon: {
                    iconUrl: "assets/images/small/" + user.profilePicture.id + ".jpg",
                    iconSize:     [50, 50],
                    iconAnchor:   [25, 25],
                    className: "user-marker-" + user.gender
                }
            }
            markers[user.id] = marker;
        }

        // add marker of self
        markers["self"] = {
            layer: "self",
            lat: selfLatitude,
            lng: selfLongitude,
            icon: {
                type: 'div',
                iconSize: [15, 15],
                className: 'self-marker'
            }
        };

        return markers;
    }
})