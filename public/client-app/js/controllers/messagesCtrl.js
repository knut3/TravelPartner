angular.module('travel.controllers')
.controller('MessagesCtrl', function ($scope, Users) {
    Users.all().success(function(users){
        $scope.users = users;
    });

    alert("messages");

})