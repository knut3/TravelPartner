angular.module('travel.controllers')

.controller('ViewProfileCtrl', function ($scope, $stateParams, Users) {
    $scope.user = {};
    Users.get($stateParams.userId).success(function (user){
        user.profilePicture.url = "images/large/" + user.profilePicture.id;
        $scope.user = user;
    });
});
