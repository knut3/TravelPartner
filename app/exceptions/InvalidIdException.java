package exceptions;

public class InvalidIdException extends TravelPartnerException{

	private static final long serialVersionUID = 4L;

	public InvalidIdException(String msg) {
		super(msg);
	}
	
}
