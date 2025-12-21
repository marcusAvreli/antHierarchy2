package antHierarchy2;

import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.properties.PropertiesConfigurationBuilder;
import javax.servlet.http.HttpServletRequest;
public final class InternalContext {

    public void initApp(ServletContext context) {
      

        // Load application.properties from WEB-INF
        Properties appProperties = initSettings(context, "/WEB-INF/application.properties");
        if (appProperties == null) {
            System.err.println("application.properties not found!");
            return;
        }

        // Retrieve LOG4J file name
        String log4jPathName = appProperties.getProperty("LOG4J");
        if (log4jPathName != null) {
            // 🔧 Force to look under WEB-INF
            if (!log4jPathName.startsWith("/WEB-INF/")) {
                // Normalize both "/logging.properties" and "logging.properties"
                if (log4jPathName.startsWith("/"))
                    log4jPathName = "/WEB-INF" + log4jPathName;
                else
                    log4jPathName = "/WEB-INF/" + log4jPathName;
            }

            System.out.println("Loading log4j config from: " + log4jPathName);

            // Load logging.properties from WEB-INF
            Properties logProps = initSettings(context, log4jPathName);
            if (logProps != null) {
                LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
                Configuration config = new PropertiesConfigurationBuilder()
                        .setConfigurationSource(ConfigurationSource.NULL_SOURCE)
                        .setRootProperties(logProps)
                        .setLoggerContext(loggerContext)
                        .build();

                loggerContext.setConfiguration(config);
                Configurator.initialize(config);
            } else {
                System.err.println("Failed to load " + log4jPathName);
            }
        }
    }

    public Properties initSettings(ServletContext context, String resourcePath) {
        Properties props = new Properties();
        try (InputStream is = context.getResourceAsStream(resourcePath)) {
            if (is != null) {
                props.load(is);
                return props;
            } else {
                // fallback for exploded WAR
                String realPath = context.getRealPath(resourcePath);
                if (realPath != null) {
                    try (FileInputStream fis = new FileInputStream(realPath)) {
                        props.load(fis);
                        return props;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized InternalContext getInstance() {
        return ContextSingleton.INSTANCE;
    }

    private static class ContextSingleton {
        private static final InternalContext INSTANCE = new InternalContext();
    }

    private InternalContext() {}
}
