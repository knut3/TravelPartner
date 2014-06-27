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
	
	public static Result options(String path){
		response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader("Allow", "*");
        response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        response().setHeader("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent");
        
        return ok();
	}

}
