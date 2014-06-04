package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.libs.EventSource;
import play.libs.EventSource.Event;
import services.interfaces.IEventSourceService;

import com.fasterxml.jackson.databind.JsonNode;

public class EventSourceService implements IEventSourceService{


	private static Map<Long, List<EventSource>> socketsPerUser = new HashMap<Long, List<EventSource>>();
	
	public void sendEvent(long userId, JsonNode msg) {
	    if(socketsPerUser.containsKey(userId)) {
	    	socketsPerUser.get(userId).stream().forEach(es -> es.send(Event.event(msg)));
	    }
	}
	
	public void sendEvent(long userId, JsonNode msg, String eventName) {
	    if(socketsPerUser.containsKey(userId)) {
	    	socketsPerUser.get(userId).stream().forEach(es -> es.send(new Event(msg.toString(), "", eventName)));
	    }
	}
	
	@Override
	public EventSource subscribe(long userId, String remoteAddress) {
		
	    Logger.info(remoteAddress + " - SSE conntected");
		
		return new EventSource() {
	      @Override
	      public void onConnected() {
	        EventSource currentSocket = this;
	
	        this.onDisconnected(() -> {
	          Logger.info(remoteAddress + " - SSE disconntected");
	          socketsPerUser.compute(userId, (key, value) -> {
	            if(value.contains(currentSocket))
	              value.remove(currentSocket);
	            return value;
	          });
	        });
	
	        if(!socketsPerUser.containsKey(userId)){
	        	socketsPerUser.put(userId, new ArrayList<EventSource>());
	        }
	        
        	List<EventSource> sockets = socketsPerUser.get(userId);
        	
        	sockets.add(currentSocket);
	        
//	        // Add socket to user
//	        socketsPerUser.compute(userId, (key, value) -> {
//	          if(value == null)
//	            return new ArrayList<EventSource>() {{ add(currentSocket); }};
//	          else
//	            value.add(currentSocket); return value;
//	        });
	      }
	    };
	}
	
	
	
}
