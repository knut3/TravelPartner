package settings;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.Picture;
import models.User;

import org.apache.http.HttpStatus;

import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.libs.Yaml;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Results.Status;
import utils.AppResources;

import com.avaje.ebean.Ebean;
import com.google.inject.Guice;
import com.google.inject.Injector;

import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import exceptions.NoLocationException;
import exceptions.TravelPartnerException;

public class Global extends GlobalSettings {

	private Injector injector;

    @Override
    public void onStart(Application app) {
        injector = Guice.createInjector(new IocBinds());
        InitialData.insert(app);
        AppResources.DefaultProfilePictureId = "default-profile-pic";
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return injector.getInstance(controllerClass);
    }
    
    static class InitialData {
        
        public static void insert(Application app) {
//            if(Ebean.find(User.class).findRowCount() == 0) {
//                
//                @SuppressWarnings("unchecked")
//				Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");
//
//                // Insert projects
//                Ebean.save(all.get("pictures"));
//                
//                // Insert users first
//                Ebean.save(all.get("users"));
//                
//                //Ebean.save(all.get("messages"));
//                
//            }
        }
        
    }
	
	@Override
	public Promise<Result> onBadRequest(RequestHeader request, String error) {
		return Promise.<Result>pure(Results.badRequest("Invalid request arguments"));
	}
	
	@Override
	public Promise<Result> onError(RequestHeader request, Throwable t) {
		
		Status result = Results.internalServerError(
				"The application had some problems in parsing your request. "
				+ "Please contact us if the problem repeats itself.");
		
		Throwable e = t.getCause();
		TravelPartnerException tpEx = null;
		
		if(e instanceof TravelPartnerException)
			tpEx = (TravelPartnerException)e;
		
		if(e instanceof AuthenticationException)
			result = Results.unauthorized(tpEx.getMessage());
		
		else if(e instanceof AuthorizationException)
			result = Results.forbidden(tpEx.getMessage());
		
		else if(e instanceof NoLocationException)
			result = Results.status(HttpStatus.SC_METHOD_NOT_ALLOWED, 
					"You have to update your GPS coordinates in order to see other travelers");
		
		
		
		return Promise.<Result>pure(result);
	}
	
	@Override
	public Promise<Result> onHandlerNotFound(RequestHeader request) {
		return Promise.<Result>pure(Results.notFound(
				"The resource you are looking for could not be found."));
	}
	
	private class ActionWrapper extends Action.Simple {
        public ActionWrapper(Action<?> action) {
            this.delegate = action;
        }

        @Override
        public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
            Promise<Result> result = this.delegate.call(ctx);
            Http.Response response = ctx.response();
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Allow", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
            
            return result;
        }
    }

    @Override
    public Action<?> onRequest(Http.Request request, java.lang.reflect.Method actionMethod) {
        return new ActionWrapper(super.onRequest(request, actionMethod));
    }
    
}
