package controllers;

import java.io.File;

import play.api.mvc.Action;
import play.api.mvc.AnyContent;
import play.mvc.Result;


public class Resources extends BaseController {
	
	public Action<AnyContent> getImage(String size, String id){
		
		return Assets.at("/public/images/" + size + "/",  id + ".jpg", false);
		
	}
	
}
