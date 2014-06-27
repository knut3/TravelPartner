package controllers;

import play.mvc.Result;
import services.interfaces.IEventSourceService;
import annotations.Authentication.RequiresAuthentication;

import com.google.inject.Inject;

public class Application extends BaseController {

	@Inject
	IEventSourceService eventSourceService;
	
	public Result index()  {	 
  		return ok(views.html.main.render());
	}
	
	@RequiresAuthentication
	public Result subscribeEvents() {
	    return ok(eventSourceService.subscribe(getCurrentUserId(), request().remoteAddress()));
	}

}
