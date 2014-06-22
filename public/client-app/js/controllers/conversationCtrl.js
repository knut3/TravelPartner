angular.module('travel.controllers')
.controller('ConversationCtrl', function ($scope, $stateParams, Users, Conversations, $ionicScrollDelegate, $timeout) {
    $scope.sendForm = {};
    $scope.sendForm.message = "";

    $timeout(function(){
        $ionicScrollDelegate.$getByHandle("conversationScroll").scrollBottom();
    }, 500);


    Conversations.get($stateParams.userId).success(function (conversation){
            $scope.conversation = conversation;

        });

    $scope.sendForm.send = function(item, event){
        Conversations.sendMessage($scope.conversation.userId, $scope.sendForm.message)
            .success(function(data){
                $scope.conversation.messages.push({
                    message: $scope.sendForm.message,
                    sentByMe: true
                });
                $ionicScrollDelegate.$getByHandle("conversationScroll").scrollBottom();
                $scope.sendForm.message = "";
            } )
            .error(function(){
                alert("unable to send message");
            });
    }
})