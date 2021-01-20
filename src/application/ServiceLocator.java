package application;

import java.util.Locale;
import java.util.logging.Logger;

public class ServiceLocator {
	private static ServiceLocator serviceLocator; // singleton

	// Application-global constants
	final private Class<?> APP_CLASS = Main.class;
	final private String APP_NAME = "Main";

	// Supported locales (for translations)
	final private Locale[] locales = new Locale[] { new Locale("en"), new Locale("de") };

	// Resources
	private Logger logger;
	private Configuration configuration;
	private Translator translator;


	public static ServiceLocator getServiceLocator() {
		if (serviceLocator == null)
			serviceLocator = new ServiceLocator();
		return serviceLocator;
	}

	
	private ServiceLocator() {

	}

	public Class<?> getAPP_CLASS() {
		return APP_CLASS;
	}

	public String getAPP_NAME() {
		return APP_NAME;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Locale[] getLocales() {
		return locales;
	}

	public Translator getTranslator() {
		return translator;
	}

	public void setTranslator(Translator translator) {
		this.translator = translator;
	}
}

