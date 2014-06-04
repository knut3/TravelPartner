package services.interfaces;

import java.util.List;

import exceptions.NoLocationException;
import models.User;


public interface IUserService {
	
	/**
	 * Create a new user based on basic information gathered from Facebook
	 * Graph API.
	 * @param accessToken Facebook access token
	 * 
	 * @param appSecretProof App secret identifying this app
	 */
	void createUser(String accessToken, String appSecretProof);
	
	List<User> getUsersNearby(long userId) throws NoLocationException;
	
	boolean areUsersWithinRange(long userId, long anotherUserId);
	
}
