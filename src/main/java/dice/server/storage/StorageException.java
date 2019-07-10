package dice.server.storage;

public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 5156793429852850131L;

	public StorageException(String message) {
		super(message);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
