package antHierarchy2.util.api.exception;

import javax.ws.rs.core.Response.Status;

import antHierarchy2.util.api.EntityBusinessException;



public class UnexpectedException extends EntityBusinessException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long APP_ERROR_CODE = 1004L;
    private static final String DEFAULT_MESSAGE = "Unexpected error while creating report";

    public UnexpectedException() {
        super(DEFAULT_MESSAGE);
        setAppErrorCode(APP_ERROR_CODE);
        setHttpStatus(Status.BAD_REQUEST);
    }

    public UnexpectedException(String message) {
        super(message);
        setAppErrorCode(APP_ERROR_CODE);
        setHttpStatus(Status.BAD_REQUEST);
    }

    public static UnexpectedException create() {
        return new UnexpectedException();
    }

    public static UnexpectedException create(String message) {
        return new UnexpectedException(message);
    }
}