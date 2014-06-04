angular.module('travel.controllers')
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

