package antHierarchy2.util.api.exception;

import javax.ws.rs.core.Response.Status;

import antHierarchy2.util.api.EntityBusinessException;



public class DatabaseException extends EntityBusinessException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long APP_ERROR_CODE = 1003L;
    private static final String DEFAULT_MESSAGE = "Application does not exist";

    public DatabaseException() {
        super(DEFAULT_MESSAGE);
        setAppErrorCode(APP_ERROR_CODE);
        setHttpStatus(Status.BAD_REQUEST);
    }

    public DatabaseException(String message) {
        super(message);
        setAppErrorCode(APP_ERROR_CODE);
        setHttpStatus(Status.BAD_REQUEST);
    }

    public static DatabaseException create() {
        return new DatabaseException();
    }

    public static DatabaseException create(String message) {
        return new DatabaseException(message);
    }
}

