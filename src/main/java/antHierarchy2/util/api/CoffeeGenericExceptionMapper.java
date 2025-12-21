package antHierarchy2.util.api;

import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class CoffeeGenericExceptionMapper implements ExceptionMapper<CoffeeGenericException> {

    @Override
    public Response toResponse(CoffeeGenericException ex) {

        StatusCode errorCode = new StatusCode(
            ex.getAppErrorCode(),
            ex.getMessage()
        );

        ApiResponse<Object> response = ApiResponse.error(
            ex.getHttpStatus().getStatusCode(),
            ex.getHttpStatus().getReasonPhrase(),
            errorCode
        );

        return Response.status(ex.getHttpStatus())
                       .entity(response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
