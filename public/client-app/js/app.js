// Ionic Starter App, v0.9.20

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('travel', ['ionic', 'leaflet-directive', 'travel.controllers', 'travel.services', 'ezfb'])

.run(function($ionicPlatform) {
    $ionicPlatform.ready(function() {
    StatusBar.styleDefault();
    });
})

.config(function($stateProvider, $urlRouterProvider, $httpProvider, ezfbProvider) {

  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $stateProvider

    // setup an abstract state for the tabs directive
    .state('tab', {
      url: "/tab",
      abstract: true,
      templateUrl: "assets/client-app/templates/tabs.html"
    })

    // Each tab has its own nav history stack:

    .state('tab.map', {
      url: '/map',
      views: {
        'tab-map': {
            templateUrl: 'assets/client-app/templates/tab-map.html',
          controller: 'MapTabCtrl'
        }
      }
    })

    .state('tab.messages', {
        url: '/messages',
      views: {
          'tab-messages': {
              templateUrl: 'assets/client-app/templates/tab-messages.html',
          controller: 'MessagesCtrl'
        }
      }
    })
    .state('tab.message-detail', {
      url: '/messages/:userId',
      views: {
        'tab-messages': {
            templateUrl: 'assets/client-app/templates/message-detail.html',
          controller: 'MessageDetailCtrl'
        }
      }
    })

    .state('tab.view-profile', {
      url: 'map/:userId',
      views: {
          'tab-map': {
              templateUrl: 'assets/client-app/templates/view-profile.html',
              controller: 'ViewProfileCtrl'
          }
      }
    })

    .state('tab.account', {
      url: '/account',
      views: {
        'tab-account': {
            templateUrl: 'assets/client-app/templates/tab-account.html',
            controller: 'AccountCtrl'
        }
      }
    });

    // if none of the above states are matched, use this as the fallback
    $urlRouterProvider.otherwise('/tab/map');

    ezfbProvider.setInitParams({
        appId: '228240120706289'});
});

