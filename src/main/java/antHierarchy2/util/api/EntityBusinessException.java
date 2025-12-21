package antHierarchy2.util.api;

import javax.ws.rs.core.Response.Status;

public abstract class EntityBusinessException extends CoffeeGenericException {

    protected EntityBusinessException(String message) {
        super(message);
        setHttpStatus(javax.ws.rs.core.Response.Status.BAD_REQUEST);
        setAppErrorCode(9999L);
    }

    protected EntityBusinessException(String messageTemplate, Object[] parameters) {
        super(messageTemplate, parameters, 9999L, javax.ws.rs.core.Response.Status.BAD_REQUEST);
    }
}