package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import utils.ContextArgsKey;

public class BaseController extends Controller{

	protected long getCurrentUserId(){
		return Long.parseLong((String) Http.Context.current().args.get(ContextArgsKey.USER_ID));
	}
	
}
