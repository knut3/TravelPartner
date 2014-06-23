angular.module('travel.controllers')
.controller('ConversationsCtrl', function ($scope, Conversations, EventSourceService, LocalEvents) {
    Conversations.all().success(function(conversations){
        $scope.conversations = conversations;
    });

    //Set latest message to isRead when the user has successfully loaded
    //the conversation details
    $scope.$on(LocalEvents.MESSAGES_READ, function(count, userId){
        for(var i = 0; i < $scope.conversations.length; i++){
            var conv = $scope.conversations[i];
            if(conv.userId === userId)
                conv.latestMessage.isRead = true;
        }
    });

    //Update the conversations list on new message
    $scope.$on(EventSourceService.Events.NEW_MESSAGE, function(event, data) {
        var conversationBrief = {
            userId: data.userId,
            userName: data.userName,
            userPictureId: data.userPictureId,
            latestMessage: {
                message: data.message.message,
                sentByMe: false,
                dateTimeSent: data.message.dateTimeSent,
                isRead: false
            }
        };

        var conversationExists = false;
        for(var i = 0; i < $scope.conversations.length; i++){
            var conv = $scope.conversations[i];
            if(conv.userId === data.userId){
                conv.latestMessage = conversationBrief.latestMessage;
                conversationExists = true;
            }
        }
        if(!conversationExists)
            $scope.conversations.push(conversationBrief);

        $scope.$apply();
    });
})