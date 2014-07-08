package services.interfaces;

import play.mvc.Http.Request;
import exceptions.AuthenticationException;

public interface IAuthenticationService {
	
	public String getAccessToken(Request request) throws AuthenticationException;

	public String refreshAccessToken(String accessToken);
	
}
