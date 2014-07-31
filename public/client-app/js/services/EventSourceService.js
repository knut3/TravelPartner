angular.module('travel.services')

.factory('EventSourceService', function(Locations, $window, LocalStorageKeys, $rootScope, toaster, Configuration) {

    var serverEvents = { NEW_MESSAGE: "new-message" };
    var serverEventsHandled = [ serverEvents.NEW_MESSAGE ];

    return {

        Events: serverEvents,

        setup: function(){
            if(typeof(EventSource) === "undefined"){
                toaster.pop("error", "", "Server-sent events are not supported by your browser. " +
                    "It is highly suggested that you change to a browser that supports it, i.e. Firefox or Chrome", 5000);
                return;
            }
            var eventSource = new EventSource(Configuration.BASE_URL + "subscribe-events?accessToken="+$window.localStorage[LocalStorageKeys.ACCESS_TOKEN]);

            for(var i = 0; i < serverEventsHandled.length; i++)
            {
                var event = serverEventsHandled[i];
                eventSource.addEventListener(event, function(data){
                    $rootScope.$broadcast(data.type, JSON.parse(data.data));
                } , false);
            }
        }
    }
});