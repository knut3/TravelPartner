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
import services.interfaces.IUserService;
import utils.ContextArgsKey;
import utils.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

public class Authorization extends Action<Authorization.Authorized>{
		
	@With(Authorization.class)
	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Authorized {
	}
	
	@Inject
	private IUserService userService;
		
    public Promise<Result> call(Http.Context ctx) throws Throwable {
		String authHeader = ctx.request().getHeader("Authorization");
		String accessToken = null;
		
		if(authHeader != null){
			authHeader = authHeader.trim();
			String authType = authHeader.substring(0, 6);
			if(!authType.equalsIgnoreCase("Bearer"))
				return Promise.<Result>pure(Results.unauthorized("Auth type should be Bearer"));
			accessToken = authHeader.substring(6).trim();
		}		
		else{
			String accessTokenParam = ctx.request().getQueryString("accessToken");
			if("".equals(accessTokenParam))
				return Promise.<Result>pure(Results.unauthorized("Missing Authorization header"));
			
			accessToken = accessTokenParam;
		}
			
		String appSecret = Play.application().configuration().getString("facebook.appSecret");
		String appSecretProof = Utils.generateAppSecretProof(accessToken, appSecret);
		
		WSRequestHolder idRequest = WS.url("https://graph.facebook.com/me");
		idRequest.setQueryParameter("access_token", accessToken);
		idRequest.setQueryParameter("appsecret_proof", appSecretProof);
		idRequest.setQueryParameter("fields", "id");
  		Promise<WSResponse> idResponsePromise = idRequest.get();
  		JsonNode idResp = idResponsePromise.get(5000).asJson();
  		if(!idResp.has("id"))
  			return Promise.<Result>pure(Results.unauthorized("Invalid access token"));
  		String userId = idResp.get("id").asText();
	  		
  		boolean userExists 
        = (User.find.where().idEq(userId).findRowCount() == 1) ? true : false;
  		
  		if(!userExists){
  			userService.createUser(accessToken, appSecretProof);  	  		
  		}
  		
  		ctx.args.put(ContextArgsKey.USER_ID, userId);
  		ctx.args.put(ContextArgsKey.ACCESS_TOKEN, accessToken);
  		ctx.args.put(ContextArgsKey.APP_SECRET_PROOF, appSecretProof);
    	
    	return delegate.call(ctx);
    }
}
