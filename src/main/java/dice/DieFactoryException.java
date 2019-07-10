package dice;

public class DieFactoryException extends RuntimeException {

	private static final long serialVersionUID = 8793152380042583325L;

	public DieFactoryException(String message) {
		super(message);
	}

	public DieFactoryException(String message, Throwable cause) {
		super(message, cause);
	}
}
