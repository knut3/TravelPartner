angular.module('travel.controllers', [])

.controller('MainCtrl', function($scope, $state, $ionicModal, AuthenticationService) {

    AuthenticationService.authHeaderUpdateFromCache();

    $ionicModal.fromTemplateUrl('assets/client-app/templates/login.html', function(modal) {
            $scope.loginModal = modal;
        },
        {
            scope: $scope,
            animation: 'slide-in-up',
            focusFirstInput: true
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

.controller('MapTabCtrl', function ($scope) {
    $scope.contr = ""
})

.controller('MapCtrl', function ($scope, $window, Users, leafletEvents, $state, Locations) {

    function getMarkers(users){
        var markers = {};
        for (var i = 0; i < users.length; i++) {
            var user = users[i];
            var status = undefined;
            if (user.status) {
                status = user.status;
            }
            var marker = {
                lat: user.latitude,
                lng: user.longitude,
                icon: {
                    iconUrl: user.profilePicture.url,
                    iconSize:     [user.profilePicture.width, user.profilePicture.height], // size of the icon
                    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
                    popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
                }
            }


            markers[user.id] = marker;

        }
        return markers;
    }

    $scope.events = {
        markers: {
            enable: ['leafletDirectiveMarker.click']
        }
    };

    $scope.$on('leafletDirectiveMarker.click' , function(event, args){
        $state.go('tab.view-profile', {userId: args.markerName})
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
        $window.navigator.geolocation.getCurrentPosition(function(position){
            Locations.setCurrent(position.coords.latitude, position.coords.longitude);
            $scope.$apply(function(){
                $scope.center.lat = position.coords.latitude;
                $scope.center.lng = position.coords.longitude;
            });
            Users.all().success(function(users){
                $scope.$apply(function(){
                    $scope.markers = getMarkers(users);
                });
            });
        })
    };

    Locations.getCurrent().success(function(location){
        if(location.latitude != null && location.longitude != null){
            $scope.center.lat = location.latitude;
            $scope.center.lng = location.longitude;
            Users.all().success(function(users){
                $scope.markers = getMarkers(users);
            });
        }
        else{
            //Display something here
        }
    });



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
