package antHierarchy2.util.api;

import javax.ws.rs.core.Response.Status;

public class CoffeeGenericException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String messageTemplate;
    private Object[] parameters;
    private long appErrorCode;
    private Status httpStatus;

    /**
     * Full constructor (message template + parameters + code + status)
     */
    public CoffeeGenericException(String messageTemplate, Object[] parameters, long appErrorCode, Status httpStatus) {
        super(messageTemplate);
        this.messageTemplate = messageTemplate;
        this.parameters = parameters;
        this.appErrorCode = appErrorCode;
        this.httpStatus = (httpStatus != null) ? httpStatus : Status.BAD_REQUEST;
    }

    /**
     * Simple constructors (for backward compatibility)
     */
    public CoffeeGenericException() {
        super();
        this.httpStatus = Status.BAD_REQUEST;
        this.appErrorCode = 9999L;
    }

    public CoffeeGenericException(String message) {
        super(message);
        this.httpStatus = Status.BAD_REQUEST;
        this.appErrorCode = 9999L;
    }

    public CoffeeGenericException(Throwable cause) {
        super(cause);
        this.httpStatus = Status.BAD_REQUEST;
        this.appErrorCode = 9999L;
    }

    public CoffeeGenericException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = Status.BAD_REQUEST;
        this.appErrorCode = 9999L;
    }

    // --- Getters & Setters ---
    public String getMessageTemplate() {
        return messageTemplate;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public long getAppErrorCode() {
        return appErrorCode;
    }

    public void setAppErrorCode(long appErrorCode) {
        this.appErrorCode = appErrorCode;
    }

    public Status getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Status httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        if (messageTemplate != null && parameters != null && parameters.length > 0) {
            try {
                return String.format(messageTemplate, parameters);
            } catch (Exception e) {
                // fallback if formatting fails
                return messageTemplate;
            }
        }
        return super.getMessage();
    }
}

