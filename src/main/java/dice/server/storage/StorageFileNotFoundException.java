package dice.server.storage;

public class StorageFileNotFoundException extends StorageException {

	private static final long serialVersionUID = 7573420997286139625L;

	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}