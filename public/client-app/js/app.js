// Ionic Starter App, v0.9.20

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.controllers' is found in viewProfileCtrl.js

angular.module('travel.controllers', []);
angular.module('travel.services', ['http-auth-interceptor', 'ezfb']);
angular.module('travel', ['ionic', 'leaflet-directive', 'travel.controllers', 'travel.services', 'ezfb', 'toaster', 'ngCordova', 'pasvaz.bindonce'])

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

  $stateProvider

    .state('app', {
      url: "/app",
      abstract: true,
      templateUrl: "templates/main.html",
      controller: 'MainCtrl',
      resolve:{
          tokenVerification: function(AuthenticationService){
              return AuthenticationService.verifyAccessToken();
          }
      }

    })
    .state('app.map', {
        url: '/map',
        views: {
            'menuContent' :{
                templateUrl: 'templates/map.html'
            }
        }
    })

    .state('app.view-profile', {
        url: '/users/:userId',
        views: {
            'menuContent' :{
                templateUrl: 'templates/view-profile.html',
                controller: 'ViewProfileCtrl'
            }
        }
    })

    .state('app.conversations-details', {
      url: '/conversations/:userId',
      views: {
          'menuContent' :{
              templateUrl: 'templates/conversation.html',
              controller: 'ConversationCtrl'
          }
      }
    })

    .state('app.account', {
        url: '/account',
        views: {
            'menuContent' :{
                templateUrl: 'templates/account.html',
                controller: 'AccountCtrl'
        }
        }
    });

    // if none of the above states are matched, use this as the fallback
    $urlRouterProvider.otherwise('/app/map');

    ezfbProvider.setInitParams({
        appId: '228240120706289'//,
        //nativeInterface: CDV.FB,
        //useCachedDialogs: false
    });

    /*ezfbProvider.setLoadSDKFunction(function ($document, ezfbAsyncInit) {
        ionic.Platform.ready(function(){
            ezfbAsyncInit();
        });
    });*/

    //Enable cross domain calls
    $httpProvider.defaults.useXDomain = true;

    //Remove the header used to identify ajax call  that would prevent CORS from working
    delete $httpProvider.defaults.headers.common['X-Requested-With'];

    $httpProvider.interceptors.push("HttpErrorInterceptor");
    $httpProvider.interceptors.push("AuthInterceptor");
    $httpProvider.interceptors.push("LoadingInterceptor");
});

