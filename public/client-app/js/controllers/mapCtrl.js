angular.module('travel.controllers')

.controller('MapCtrl', function ($scope, $window, Users, leafletEvents, $state, Locations, toaster, LocalStorageKeys) {


    $scope.message = "";
    $scope.markers = {};
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


    $scope.setCenter = function(lat, lon){
        var currentZoomLevel = $window.localStorage[LocalStorageKeys.CURRENT_ZOOM_LEVEL];

        if(currentZoomLevel == null)
            currentZoomLevel = 15;
        else
            currentZoomLevel = parseInt(currentZoomLevel);

        $scope.center = {
            lat: lat,
            lng: lon,
            zoom: currentZoomLevel
        };
    };

    $scope.setCenter(59.7475, 10.3936); //Set to Røyken for now

    $scope.defaults = {
        minZoom: 8,
        attributionControl: false
    };

    $scope.events = {
        map: {
            enable: ['zoomend'],
            logic: "emit"
        }
    };

    $scope.$on("leafletDirectiveMap.zoomend", function(event, args) {
        $window.localStorage[LocalStorageKeys.CURRENT_ZOOM_LEVEL] = args.leafletEvent.target._zoom;
    });

    $scope.$on('leafletDirectiveMarker.click' , function(event, args){
        if(args.markerName !== "self"){
            $state.go('app.view-profile', {userId: args.markerName})
        }
    });
    $scope.setPosition = function(){
        $window.navigator.geolocation.getCurrentPosition(
            function(position){
                Locations.setCurrent(position.coords.latitude, position.coords.longitude)
                    .success(function(){
                        Users.all().success(function(users){
                            //var RADIUS = 0.1;
                            var latitude = position.coords.latitude;
                            var longitude = position.coords.longitude;
                            $scope.setCenter(latitude, longitude);
                            $scope.markers = createMarkers(users, latitude, longitude);
                            $scope.message = "";
                            //$scope.maxbounds = leafletBoundsHelpers.createBoundsFromArray([
                            //    [ location.latitude - RADIUS, location.longitude - RADIUS ],
                            //    [ location.latitude + RADIUS, location.longitude + RADIUS ]
                            //]);
                            toaster.pop("success", "", "Location updated", 2500);
                        });

                    });
            },
            function(err){
                console.log(err);
                toaster.pop("error", "", "Unable to get your location");
            }
        );
    };

    Locations.getCurrent().success(function(location){
        if(location.latitude != null && location.longitude != null){
            $scope.setCenter(location.latitude, location.longitude);
            Users.all().success(function(users){
                $scope.markers = createMarkers(users, location.latitude, location.longitude);
            });
            //var RADIUS = 0.1;
            //$scope.maxbounds = leafletBoundsHelpers.createBoundsFromArray([
            //    [ location.latitude - RADIUS, location.longitude - RADIUS ],
            //    [ location.latitude + RADIUS, location.longitude + RADIUS ]
            //]);
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
                    iconUrl: "images/small/" + user.profilePicture.id,
                    iconSize:     [50, 50],
                    iconAnchor:   [25, 25],
                    className: "user-marker-" + user.gender
                },
                enable: ['leafletDirectiveMarker.click']
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