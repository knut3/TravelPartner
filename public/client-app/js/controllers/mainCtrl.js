angular.module('travel.controllers')
.controller('MainCtrl', function($scope, $state, $ionicModal, AuthenticationService, EventSourceService, LocalEvents, toaster, Conversations, $window) {


        AuthenticationService.authHeaderUpdateFromCache();
        EventSourceService.setup();

        $ionicModal.fromTemplateUrl('assets/client-app/templates/login.html', function(modal) {
                $scope.loginModal = modal;
            },
            {
                scope: $scope,
                animation: 'slide-in-up'
            }
        );

        Conversations.getUnreadMessageCount()
            .success(function(count){
                $scope.unreadMessageCount = count;
            })
            .error(function(){
                console.log("unable to fetch unread msg count");
            });


        $scope.$on(EventSourceService.Events.NEW_MESSAGE, function(event, data){
            //Increment the unread messages counter if the user is not already
            //looking at the conversation
            if($window.location.hash !== "#/app/conversations/"+data.userId)
                $scope.unreadMessageCount++;
            toaster.pop("info", "", "New message from " + data.userName, 2500);
        });

        $scope.$on(LocalEvents.MESSAGES_READ, function(count){
           $scope.unreadMessageCount -= count;
        });


        //Cleanup the modal by removing it from the DOM
        $scope.$on('$destroy', function() {
            $scope.loginModal.remove();
        });
})