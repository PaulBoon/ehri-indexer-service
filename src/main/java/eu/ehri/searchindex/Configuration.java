package eu.ehri.searchindex;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
    private static Logger logger = LoggerFactory.getLogger(Configuration.class);
	private Properties props = new Properties();
	public static final String CONFIG_FILE = "config.properties";
	
	public Properties getProps() {
		return props;
	}

	public Configuration() {
		load();
	}

	/**
	 * Load the properties from a file
	 */
	private void load() {
		try {
			// load a properties file from class path
			props.load(getClass().getClassLoader().getResourceAsStream(
					CONFIG_FILE));
			// show config
			show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Show all the properties key/name and value
	 */
	public void show() {
		Iterator<Object> it = props.keySet().iterator();
	 
		logger.info("----------------------");
		logger.info("Configuration from file: " + CONFIG_FILE);
		logger.info("----------------------");
	    while (it.hasNext()) {          
	        //propertyName = (String) it.next();
	        String propertyName = (String) it.next();
	        String propertyValue = props.getProperty(propertyName);
	        logger.info(propertyName + " = " + propertyValue);
	    }
		logger.info("----------------------");
	}
	    
}
