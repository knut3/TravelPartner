package test.services;


import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import models.Location;
import models.User;
import models.view.UserViewModel;

import org.junit.Test;

import services.ImageService;
import services.UserService;
import services.interfaces.IUserService;
import config.BaseTest;
import exceptions.InvalidIdException;
import exceptions.NoLocationException;


public class UserServiceTest extends BaseTest{

	
	private IUserService userService = new UserService(new ImageService());
	
	@Test(expected = NoLocationException.class)
	public void getUsersNearby_NoLocation() throws NoLocationException{
		final long ID = 1;
		new User(ID, "Knut", "male").save();
		userService.getUsersNearby(ID);
	}
	
	@Test
	public void getUsersNearby() throws NoLocationException{
		User me = new User(1L, "Knut", "male");
		Location myLoc = new Location(me.id, 10f, 10f, "");
		myLoc.save();
		me.setLocation(myLoc);
		me.save();
		User nearby1 = new User(2L, "Knut", "male");
		Location nearby1Loc = new Location(nearby1.id, 
				myLoc.latitude + UserService.RADIUS - UserService.RADIUS/2,
				myLoc.longitude - UserService.RADIUS + UserService.RADIUS/2, "");
		nearby1Loc.save();
		nearby1.setLocation(nearby1Loc);
		nearby1.save();
		User nearby2 = new User(3L, "Knut", "male");
		Location nearby2Loc = new Location(nearby2.id, 
				myLoc.latitude - UserService.RADIUS + UserService.RADIUS/100,
				myLoc.longitude + UserService.RADIUS - UserService.RADIUS/100, "");
		nearby2Loc.save();
		nearby2.setLocation(nearby2Loc);
		nearby2.save();
		User tooFarAway = new User(4L, "Knut", "male");
		Location tooFarAwayloc = new Location(tooFarAway.id, 
				myLoc.latitude + UserService.RADIUS + 1,
				myLoc.longitude - UserService.RADIUS - 1, "");
		tooFarAwayloc.save();
		tooFarAway.setLocation(tooFarAwayloc);
		tooFarAway.save();
		List<UserViewModel> usersNearby = userService.getUsersNearby(me.id);
		
		assertThat(usersNearby.size()).isEqualTo(2);
		
		for(UserViewModel user : usersNearby){
			assertThat(user.id).isIn(2L, 3L);
		}
		
	}
	
	@Test
	public void getUsersNearby_SameCoordsAsMe() throws NoLocationException{
		User me = new User(1L, "Knut", "male");
		Location myLoc = new Location(me.id, 10f, 10f, "");
		myLoc.save();
		me.setLocation(myLoc);
		me.save();
		User otherUser = new User(2L, "Knut", "male");
		Location otherUserLoc = new Location(otherUser.id, 
				myLoc.latitude,
				myLoc.longitude, "");
		otherUserLoc.save();
		otherUser.setLocation(otherUserLoc);
		otherUser.save();
		
		List<UserViewModel> usersNearby = userService.getUsersNearby(me.id);
				
		assertThat(usersNearby.size()).isEqualTo(1);
		assertThat(usersNearby.get(0).id).isEqualTo(otherUser.id);
		
	}
	
	@Test(expected = InvalidIdException.class)
	public void areUsersWithinRange_InvalidUserId() throws InvalidIdException{
		
		User user = new User(1L, "Knut", "male");
		Location myLoc = new Location(user.id, 10f, 10f, "");
		myLoc.save();
		user.setLocation(myLoc);
		user.save();		
		boolean areUsersWithinRange = userService.areUsersWithinRange(666, user.id);		
		assertThat(areUsersWithinRange).isFalse();		
	}
	
	@Test(expected = InvalidIdException.class)
	public void areUsersWithinRange_InvalidAnotherUserId() throws InvalidIdException{
		
		User user = new User(1L, "Knut", "male");
		Location myLoc = new Location(user.id, 10f, 10f, "");
		myLoc.save();
		user.setLocation(myLoc);
		user.save();
		boolean areUsersWithinRange = userService.areUsersWithinRange(user.id, 666);
		assertThat(areUsersWithinRange).isFalse();
	}
	
	@Test
	public void areUsersWithinRange_True() throws InvalidIdException{
		
		User user = new User(1L, "Knut", "male");
		Location userLoc = new Location(user.id, 10f, 10f, "");
		userLoc.save();
		user.setLocation(userLoc);
		user.save();
		User anotherUser = new User(2L, "Knut", "male");
		Location anotherUserLoc = new Location(anotherUser.id,
				userLoc.latitude + UserService.RADIUS/2, userLoc.longitude + UserService.RADIUS/2, "");
		anotherUserLoc.save();
		anotherUser.setLocation(anotherUserLoc);
		anotherUser.save();
		boolean areUsersWithinRange = userService.areUsersWithinRange(user.id, anotherUser.id);
		
		assertThat(areUsersWithinRange).isTrue();
	}
	
	@Test
	public void areUsersWithinRange_False() throws InvalidIdException{
		
		User user = new User(1L, "Knut", "male");
		Location userLoc = new Location(user.id, 10f, 10f, "");
		userLoc.save();
		user.setLocation(userLoc);
		user.save();
		User anotherUser = new User(2L, "Knut", "male");
		Location anotherUserLoc = new Location(anotherUser.id,
				userLoc.latitude + UserService.RADIUS/2, userLoc.longitude + UserService.RADIUS*2, "");
		anotherUserLoc.save();
		anotherUser.setLocation(anotherUserLoc);
		anotherUser.save();
		boolean areUsersWithinRange = userService.areUsersWithinRange(user.id, anotherUser.id);
		
		assertThat(areUsersWithinRange).isFalse();
	}
	
	@Test
	public void areUsersWithinRange_NoCurrentLocation() throws InvalidIdException{
		
		User user = new User(1L, "Knut", "male");
		Location userLoc = new Location(user.id, 10f, 10f, "");
		userLoc.save();
		user.setLocation(userLoc);
		user.save();
		User anotherUser = new User(2L, "Knut", "male");
		anotherUser.save();
		boolean areUsersWithinRange = userService.areUsersWithinRange(user.id, anotherUser.id);
		
		assertThat(areUsersWithinRange).isFalse();
		
		areUsersWithinRange = userService.areUsersWithinRange(anotherUser.id, user.id);
		
		assertThat(areUsersWithinRange).isFalse();
	}
	
}
