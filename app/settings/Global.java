package settings;

import play.GlobalSettings;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Results;
import play.mvc.SimpleResult;

public class Global extends GlobalSettings {

	
	
	@Override
	public Promise<SimpleResult> onBadRequest(RequestHeader request, String error) {
		return Promise.<SimpleResult>pure(Results.badRequest("Invalid request arguments"));
	}
	
	@Override
	public Promise<SimpleResult> onError(RequestHeader request, Throwable t) {
		return Promise.<SimpleResult>pure(Results.internalServerError(
				"The application had some problems in parsing your request. "
				+ "Please contact us if the problem repeats itself."));
	}
	
	@Override
	public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
		return Promise.<SimpleResult>pure(Results.notFound(
				"The resource you are looking for could not be found."));
	}
    
}
