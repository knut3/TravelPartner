angular.module('travel.controllers')
.controller('ConversationsCtrl', function ($scope, Conversations, EventSourceService, LocalEvents, $window) {
    Conversations.all().success(function(conversations){
        $scope.conversations = conversations;
    });

    //Set latest message to isRead when the user has successfully loaded
    //the conversation details
    $scope.$on(LocalEvents.MESSAGES_READ, function(event, userId){
        for(var i = 0; i < $scope.conversations.length; i++){
            var conv = $scope.conversations[i];
            if(conv.userId === userId)
                conv.latestMessage.isRead = true;
        }
    });

    $scope.$on(LocalEvents.MESSAGE_SENT, function(event, user, message){
        var messageObject = {
            message: message,
            sentByMe: true,
            dateTimeSent: "now",
            isRead: true
        };

        var conversationExists = false;
        var thisConversation = null;
        for(var i = 0; i < $scope.conversations.length; i++){
            var conv = $scope.conversations[i];
            if(conv.userId === user.id) {
                conv.latestMessage = messageObject
                thisConversation = i;
                conversationExists = true;
            }
        }

        if(!conversationExists) {
            $scope.conversations.push({
                userId: user.id,
                userName: user.name,
                userPictureId: user.profilePictureId,
                latestMessage: messageObject
            });
            thisConversation = $scope.conversations.length -1;
        }

        $scope.conversations = ionic.Utils.arrayMove($scope.conversations, thisConversation, 0);
    });

    //Update the conversations list on new message
    $scope.$on(EventSourceService.Events.NEW_MESSAGE, function(event, data) {

        // The new message is unread unless the user is currently watching the
        // conversation
        var isRead = false;
        if($window.location.hash === "#/app/conversations/"+data.userId)
            isRead = true;

        var conversationBrief = {
            userId: data.userId,
            userName: data.userName,
            userPictureId: data.userPictureId,
            latestMessage: {
                message: data.message.message,
                sentByMe: false,
                dateTimeSent: data.message.dateTimeSent,
                isRead: isRead
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