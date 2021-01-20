package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;


public class Configuration {
	ServiceLocator sl = ServiceLocator.getServiceLocator(); 
	Logger logger = sl.getLogger();                          

	private Properties defaultOptions;
	private Properties localOptions;

	public Configuration() {
		
		defaultOptions = new Properties();
		String defaultFilename = sl.getAPP_NAME() + "_defaults.cfg";
		InputStream inStream = sl.getAPP_CLASS().getResourceAsStream(defaultFilename);
		try {
			defaultOptions.load(inStream);
			logger.config("Default configuration file found");
		} catch (Exception e) {
			logger.warning("No default configuration file found: " + defaultFilename);
		} finally {
			try {
				inStream.close();
			} catch (Exception ignore) {
			}
		}

		
		localOptions = new Properties(defaultOptions);

		
		try {
			inStream = new FileInputStream(sl.getAPP_NAME() + ".cfg");
			localOptions.load(inStream);
		} catch (FileNotFoundException e) { 
			logger.config("No local configuration file found");
		} catch (IOException e) { 
			logger.warning("Error reading local options file: " + e.toString());
		} finally {
			try {
				inStream.close();
			} catch (Exception ignore) {
			}
		}

		for (Enumeration<Object> i = localOptions.keys(); i.hasMoreElements();) {
			String key = (String) i.nextElement();
			logger.config("Option: " + key + " = " + localOptions.getProperty(key));
		}
	}

	public void save() {
		FileOutputStream propFile = null;
		try {
			propFile = new FileOutputStream(sl.getAPP_NAME() + ".cfg");
			localOptions.store(propFile, null);
			logger.config("Local configuration file saved");
		} catch (Exception e) {
			logger.warning("Unable to save local options: " + e.toString());
		} finally {
			if (propFile != null) {
				try {
					propFile.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public String getOption(String name) {
		return localOptions.getProperty(name);
	}

	public void setLocalOption(String name, String value) {
		localOptions.setProperty(name, value);
	}
}

