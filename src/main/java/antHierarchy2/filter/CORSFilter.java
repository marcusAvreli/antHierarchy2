package antHierarchy2.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

@Provider
public class CORSFilter implements ContainerRequestFilter,ContainerResponseFilter {
	private static final Logger logger = LoggerFactory.getLogger(CORSFilter.class);
	
	// preflight request method
		private static final String OPTIONS_METHOD = "OPTIONS";
		// preflight request headers
		private static final String ORIGIN = "Origin";
		private static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
		private static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
		// preflight response headers
		private static final String VARY = "Vary";
		private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
		private static final String ACCESS_CONTROL_ALLOW_METHOD = "Access-Control-Allow-Method";
		private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
		  private static final String ALLOWED_HEADERS = "Origin, Content-Type, Accept, Authorization";
		    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
		    private static final String ALLOWED_ORIGIN = "*"; // or your frontend domain
		@Override
	public void filter(ContainerRequestContext reqContext) {
			logger.info("filter_start");
			if ("OPTIONS".equalsIgnoreCase(reqContext.getMethod())) {
	            Response response = Response.ok()
	                    .header("Access-Control-Allow-Origin", ALLOWED_ORIGIN)
	                    .header("Access-Control-Allow-Headers", ALLOWED_HEADERS)
	                    .header("Access-Control-Allow-Methods", ALLOWED_METHODS)
	                    .build();
	            reqContext.abortWith(response); // stops request here
	        }
				logger.info("filter_end");
	}
	
    public void filter(ContainerRequestContext reqContext, ContainerResponseContext responseContext) throws IOException {
    	logger.info("filter_1");
    	  if (!"OPTIONS".equalsIgnoreCase(reqContext.getMethod())) {
              responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
             responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", ALLOWED_HEADERS);
              responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", ALLOWED_METHODS);
              responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
              responseContext.getHeaders().putSingle("Access-Control-Max-Age", "3600");
          }
    }

		
}