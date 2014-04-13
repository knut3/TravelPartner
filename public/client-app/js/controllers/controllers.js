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

.controller('MapCtrl', function ($scope, Travelers, leafletEvents, $state) {

    function getMarkers(travelers){
        var markers = {};
        for (var i = 0; i < travelers.length; i++) {
            var traveler = travelers[i];
            var status = undefined;
            var focus = false;
            if (traveler.status) {
                status = traveler.status;
                focus = true;
            }
            var marker = {
                lat: traveler.latitude,
                lng: traveler.longitude,
                icon: {
                    iconUrl: traveler.profilePicture,
                    iconSize:     [38, 95], // size of the icon
                    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
                    popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
                }
            }


            markers[traveler.id] = marker;

        }
        return markers;
    }

    $scope.events = {
        markers: {
            enable: ['leafletDirectiveMarker.click']
        }
    };

    $scope.$on('leafletDirectiveMarker.click' , function(event, args){
        $state.go('tab.message-detail', {userId: args.markerName})
    });


    $scope.center = {
            lat: 59.7475,
            lng: 10.3936,
            zoom: 12
            };

    $scope.defaults = {
            maxZoom: 14,
            minZoom: 8
            };


    $scope.markers = {};
    Travelers.all().success(function(travelers){
        $scope.markers = getMarkers(travelers);
    });

})

.controller('MessagesCtrl', function ($scope, Travelers) {
    Travelers.all().success(function(travelers){
        $scope.friends = travelers;
    });


})

.controller('MessageDetailCtrl', function ($scope, $stateParams, Travelers, Dialogs) {
    Travelers.get($stateParams.userId).success(function (traveler){
            $scope.friend = traveler;
        });
    $scope.messages = Dialogs.get(0).messages;
})

.controller('AccountCtrl', function ($scope, Travelers) {
});
