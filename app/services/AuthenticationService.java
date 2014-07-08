package services;

import play.mvc.Http.Request;
import services.interfaces.IAuthenticationService;
import exceptions.AuthenticationException;

public class AuthenticationService implements IAuthenticationService{

	@Override
	public String getAccessToken(Request request) throws AuthenticationException{
		String accessToken = null;
		String authHeader = request.getHeader("Authorization");
		if(authHeader != null){
			authHeader = authHeader.trim();
			String authType = authHeader.substring(0, 6);
			if(!authType.equalsIgnoreCase("Bearer"))
				throw new AuthenticationException("Auth type should be Bearer");
			accessToken = authHeader.substring(6).trim();
		}		
		else{
			String accessTokenParam = request.getQueryString("accessToken");
			if("".equals(accessTokenParam))
				throw new AuthenticationException("Missing Authorization header");
			
			accessToken = accessTokenParam;
		}
		
		return accessToken;
	}

	@Override
	public String refreshAccessToken(String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
