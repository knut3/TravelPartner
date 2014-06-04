angular.module('travel.controllers')

.controller('ViewProfileCtrl', function ($scope, $stateParams, Users) {
    $scope.user = {};
    Users.get($stateParams.userId).success(function (user){
        user.profilePicture.url = "assets/images/large/" + user.profilePicture.id + ".jpg";
        $scope.user = user;
    });
});
