angular.module('travel.controllers')
.controller('SignOutCtrl', function($scope, AuthenticationService) {
    AuthenticationService.logout();
})
