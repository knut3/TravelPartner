package exceptions;

public class TravelPartnerException extends Exception{

	private static final long serialVersionUID = -569629458905374913L;
	
	private String reason;
	
	public TravelPartnerException() {
		super();
		reason = "Something happened";
	}
	
	public TravelPartnerException(String reason) {
		super(reason);
		this.reason = reason;
	}
	
	
	@Override
	public String getMessage() {
		return reason;
	}
}
