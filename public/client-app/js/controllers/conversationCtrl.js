angular.module('travel.controllers')
.controller('ConversationCtrl', function ($scope, $stateParams, Users, Dialogs) {
    Users.get($stateParams.userId).success(function (user){
            $scope.user = user;

        });
    $scope.messages = Dialogs.get(0).messages;
})