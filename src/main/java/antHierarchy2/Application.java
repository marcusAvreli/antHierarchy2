package antHierarchy2;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

import antHierarchy2.config.EntityManagerFactoryBinder;


public class Application  extends ResourceConfig {
	public static final Logger logger = LogManager.getLogger(Application.class);
	public Application(@Context ServletContext context ) {
		InternalContext.getInstance().initApp(context);		
	
		logger.info("application_started");
		
	
		context.getAttribute("ddd");
    	
    	
    	Enumeration<URL> urls;
		try {
			urls = Thread.currentThread().getContextClassLoader()
			        .getResources("META-INF/persistence.xml");
			while (urls.hasMoreElements()) {
				System.out.println(urls.nextElement());
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        packages("antHierarchy2.resources");
       
        packages("antHierarchy2.filter");
        
        packages("antHierarchy2.util.api");

       
        register(new EntityManagerFactoryBinder());
       
    }
}
