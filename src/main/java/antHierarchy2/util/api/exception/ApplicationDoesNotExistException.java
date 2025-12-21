package antHierarchy2.util.api.exception;

import javax.ws.rs.core.Response.Status;

import antHierarchy2.util.api.EntityBusinessException;



public class ApplicationDoesNotExistException extends EntityBusinessException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long APP_ERROR_CODE = 1002L;
    private static final String DEFAULT_MESSAGE = "Application does not exist";

    public ApplicationDoesNotExistException() {
        super(DEFAULT_MESSAGE);
        setAppErrorCode(APP_ERROR_CODE);
        setHttpStatus(Status.BAD_REQUEST);
    }

    public ApplicationDoesNotExistException(String message) {
        super(message);
        setAppErrorCode(APP_ERROR_CODE);
        setHttpStatus(Status.BAD_REQUEST);
    }

    public static ApplicationDoesNotExistException create() {
        return new ApplicationDoesNotExistException();
    }

    public static ApplicationDoesNotExistException create(String message) {
        return new ApplicationDoesNotExistException(message);
    }
}