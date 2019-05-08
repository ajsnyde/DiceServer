package dice;

public class DieFactoryException extends RuntimeException {
	public DieFactoryException(String message) {
		super(message);
	}

	public DieFactoryException(String message, Throwable cause) {
		super(message, cause);
	}
}
