package antHierarchy2.util.api;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import antHierarchy2.Application;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ThrowableMapper implements ExceptionMapper<Throwable> {

	public static final Logger logger = LogManager.getLogger(Application.class);

    @Override
    public Response toResponse(Throwable ex) {

        // Log for server-side debugging
    	logger.error("Unhandled exception: " + ex.getMessage());

        StatusCode errorCode = new StatusCode(
            9999L,
            "An unexpected error occurred"
        );

        ApiResponse<Object> response = ApiResponse.error(
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
            Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            errorCode
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}