package com.gsd.pos.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Config {
	private static final Logger logger = Logger.getLogger(Config.class
			.getName());
	private static boolean initialized = false;
	private static Properties props;

	private Config() {
	}

	public static Properties getProperties() {
		if (!initialized) {
			init();
		}
		return props;
	}

	public static String getProperty(String key) {
		return getProperty(key, null);
	}

	public static String getProperty(String key, String defValue) {
		if (!initialized) {
			init();
		}
		return props.getProperty(key, defValue);
	}

	public static int getIntProperty(String key, int defValue) {
		if (!initialized) {
			init();
		}
		String value = props.getProperty(key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			return defValue;
		}
	}

	private static void init() {
		props = new Properties();
		String path = System.getProperty("posman.home" , "/home/sdabre/workspace/posman1");
		try {
			File propsFile = new File(path + File.separator + "posman.properties");
			if (propsFile.exists()) {
				logger.trace("loading file from [" + propsFile.getAbsolutePath() + "]" );
				props.load(new FileInputStream(propsFile));
				initialized = true;
			}
		} catch (IOException ex) {
			logger.warn(ex);
			logger.warn(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Config ");
		Enumeration names = props.propertyNames();
		while (names.hasMoreElements()) {
			String key = (String) names.nextElement();
			sb.append("\t" + key + "\t" + props.getProperty(key));
		}
		return sb.toString();
	}
}
