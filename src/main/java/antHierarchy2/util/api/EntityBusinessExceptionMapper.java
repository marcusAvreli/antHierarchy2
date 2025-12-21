package antHierarchy2.util.api;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;




@Provider
@Produces(MediaType.APPLICATION_JSON)

public class EntityBusinessExceptionMapper implements ExceptionMapper<EntityBusinessException> {

    @Override
    public Response toResponse(EntityBusinessException ex) {
       StatusCode errorCode = new StatusCode();
        errorCode.setErrorCode(ex.getAppErrorCode());
        errorCode.setErrorMessage(ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
            ex.getHttpStatus().getStatusCode(),
            ex.getHttpStatus().getReasonPhrase(),
            errorCode
        );

        return Response.status(ex.getHttpStatus().getStatusCode())
                       .entity(response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}

