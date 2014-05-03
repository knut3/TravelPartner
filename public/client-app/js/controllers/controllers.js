angular.module('travel.controllers', [])

.controller('AppCtrl', function($scope) {
})


.controller('MainCtrl', function($scope, $state, $ionicModal, AuthenticationService) {

    AuthenticationService.authHeaderUpdateFromCache();

    $ionicModal.fromTemplateUrl('assets/client-app/templates/login.html', function(modal) {
            $scope.loginModal = modal;
        },
        {
            scope: $scope,
            animation: 'slide-in-up'
        }
    );
    //Be sure to cleanup the modal by removing it from the DOM
    $scope.$on('$destroy', function() {
        $scope.loginModal.remove();
    });
})

.controller('LoginCtrl', function($scope, $http, $state, AuthenticationService) {
    $scope.message = '';

        $scope.login = function () {
            AuthenticationService.login();
        };

        $scope.logout = function () {
            AuthenticationService.logout();
        };



    $scope.$on('event:auth-loginRequired', function(e, rejection) {
        $scope.loginModal.show();
    });

    $scope.$on('event:auth-loginConfirmed', function() {
        $scope.loginModal.hide();
    });

    $scope.$on('event:auth-logout-complete', function() {
        $state.go("map");
        $scope.loginModal.show();
    });
})


.controller('SignOutCtrl', function($scope, AuthenticationService) {
    AuthenticationService.logout();
})

.controller('MapCtrl', function ($scope, $window, Users, leafletEvents, $state, Locations, toaster, $ionicPopup) {

    function createMarkers(users, selfLatitude, selfLongitude){
        var markers = {};
        for (var i = 0; i < users.length; i++) {
            var user = users[i];
            var marker = {
                layer: "users",
                lat: user.latitude,
                lng: user.longitude,
                icon: {
                    iconUrl: user.profilePicture.url,
                    iconSize:     [user.profilePicture.width, user.profilePicture.height],
                    iconAnchor:   [user.profilePicture.width/2, user.profilePicture.height/2],
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

    $scope.$on('leafletDirectiveMarker.click' , function(event, args){
        if(args.markerName === "self")
            $state.go("tab.account");
        else {
            $state.go('tab.view-profile', {userId: args.markerName})
        }
    });


    $scope.center = {
            lat: 59.7475,
            lng: 10.3936,
            zoom: 12
            };

    $scope.defaults = {
            minZoom: 8
            };


    $scope.markers = {};

    $scope.setPosition = function(){
        $window.navigator.geolocation.getCurrentPosition(
            function(position){
                Locations.setCurrent(position.coords.latitude, position.coords.longitude)
                    .then(function(){
                        Users.all().success(function(users){
                            $scope.center.lat = position.coords.latitude;
                            $scope.center.lng = position.coords.longitude;
                            $scope.markers = createMarkers(users, position.coords.latitude, position.coords.longitude);
                            $scope.message = "";
                            toaster.pop("success", "", "Location updated", 2500);
                        });

                    });

            },
            function(){
                toaster.pop("error", "", "Unable to get your location");
            })
    };

    Locations.getCurrent().success(function(location){
        if(location.latitude != null && location.longitude != null){
            $scope.center.lat = location.latitude;
            $scope.center.lng = location.longitude;
            Users.all().success(function(users){
                $scope.markers = createMarkers(users, location.latitude, location.longitude);
            });
        }
        else{
            $scope.message = "Update your location to see travelers near you";
        }
    });

    $scope.message = "";

})

.controller('MessagesCtrl', function ($scope, Users) {
    Users.all().success(function(users){
        $scope.users = users;
    });


})

.controller('MessageDetailCtrl', function ($scope, $stateParams, Users, Dialogs) {
    Users.get($stateParams.userId).success(function (user){
            $scope.user = user;
        });
    $scope.messages = Dialogs.get(0).messages;
})

.controller('AccountCtrl', function ($scope) {
})

.controller('ViewProfileCtrl', function ($scope, $stateParams, Users) {
    $scope.user = {};
    Users.get($stateParams.userId).success(function (user){
        $scope.user = user;
    });
});
