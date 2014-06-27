package exceptions;

public class AuthenticationException extends TravelPartnerException{

	public AuthenticationException(String reason) {
		super(reason);
	}
	
	private static final long serialVersionUID = 4L;

}
