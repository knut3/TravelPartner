angular.module('travel.controllers')
.controller('ConversationCtrl', function ($scope, $stateParams, Users, Conversations, $ionicScrollDelegate, $timeout, EventSourceService, LocalEvents) {
    $scope.sendForm = {};
    $scope.sendForm.message = "";

    $scope.scrollToBottom = function(){
        $ionicScrollDelegate.$getByHandle("conversationScroll").scrollBottom();
    }

    $timeout(function(){
        $scope.scrollToBottom();
    }, 500);


    Conversations.get($stateParams.userId)
        .success(function (conversation){
            $scope.conversation = conversation;
            if(conversation.unreadMessageCount > 0)
                $scope.$emit(LocalEvents.MESSAGES_READ, conversation.unreadMessageCount, $stateParams.userId);
        })
        .error(function(){
            alert("unable to fetch conversation");
        });

    // Update the message list whenever a new message arrives
    $scope.$on(EventSourceService.Events.NEW_MESSAGE, function(event, data) {
        if(data.userId === $scope.conversation.userId){
            $scope.conversation.messages.push({
                message: data.message.message,
                sentByMe: false,
                dateTimeSent: data.message.dateTimeSent
            });
            $timeout(function(){
                $scope.scrollToBottom();
            }, 500);
        }
    });

    $scope.sendForm.send = function(item, event){
        Conversations.sendMessage($scope.conversation.userId, $scope.sendForm.message)
            .success(function(data){
                $scope.conversation.messages.push({
                    message: $scope.sendForm.message,
                    sentByMe: true,
                    dateTimeSent: "Now"
                });
                $scope.scrollToBottom();
                $scope.sendForm.message = "";
            } )
            .error(function(){
                alert("unable to send message");
            });
    }
})