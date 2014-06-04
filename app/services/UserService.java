package services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import models.Picture;
import models.User;
import play.Logger;
import play.libs.Json;
import play.libs.ws.*;
import play.libs.F.Promise;
import services.interfaces.IImageService;
import services.interfaces.IUserService;
import utils.AppResources;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import exceptions.NoLocationException;

public class UserService implements IUserService{
	
	final static float RADIUS = 0.1f; // 6 km
	
	@Inject
	IImageService imageService;
	

	
	public void createUser(String accessToken, String appSecretProof){
		
		String content;
		try {
			content = "batch=[" + URLEncoder.encode(
					"{'method':'GET', 'relative_url':'me?fields=first_name,gender'}, " +
					"{'method':'GET', 'relative_url':'me/albums?limit=1&fields=photos.limit(5).fields(source, width, height)'}"
					, "UTF-8") + "]";
		} catch (UnsupportedEncodingException e) {
			Logger.error("Bad URL-encoding");
			return;
		}
		WSRequestHolder userInfoRequest = WS.url("https://graph.facebook.com");
		userInfoRequest.setQueryParameter("access_token", accessToken);
		userInfoRequest.setQueryParameter("appsecret_proof", appSecretProof);
		userInfoRequest.setQueryParameter("include_headers", "false");
		Promise<WSResponse> userInfoResponsePromise = userInfoRequest.post(content);
		JsonNode userInfoResp = userInfoResponsePromise.get(5000).asJson();
  		JsonNode me = Json.parse(userInfoResp.get(0).get("body").asText());
  		  		
  		User newUser = new User(
  				me.get("id").asLong(),
  				me.get("first_name").asText(),
  				me.get("gender").asText());
  		
  		JsonNode profilePicturesContainer = Json.parse(userInfoResp
					.get(1)
					.get("body").asText())
					.withArray("data");
  		
  		if(!profilePicturesContainer.has(0)){ // User has no albums (No profile picture)
  			newUser.profilePicture = Picture.find.byId(AppResources.DefaultProfilePictureId);
  			newUser.save();
  			return;
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
  					imageId, 
  					src,
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
  					imageId,
  					src, 
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
	  			
	}

	@Override
	public List<User> getUsersNearby(long userId) throws NoLocationException {
		
		User user = User.find.byId(userId);
		if(user.longitude == null || user.latitude == null)
			throw new NoLocationException();
		
		List<User> users = User.find
		.fetch("profilePicture")
		.where()
		 .ge("latitude", user.latitude - RADIUS)
		 .le("latitude", user.latitude + RADIUS)
		 .ge("longitude", user.longitude - RADIUS)
		 .le("longitude", user.longitude + RADIUS)
		 .not(Expr.eq("id", userId))
        .findList();
		
		return users;
	}

	@Override
	public boolean areUsersWithinRange(long userId, long anotherUserId) {
		
		User user = User.find.byId(userId);
		User anotherUser = User.find.byId(anotherUserId);
		
		if(user.latitude == null || user.longitude == null)
			return false;
		
		boolean withinLatitudeRange = user.latitude >= (anotherUser.latitude - RADIUS) 
									&& user.latitude <= (anotherUser.latitude + RADIUS);
		
		boolean withinLongitudeRange = user.longitude >= (anotherUser.longitude - RADIUS) 
				&& user.longitude <= (anotherUser.longitude + RADIUS);
		
		return withinLatitudeRange && withinLongitudeRange;
		
	}
	
}
