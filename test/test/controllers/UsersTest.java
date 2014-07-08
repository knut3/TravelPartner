package test.controllers;

import static play.test.Helpers.contentAsString;

import java.util.UUID;

import models.Location;
import models.Picture;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.mvc.Result;
import services.ImageService;
import services.UserService;
import config.BaseTest;
import controllers.Users;
import exceptions.NoLocationException;

public class UsersTest extends BaseTest{
	
	private static final long NUM_USERS = 5;
	final long LOGGED_IN_USER_ID = 1;
	
	class UsersCtrl extends Users{
		
		public UsersCtrl() {
			super(new UserService(new ImageService()));
		}

		// No authentication
		@Override
		protected long getCurrentUserId() {
			return LOGGED_IN_USER_ID;
		}
	}
	
	UsersCtrl usersCtrl = new UsersCtrl();
	
	@Before
	public void insertUsers() {		
		for(long i = 1; i <= NUM_USERS; i++){
			User user = new User(i, "User"+i, i%2==0?"male":"female");
			Location userLoc = new Location(user.id, 10f + i*(UserService.RADIUS/3), 10f, "");
			userLoc.save();
			user.setLocation(userLoc);
			Picture profilePic = new Picture(UUID.randomUUID().toString(), i, 10, 10);
			profilePic.save();
			user.profilePicture = profilePic;
			user.save();
		}
	}
	
	@Test
	public void getUsersNearYou() throws NoLocationException {
	    Result result = usersCtrl.getUsersNearYou();
	    String res = contentAsString(result);
	    System.out.println(res);
	    
  }
}
