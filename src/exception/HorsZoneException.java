package exception;

public class HorsZoneException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	public HorsZoneException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return ("HorsZoneException survenue " + this.message);
	}
}
