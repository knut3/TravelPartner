package services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import models.Picture;
import models.User;
import models.view.UserViewModel;
import play.Logger;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import services.interfaces.IImageService;
import services.interfaces.IUserService;
import utils.AppResources;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import exceptions.InvalidIdException;
import exceptions.NoLocationException;

public class UserService implements IUserService{
	
	public final static float RADIUS = 0.1f; // 6 km
	
	IImageService imageService;
	
	@Inject
	public UserService(IImageService imageService) {
		this.imageService = imageService;
	}

	public Promise<Void> createUser(String accessToken, String appSecretProof, long userId){
		
		User newUser = new User(userId);
		
		String content;
		try {
			content = "batch=[" + URLEncoder.encode(
					"{'method':'GET', 'relative_url':'me?fields=first_name,gender'}, " +
					"{'method':'GET', 'relative_url':'me/albums?limit=1&fields=photos.limit(5).fields(source, width, height)'}"
					, "UTF-8") + "]";
		} catch (UnsupportedEncodingException e) {
			Logger.error("Bad URL-encoding");
			return Promise.<Void>pure(null);
		}
		WSRequestHolder userInfoRequest = WS.url("https://graph.facebook.com");
		userInfoRequest.setQueryParameter("access_token", accessToken);
		userInfoRequest.setQueryParameter("appsecret_proof", appSecretProof);
		userInfoRequest.setQueryParameter("include_headers", "false");
		Promise<WSResponse> userInfoResponsePromise = userInfoRequest.post(content);
		
		return userInfoResponsePromise.map(userInfo -> {
		
			JsonNode userInfoResp = userInfo.asJson();
	  		JsonNode me = Json.parse(userInfoResp.get(0).get("body").asText());
	  		  		
	  		
	  		newUser.firstName = me.get("first_name").asText();
	  		newUser.gender = me.get("gender").asText();
	  		
	  		JsonNode profilePicturesContainer = Json.parse(userInfoResp
						.get(1)
						.get("body").asText())
						.withArray("data");
	  		
	  		if(!profilePicturesContainer.has(0)){ // User has no albums (No profile picture)
	  			newUser.profilePicture = Picture.find.byId(AppResources.DefaultProfilePictureId);
	  			newUser.save();
	  			return null;
	  		}
	  		
	  		JsonNode profilePictures = profilePicturesContainer
										.get(0)
										.get("photos")
										.withArray("data");
	  		
	  		Iterator<JsonNode> pictureIterator = profilePictures.iterator();
	  		// First picture is set as profile picture
	  		Picture profilePicture;
	  		if(pictureIterator.hasNext()){
	  			JsonNode pic = pictureIterator.next();
	  			UUID imageId = UUID.randomUUID();
	  			String src = pic.get("source").asText();
	  			int width = pic.get("width").asInt();
	  			int height = pic.get("height").asInt();
	  			profilePicture = new Picture(
	  					imageId.toString(), 
	  					newUser.id, 
	  					width,
	  					height);
	  			try {
					imageService.generateResizedImages(src, imageId, width, height);
				} catch (IOException e) {
					Logger.error(src + " was not reachable");
				}
	  		}  	
	  		else{
	  			profilePicture = Picture.find.byId(AppResources.DefaultProfilePictureId);
	  		}
	  		
	  		profilePicture.save();
	  		newUser.profilePicture = profilePicture;
	  		newUser.save();
	  		
	  		// Save rest of the profile pictures
	  		while(pictureIterator.hasNext()){
	  			JsonNode pic = pictureIterator.next();
	  			UUID imageId = UUID.randomUUID();
	  			String src = pic.get("source").asText();
	  			int width = pic.get("width").asInt();
	  			int height = pic.get("height").asInt();
	  			new Picture(
	  					imageId.toString(),
	  					newUser.id, 
	  					width, 
	  					height)
	  			.save(); 
	  			try {
					imageService.generateResizedImages(src, imageId, width, height);
				} catch (IOException e) {
					Logger.error(src + " was not reachable");
				}
	  		}
	  		return null;
		});
	  			
	}

	@Override
	public Promise<List<UserViewModel>> getUsersNearby(long userId, String accessToken, String appSecretProof) throws NoLocationException {
		
		User user = User.find.fetch("currentLocation").select("currentLocation").where().eq("id", userId).findUnique();

		if(user.currentLocation == null)
			throw new NoLocationException();
		
		List<User> users = User.find
		.fetch("profilePicture")
		.fetch("currentLocation")
		.where()
		 .ge("currentLocation.latitude", user.currentLocation.latitude - RADIUS)
		 .le("currentLocation.latitude", user.currentLocation.latitude + RADIUS)
		 .ge("currentLocation.longitude", user.currentLocation.longitude - RADIUS)
		 .le("currentLocation.longitude", user.currentLocation.longitude + RADIUS)
		 .not(Expr.eq("id", userId))
        .findList();
		
		
		StringBuilder userIds = new StringBuilder();
		for(int i = 0; i < users.size(); i++){
			if(i != 0 )
				userIds.append(',');
			userIds.append(users.get(i).id);
		}
		
		
		WSRequestHolder friendsRequest = WS.url("https://graph.facebook.com/fql");
		friendsRequest.setQueryParameter("access_token", accessToken);
		friendsRequest.setQueryParameter("appsecret_proof", appSecretProof);
		friendsRequest.setQueryParameter("q", "SELECT uid2 FROM friend WHERE uid1=me() and uid2 in(" + userIds + ")");
  		Promise<WSResponse> friendsResponsePromise = friendsRequest.get();
		
		return friendsResponsePromise.map(friendsResp -> {
			
			JsonNode friends = friendsResp.asJson().withArray("data");			
			
			List<UserViewModel> userViewModels = new ArrayList<UserViewModel>();
			for(User u : users){
				
				UserViewModel uv = new UserViewModel(u);
				
				for(JsonNode friend : friends){
					long friendId = Long.parseLong(friend.get("uid2").asText());
					if(friendId == u.id)
						uv.isFriend = true;
				}
				userViewModels.add(uv);
			}
			
			return userViewModels;
		});
		
	}

	@Override
	public boolean areUsersWithinRange(long userId, long anotherUserId) throws InvalidIdException {
		
		int userCount = User.find.where().or(Expr.eq("id", userId), Expr.eq("id", anotherUserId)).findRowCount();
		
		if(userCount != 2)
			throw new InvalidIdException("Invalid user ID");
		
		User user = User.find.fetch("currentLocation").select("currentLocation").where().eq("id", userId).findUnique();
		User anotherUser = User.find.fetch("currentLocation").select("currentLocation").where().eq("id", anotherUserId).findUnique();
		
		if(user.currentLocation == null || anotherUser.currentLocation == null)
			return false;
		
		boolean withinLatitudeRange = user.currentLocation.latitude >= (anotherUser.currentLocation.latitude - RADIUS) 
									&& user.currentLocation.latitude <= (anotherUser.currentLocation.latitude + RADIUS);
		
		boolean withinLongitudeRange = user.currentLocation.longitude >= (anotherUser.currentLocation.longitude - RADIUS) 
				&& user.currentLocation.longitude <= (anotherUser.currentLocation.longitude + RADIUS);
		
		return withinLatitudeRange && withinLongitudeRange;
		
	}
	
	
	
}
