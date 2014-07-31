angular.module('travel.controllers')

.controller('ViewProfileCtrl', function ($scope, $stateParams, Users, Configuration) {
    $scope.user = {};
    Users.get($stateParams.userId).success(function (user){
        user.profilePicture.url = Configuration.BASE_URL + "images/large/" + user.profilePicture.id;
        $scope.user = user;
    });
});
