package services.interfaces;

import java.util.List;

import play.libs.F.Promise;
import exceptions.NoLocationException;
import models.User;


public interface IUserService {
	
	/**
	 * Create a new user based on basic information gathered from Facebook
	 * Graph API.
	 * @param accessToken Facebook access token
	 * 
	 * @param appSecretProof App secret identifying this app
	 * @param userId 
	 * @return 
	 */
	Promise<Void> createUser(String accessToken, String appSecretProof, long userId);
	
	List<User> getUsersNearby(long userId) throws NoLocationException;
	
	boolean areUsersWithinRange(long userId, long anotherUserId);
	
}
