package settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.Location;
import models.Picture;
import models.User;
import models.view.Coordinate;

import org.apache.http.HttpStatus;

import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Results.Status;
import services.UserService;
import utils.AppResources;

import com.avaje.ebean.Ebean;
import com.google.inject.Guice;
import com.google.inject.Injector;

import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import exceptions.InvalidIdException;
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
            if(Ebean.find(User.class).findRowCount() == 0) {
                
            	final int NUM_USERS = 150;
            	final Coordinate CENTER = new Coordinate(59.7475f, 10.3936f);
            	Random rnd = new Random();
            	final float RADIUS = UserService.RADIUS;
            	
            	Picture defaultPic = new Picture("default-profile-pic", 1l, 50, 50);
                List<Location> locations = new ArrayList<Location>();
                List<User> users = new ArrayList<User>();
                
                for(long i = 1; i <= NUM_USERS; i++){
                	User user = new User(i);
                	user.firstName = "User " + i;
                	Location loc = new Location(i, 
                			CENTER.latitude - RADIUS + (RADIUS * 2 * rnd.nextFloat()),
                			CENTER.longitude - RADIUS + (RADIUS * 2 * rnd.nextFloat()),
                			"Røyken");
                	user.setLocation(loc);
                	
                	
                	// Users on same location
                	User user2 = new User(NUM_USERS+i);
                	Location loc2 = new Location(NUM_USERS+i, CENTER.latitude, CENTER.longitude, "Røyken");
                	user2.currentLocation = loc2;
                	user2.firstName = "User " + NUM_USERS + i;
                	user.profilePicture = user2.profilePicture = defaultPic;
                	user.gender = user2.gender = i % 2 == 0 ? "male" : "female";
                	
                	locations.add(loc);
                	users.add(user);
                	locations.add(loc2);
                	users.add(user2);
                	
                }
                
                
                Ebean.save(locations);
                defaultPic.save();
                Ebean.save(users);
                
            }
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
		
		else if(e instanceof InvalidIdException)
			result = Results.badRequest(tpEx.getMessage());
		
		
		
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
            
            return result;
        }
    }

    @Override
    public Action<?> onRequest(Http.Request request, java.lang.reflect.Method actionMethod) {
        return new ActionWrapper(super.onRequest(request, actionMethod));
    }
    
}
