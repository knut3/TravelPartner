package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import models.Picture;
import models.User;
import play.Play;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.WS;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Results;
import play.mvc.SimpleResult;
import play.mvc.With;
import utils.ContextArgsKeys;
import utils.Utils;

import com.fasterxml.jackson.databind.JsonNode;

public class Authorization extends Action<Authorization.Authorized>{
		
	@With(Authorization.class)
	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Authorized {
	}
		
    public Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
		String authHeader = ctx.request().getHeader("Authorization");
		if(authHeader == null)
			return Promise.<SimpleResult>pure(Results.unauthorized("Missing Authorization header"));
		
		authHeader = authHeader.trim();
		String authType = authHeader.substring(0, 6);
		if(!authType.equalsIgnoreCase("Bearer"))
			return Promise.<SimpleResult>pure(Results.unauthorized("Auth type should be Bearer"));
		
		String accessToken = authHeader.substring(6).trim();
		String appSecret = Play.application().configuration().getString("facebook.appSecret");
		String appSecretProof = Utils.generateAppSecretProof(accessToken, appSecret);
		
		WSRequestHolder idRequest = WS.url("https://graph.facebook.com/me");
		idRequest.setQueryParameter("access_token", accessToken);
		idRequest.setQueryParameter("appsecret_proof", appSecretProof);
		idRequest.setQueryParameter("fields", "id");
  		Promise<Response> idResponsePromise = idRequest.get();
  		JsonNode idResp = idResponsePromise.get(5000).asJson();
  		if(!idResp.has("id"))
  			return Promise.<SimpleResult>pure(Results.unauthorized("Invalid access token"));
  		String userId = idResp.get("id").asText();
	  		
  		boolean userExists 
        = (User.find.where().idEq(userId).findRowCount() == 1) ? true : false;
  		
  		if(!userExists){
  			String content = "batch=["
  					+ "{'method':'GET', 'relative_url':'me?fields=first_name,gender'}, "
  					+ "{'method':'GET', 'relative_url':'me/picture?redirect=0'}]";
  			WSRequestHolder userInfoRequest = WS.url("https://graph.facebook.com");
  			userInfoRequest.setQueryParameter("access_token", accessToken);
  			idRequest.setQueryParameter("appsecret_proof", appSecretProof);
  			userInfoRequest.setQueryParameter("include_headers", "false");
  			Promise<Response> userInfoResponsePromise = userInfoRequest.post(content);
  			JsonNode userInfoResp = userInfoResponsePromise.get(5000).asJson();
  	  		JsonNode me = Json.parse(userInfoResp.get(0).get("body").asText());
  	  		JsonNode profilePicture = Json.parse(userInfoResp.get(1).get("body").asText()).get("data");
  	  		// Add to database
  	  		
  	  		Picture profilePic = new Picture(
  	  				profilePicture.get("url").asText(),50,50);
  	  		profilePic.save();
  	  		
  	  		User newUser = new User(
  	  				Long.parseLong(userId), 
  	  				me.get("first_name").asText(), 
  	  				me.get("gender").asText(),
  	  				profilePic.id);
  	  		newUser.save();
  		}
  		
  		ctx.args.put(ContextArgsKeys.USER_ID, userId);
  		ctx.args.put(ContextArgsKeys.ACCESS_TOKEN, accessToken);
  		ctx.args.put(ContextArgsKeys.APP_SECRET_PROOF, appSecretProof);
    	
    	return delegate.call(ctx);
    }
}
