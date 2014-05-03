// Ionic Starter App, v0.9.20

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('travel', ['ionic', 'leaflet-directive', 'travel.controllers', 'travel.services', 'ezfb', 'toaster'])

.run(function($ionicPlatform) {
    $ionicPlatform.ready(function() {
        if(window.StatusBar) {
            // org.apache.cordova.statusbar required
            StatusBar.styleDefault();
        }
    });
})

.config(function($stateProvider, $urlRouterProvider, $httpProvider, ezfbProvider) {

  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $stateProvider

    .state('app', {
      url: "/app",
      abstract: true,
      templateUrl: "assets/client-app/templates/menu.html",
      controller: 'AppCtrl'
    })
    .state('app.map', {
        url: '/map',
        views: {
            'menuContent' :{
                templateUrl: 'assets/client-app/templates/map.html'
            }
        }
    })
    .state('app.messages', {
        url: '/messages',
        views: {
            'menuContent' :{
                templateUrl: 'assets/client-app/templates/messages.html',
                controller: 'MessagesCtrl'
        }
      }
    })
    .state('app.message-detail', {
        url: '/messages/:userId',
        views: {
            'menuContent' :{
                templateUrl: 'assets/client-app/templates/message-detail.html',
                controller: 'MessageDetailCtrl'
            }
        }
    })

    .state('app.view-profile', {
        url: '/users/:userId',
        views: {
            'menuContent' :{
                templateUrl: 'assets/client-app/templates/view-profile.html',
                controller: 'ViewProfileCtrl'
            }
        }
    })

    .state('app.account', {
        url: '/account',
        views: {
            'menuContent' :{
                templateUrl: 'assets/client-app/templates/account.html',
                controller: 'AccountCtrl'
        }
        }
    });

    // if none of the above states are matched, use this as the fallback
    $urlRouterProvider.otherwise('/app/map');

    ezfbProvider.setInitParams({
        appId: '228240120706289'});
});

