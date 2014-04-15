package settings;

import java.util.List;
import java.util.Map;

import models.User;
import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.libs.Yaml;
import play.mvc.Http.RequestHeader;
import play.mvc.Results;
import play.mvc.SimpleResult;

import com.avaje.ebean.Ebean;

public class Global extends GlobalSettings {

	public void onStart(Application app) {
        InitialData.insert(app);
    }
    
    static class InitialData {
        
        public static void insert(Application app) {
            if(Ebean.find(User.class).findRowCount() == 0) {
                
                @SuppressWarnings("unchecked")
				Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");

                

                // Insert projects
                Ebean.save(all.get("pictures"));
                
                // Insert users first
                Ebean.save(all.get("users"));
                
            }
        }
        
    }
	
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
