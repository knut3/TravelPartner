angular.module('travel.controllers')

.controller('MapCtrl', function ($scope, $window, Users, leafletEvents, $state, Locations, toaster, LocalStorageKeys, Configuration, $cordovaGeolocation) {

    $scope.showFriends = true;
    $scope.showStrangers = true;
    $scope.showSelf = true;
    $scope.buttonMessage = "Update my position";
    $scope.map = {};
    $scope.map.markers = {};
    $scope.map.defaults = {
        minZoom: 8,
        zoomControl: true,
        attributionControl: false
    };

    $scope.map.events = {
        map: {
            enable: ['zoomend'],
            logic: "emit"
        }
    };
    $scope.map.layers = {
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
            self: {
                name: "Self",
                type: "group",
                visible: true
            },
            users: {
                name: "Users",
                type: "markercluster",
                visible: true,
                layerOptions: {
                    showCoverageOnHover: false,
                    spiderfyDistanceMultiplier: 2
                }
            },
            friends: {
                name: "Friends",
                type: "markercluster",
                visible: true,
                layerOptions: {
                    showCoverageOnHover: false,
                    spiderfyDistanceMultiplier: 2
                }
            }

        }
    }

    $scope.filterMarkers = function(){
        if($scope.showFriends && $scope.showStrangers) {
            var markers = {};
            $scope.map.markers = angular.extend(markers, $scope.friends, $scope.strangers);
        }

        else if($scope.showFriends)
            $scope.map.markers = $scope.friends;

        else if($scope.showStrangers)
            $scope.map.markers = $scope.strangers;

        else $scope.map.markers = {};

        if($scope.showSelf)
            $scope.map.markers["self"] = $scope.self;

    }

    $scope.setCenter = function(lat, lon){
        var currentZoomLevel = $window.localStorage[LocalStorageKeys.CURRENT_ZOOM_LEVEL];

        if(currentZoomLevel == null)
            currentZoomLevel = 15;
        else
            currentZoomLevel = parseInt(currentZoomLevel);

        $scope.map.center = {
            lat: lat,
            lng: lon,
            zoom: currentZoomLevel
        };
    };

    $scope.setCenter(59.7475, 10.3936); //Set to Røyken for now



    $scope.$on("leafletDirectiveMap.zoomend", function(event, args) {
        $window.localStorage[LocalStorageKeys.CURRENT_ZOOM_LEVEL] = args.leafletEvent.target._zoom;
    });

    $scope.$on('leafletDirectiveMarker.click' , function(event, args){
        if(args.markerName !== "self"){
            //$state.go('app.view-profile', {userId: args.markerName})
        }
    });
    $scope.setPosition = function(){
        $cordovaGeolocation.getCurrentPosition()
            .then(function(position) {
                Locations.setCurrent(position.coords.latitude, position.coords.longitude)
                    .success(function(){
                        Users.all().success(function(users){
                            //var RADIUS = 0.1;
                            var latitude = position.coords.latitude;
                            var longitude = position.coords.longitude;
                            $scope.setCenter(latitude, longitude);
                            $scope.map.markers = createMarkers($scope, users, latitude, longitude, $scope.showSelf, $scope.showFriends, $scope.showStrangers);
                            //$scope.maxbounds = leafletBoundsHelpers.createBoundsFromArray([
                            //    [ location.latitude - RADIUS, location.longitude - RADIUS ],
                            //    [ location.latitude + RADIUS, location.longitude + RADIUS ]
                            //]);
                            $scope.buttonMessage = "Update my position";
                            toaster.pop("success", "", "Location updated", 2500);
                        });

                    });
            },
            function(err){
                toaster.pop("error", "", "Unable to get your location");
            }
        );
    };

    Locations.getCurrent().success(function(location){
        if(location != null && location !== ""){
            $scope.setCenter(location.latitude, location.longitude);
            Users.all().success(function(users){
                $scope.map.markers = createMarkers($scope, users, location.latitude, location.longitude, $scope.showSelf, $scope.showFriends, $scope.showStrangers);
            });
            //var RADIUS = 0.1;
            //$scope.maxbounds = leafletBoundsHelpers.createBoundsFromArray([
            //    [ location.latitude - RADIUS, location.longitude - RADIUS ],
            //    [ location.latitude + RADIUS, location.longitude + RADIUS ]
            //]);
        }
        else{
            $scope.buttonMessage = "Update your location to see travelers near you";
        }
    });

    function createMarkers($scope, users, selfLatitude, selfLongitude, showSelf, showFriends, showStrangers){
        var markers = {};
        $scope.friends = {};
        $scope.strangers = {};

        for (var i = 0; i < users.length; i++) {
            var user = users[i];
            var marker = {
                layer: "users",
                lat: user.latitude,
                lng: user.longitude,
                draggable: false,
                message: "<div class='list card'>" +
                            "<div class='item item-image'> " +
                                "<img src='" + Configuration.BASE_URL + "images/medium/" + user.profilePictureId + "' />" +
                            "</div>" +
                            "<a class='button' href='#/app/users/" + user.id + "'>View Profile</a>" +
                        "</div>",
                icon: {
                    iconUrl: Configuration.BASE_URL + "images/small/" + user.profilePictureId,
                    iconSize:     [50, 50],
                    iconAnchor:   [25, 25],
                    className: "user-marker-" + user.gender
                }
            }

            if(user.isFriend)
                $scope.friends[user.id] = marker

            else $scope.strangers[user.id] = marker;
        }

        $scope.self = {
            layer: "self",
            lat: selfLatitude,
            lng: selfLongitude,
            icon: {
                type: 'div',
                iconSize: [15, 15],
            className: 'self-marker'
            }
        };

        if(showFriends && showStrangers)
            markers = angular.extend(markers, $scope.friends, $scope.strangers);

        else if(showFriends)
            markers = $scope.friends;

        else if(showStrangers)
            markers = $scope.strangers;

        if(showSelf)
            // add marker of self
            markers["self"] = $scope.self;


        return markers;
    }
})