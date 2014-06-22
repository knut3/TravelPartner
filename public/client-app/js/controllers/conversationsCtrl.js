angular.module('travel.controllers')
.controller('ConversationsCtrl', function ($scope, Conversations, EventSourceService) {
    Conversations.all().success(function(conversations){
        $scope.conversations = conversations;
    });

    $scope.$on(EventSourceService.Events.NEW_MESSAGE, function(event, data) {
        var conversationBrief = {
            userId: data.userId,
            userName: data.userName,
            userPictureId: data.userPictureId,
            latestMessage: {
                message: data.message,
                sentByMe: false
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