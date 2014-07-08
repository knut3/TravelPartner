package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import models.User;
import play.Play;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;
import services.interfaces.IAuthenticationService;
import services.interfaces.IUserService;
import utils.ContextArgsKey;
import utils.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

public class Authentication extends Action<Authentication.RequiresAuthentication>{
		
	@With(Authentication.class)
	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RequiresAuthentication {
	}
	
	@Inject
	private IUserService userService;
	@Inject
	private IAuthenticationService authService;
		
    public Promise<Result> call(Http.Context ctx) throws Throwable {
		String authHeader = ctx.request().getHeader("Authorization");
		final String accessToken = authService.getAccessToken(ctx.request());
		String appSecret = Play.application().configuration().getString("facebook.appSecret");
		String appSecretProof = Utils.generateAppSecretProof(accessToken, appSecret);
		WSRequestHolder idRequest = WS.url("https://graph.facebook.com/me");
		idRequest.setQueryParameter("access_token", accessToken);
		idRequest.setQueryParameter("appsecret_proof", appSecretProof);
		idRequest.setQueryParameter("fields", "id");
  		Promise<WSResponse> idResponsePromise = idRequest.get();
  		
  		return idResponsePromise.flatMap( idResp -> {
  			
  			JsonNode idRespJson = idResp.asJson();
  			
  	  		if(!idRespJson.has("id"))
  	  			return Promise.<Result>pure(Results.unauthorized("Invalid access token"));
  	  		String userId = idRespJson.get("id").asText();
  		  		
  	  		ctx.args.put(ContextArgsKey.USER_ID, userId);
  	  		ctx.args.put(ContextArgsKey.ACCESS_TOKEN, accessToken);
  	  		ctx.args.put(ContextArgsKey.APP_SECRET_PROOF, appSecretProof);
  	    	  	
  	  		boolean userExists 
	        = (User.find.where().idEq(userId).findRowCount() == 1) ? true : false;
  	  		
  	  		if(!userExists){
	  			Promise<Result> result = userService.createUser(accessToken, appSecretProof, Long.parseLong(userId)).flatMap( nothing -> {
	  				return delegate.call(ctx);
	  			} );
	  			return result;
	  		}
  	  		else{
//  	  			int expires = idRespJson.get("expires").asInt();
//  	  			if(expires < AppSettings.ACCESS_TOKEN_REFRESH_TRESHOLD){
//  	  				String newAccessToken = authService.refreshAccessToken(accessToken);
//  					ctx.response().setHeader(Http.HeaderNames.AUTHORIZATION, newAccessToken);
//  	  			}
  	  			return delegate.call(ctx);
  	  		}
  		}
  		);
    }
}
