angular.module('travel.controllers')
.controller('MainCtrl', function($scope, $state, $ionicModal, AuthenticationService, EventSourceService, toaster) {


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


        $scope.$on(EventSourceService.Events.NEW_MESSAGE, function(event, data){
            toaster.pop("success", "", "New message from " + data.senderName, 2500);
        })


        //Cleanup the modal by removing it from the DOM
        $scope.$on('$destroy', function() {
            $scope.loginModal.remove();
        });
})