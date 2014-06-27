package controllers;

import java.io.File;

import play.mvc.Result;


public class Resources extends BaseController {
	
	public Result getImage(String size, String id){
		
		response().setContentType("image");

        return ok(new File("public/images/"+ size + "/" + id + ".jpg"));
		
	}
	
}
