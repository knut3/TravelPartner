package exceptions;

public class AuthorizationException extends TravelPartnerException{

	private static final long serialVersionUID = 4L;
	
	public AuthorizationException(String reason) {
		super(reason);
	}

}
