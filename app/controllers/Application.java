package controllers;

import play.mvc.Result;
import services.interfaces.IEventSourceService;
import annotations.Authorization.Authorized;

import com.google.inject.Inject;

public class Application extends BaseController {

	@Inject
	IEventSourceService eventSourceService;
	
	public Result index()  {	 
  		return ok(views.html.main.render());
	}
	
	@Authorized
	public Result subscribeEvents() {
	    return ok(eventSourceService.subscribe(getCurrentUserId(), request().remoteAddress()));
	}

}
