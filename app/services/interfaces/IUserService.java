package services.interfaces;

import java.util.List;

import models.view.UserViewModel;
import play.libs.F.Promise;
import exceptions.InvalidIdException;
import exceptions.NoLocationException;


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
		
	boolean areUsersWithinRange(long userId, long anotherUserId) throws InvalidIdException;

	Promise<List<UserViewModel>> getUsersNearby(long userId, String accessToken,
			String appSecretProof) throws NoLocationException;
	
	
}
