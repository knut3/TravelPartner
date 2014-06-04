package services.interfaces;

import play.libs.EventSource;

import com.fasterxml.jackson.databind.JsonNode;



public interface IEventSourceService {
	
	EventSource subscribe(long userId, String remoteAddress);
	void sendEvent(long userId, JsonNode msg);
	void sendEvent(long userId, JsonNode msg, String eventName);
	
	
}
