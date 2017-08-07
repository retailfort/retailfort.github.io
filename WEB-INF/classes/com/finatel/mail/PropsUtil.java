/**
 * 
 */
package com.finatel.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * @author ftuser
 *
 */
public class PropsUtil {

	private final Properties configProp = new Properties();

	private static PropsUtil propsUtil = null;

	/**
	 * 
	 */
	private PropsUtil() {

		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("application.properties");
			System.out.println("Read all properties from file");
			configProp.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static PropsUtil getInstance() {
		if (propsUtil == null) {
			propsUtil = new PropsUtil();
		}

		return propsUtil;
	}

	public String getProperty(String key) {
		return configProp.getProperty(key);
	}

	public Set<String> getAllPropertyNames() {
		return configProp.stringPropertyNames();
	}

	public boolean containsKey(String key) {
		return configProp.containsKey(key);
	}
}
